import javafx.application.Application;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Function;

/**
 * One-window JavaFX inventory app for CSI 2300.
 * Inventory is saved automatically to a CSV file in the user's home folder
 * after every change and loaded again on the next start.
 */
public class AssetTrackApp extends Application {
    private static final Path SAVE_FILE = Path.of(System.getProperty("user.home"), "assettrack-inventory.csv");

    private final InventoryManager inventory = new InventoryManager();
    private final TableView<Device> table = new TableView<>();
    private final TextField search = new TextField();
    private final Label statusBar = new Label();

    @Override
    public void start(Stage stage) {
        loadInventory();

        addColumn("Asset Tag", Device::getAssetTag);
        addColumn("Type", Device::getDeviceType);           // runtime polymorphism
        addColumn("Manufacturer", Device::getManufacturer);
        addColumn("Model", Device::getModel);
        addColumn("Status", d -> d.getStatus().toString());
        addColumn("Assigned To", Device::getAssignedTo);
        addColumn("Location", Device::getLocation);
        addColumn("Details", Device::getDetails);           // runtime polymorphism, no instanceof needed
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setPlaceholder(new Label("No devices match your search."));

        // Double-click a row to edit it.
        table.setRowFactory(tv -> {
            TableRow<Device> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2 && !row.isEmpty()) {
                    showDeviceForm(row.getItem());
                }
            });
            return row;
        });

        search.setPromptText("Search any field: tag, model, status, assignee, location...");
        search.textProperty().addListener((obs, oldValue, newValue) -> refresh());

        Button add = new Button("Add Device");
        Button edit = new Button("Edit Device");
        Button remove = new Button("Remove Selected");
        add.setOnAction(e -> showDeviceForm(null));
        edit.setOnAction(e -> editDevice());
        remove.setOnAction(e -> removeDevice());

        HBox buttons = new HBox(8, add, edit, remove);
        VBox root = new VBox(10, new Label("AssetTrack"), search, table, buttons, statusBar);
        root.setPadding(new Insets(15));
        VBox.setVgrow(table, Priority.ALWAYS);
        refresh();

        stage.setTitle("AssetTrack");
        stage.setScene(new Scene(root, 1000, 480));
        stage.show();
    }

    // ------------------------------------------------------------ Save / Load

    /** Loads the saved CSV, or starts with sample devices on the first run. */
    private void loadInventory() {
        if (Files.exists(SAVE_FILE)) {
            try {
                int skipped = inventory.loadFromFile(SAVE_FILE);
                if (skipped > 0) {
                    showError(skipped + " row(s) in the save file could not be read and were skipped.");
                }
                return;
            } catch (IOException ex) {
                showError("Could not read " + SAVE_FILE + ": " + ex.getMessage()
                        + "\nStarting with sample data instead.");
            }
        }
        addSampleDevices();
    }

    private void addSampleDevices() {
        // Array and loop: create some sample devices for the first run.
        String[][] examples = {
            {"Desktop", "TAG001", "Dell", "OptiPlex 7090", "Available", "", "Tech Center", ""},
            {"Laptop", "TAG002", "Dell", "Latitude 5440", "Deployed", "Student", "Kresge Library", "14"},
            {"Laptop", "TAG003", "Lenovo", "ThinkPad T14", "In Repair", "", "Tech Center", "14"}
        };
        for (String[] r : examples) {
            inventory.addDevice(InventoryManager.createDevice(
                    r[0], r[1], r[2], r[3], Status.fromText(r[4]), r[5], r[6], r[7]));
        }
    }

    /** Called after every add, edit, and remove so nothing is lost when the app closes. */
    private void saveInventory() {
        try {
            inventory.saveToFile(SAVE_FILE);
        } catch (IOException ex) {
            showError("Could not save to " + SAVE_FILE + ": " + ex.getMessage());
        }
    }

    // ------------------------------------------------------------------ Table

    private void addColumn(String heading, Function<Device, String> value) {
        TableColumn<Device, String> column = new TableColumn<>(heading);
        column.setCellValueFactory(data -> new ReadOnlyStringWrapper(value.apply(data.getValue())));
        table.getColumns().add(column);
    }

    private void refresh() {
        table.setItems(FXCollections.observableArrayList(inventory.search(search.getText())));
        // Device uses plain Java fields, so existing cells need to reread their values.
        table.refresh();
        statusBar.setText("Showing " + table.getItems().size() + " of " + inventory.size()
                + " devices. Saved automatically to " + SAVE_FILE);
    }

    // ---------------------------------------------------------------- Actions

    private void editDevice() {
        Device selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Select a device first.");
            return;
        }
        showDeviceForm(selected);
    }

    private void removeDevice() {
        Device selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Select a device first.");
            return;
        }
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Remove " + selected + " from the inventory?", ButtonType.OK, ButtonType.CANCEL);
        confirm.setHeaderText("Remove device");
        confirm.showAndWait()
                .filter(button -> button == ButtonType.OK)
                .ifPresent(button -> {
                    inventory.removeDevice(selected.getAssetTag());
                    saveInventory();
                    refresh();
                });
    }

    /** Shared Add/Edit form: changes are applied only after successful validation and Save. */
    private void showDeviceForm(Device original) {
        boolean editing = original != null;
        Dialog<Device> dialog = new Dialog<>();
        dialog.setTitle(editing ? "Edit Device" : "Add Device");
        dialog.initOwner(table.getScene().getWindow());
        ButtonType saveButton = new ButtonType(editing ? "Save" : "Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButton, ButtonType.CANCEL);

        TextField tag = new TextField(editing ? original.getAssetTag() : "");
        TextField manufacturer = new TextField(editing ? original.getManufacturer() : "");
        TextField model = new TextField(editing ? original.getModel() : "");
        TextField assigned = new TextField(editing ? original.getAssignedTo() : "");
        TextField location = new TextField(editing ? original.getLocation() : "");
        ComboBox<String> type = new ComboBox<>(FXCollections.observableArrayList("Desktop", "Laptop"));
        type.setValue(editing ? original.getDeviceType() : "Desktop");
        ComboBox<Status> status = new ComboBox<>(FXCollections.observableArrayList(Status.values()));
        status.setValue(editing ? original.getStatus() : Status.AVAILABLE);
        // getExtraValue() is polymorphic: a Laptop returns its screen size, a Desktop returns "".
        String extra = editing ? original.getExtraValue() : "";
        TextField screen = new TextField(extra.isBlank() ? "14" : extra);
        screen.disableProperty().bind(type.valueProperty().isNotEqualTo("Laptop"));

        GridPane fields = new GridPane();
        fields.setHgap(10);
        fields.setVgap(10);
        fields.setPadding(new Insets(12));
        String[] labels = {"Asset tag", "Manufacturer", "Model", "Assigned to", "Location",
                "Type", "Status", "Screen size (inches)"};
        Control[] inputs = {tag, manufacturer, model, assigned, location, type, status, screen};
        for (int i = 0; i < labels.length; i++) {
            Label label = new Label(labels[i]);
            label.setLabelFor(inputs[i]);
            fields.add(label, 0, i);
            fields.add(inputs[i], 1, i);
        }
        dialog.getDialogPane().setContent(fields);

        // Build a validated replacement without mutating the original on Cancel or error.
        final Device[] validated = new Device[1];
        dialog.getDialogPane().lookupButton(saveButton).addEventFilter(
                javafx.event.ActionEvent.ACTION, event -> {
                    try {
                        Device duplicate = inventory.findByTag(tag.getText());
                        if (duplicate != null && duplicate != original) {
                            throw new IllegalArgumentException("That asset tag already exists.");
                        }
                        validated[0] = InventoryManager.createDevice(type.getValue(), tag.getText(),
                                manufacturer.getText(), model.getText(), status.getValue(),
                                assigned.getText(), location.getText(), screen.getText());
                    } catch (IllegalArgumentException ex) {
                        event.consume(); // keep the dialog open so the user can fix the input
                        showError(ex.getMessage());
                    }
                });
        dialog.setResultConverter(button -> button == saveButton ? validated[0] : null);

        dialog.showAndWait().ifPresent(device -> {
            boolean saved = editing ? inventory.updateDevice(original.getAssetTag(), device)
                    : inventory.addDevice(device);
            if (saved) {
                saveInventory();
            } else {
                showError("Unable to save: the asset tag is already in use or the device was removed.");
            }
            refresh();
            if (saved && table.getItems().contains(device)) {
                table.getSelectionModel().select(device);
            }
        });
    }

    private void showError(String message) {
        new Alert(Alert.AlertType.WARNING, message, ButtonType.OK).showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
