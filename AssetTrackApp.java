import javafx.application.Application;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

/**
 * One-window JavaFX inventory demo for CSI 2300.
 * Inventory is in memory only; it resets when the application closes.
 */
public class AssetTrackApp extends Application {
    private final InventoryManager inventory = new InventoryManager();
    private final TableView<Device> table = new TableView<>();
    private final TextField search = new TextField();

    @Override
    public void start(Stage stage) {
        // Array and loop: create some sample devices for the demo.
        String[][] examples = {
            {"TAG001", "Dell", "OptiPlex", "Available", ""},
            {"TAG002", "HP", "EliteBook", "Deployed", "Student"}
        };
        for (String[] row : examples) {
            inventory.addDevice(new Device(row[0], row[1], row[2], row[3], row[4]));
        }
        inventory.addDevice(new Laptop("TAG003", "Lenovo", "ThinkPad", "In Repair", "", 14));

        addColumn("Asset Tag", Device::getAssetTag);
        addColumn("Type", Device::getDeviceType); // runtime polymorphism
        addColumn("Manufacturer", Device::getManufacturer);
        addColumn("Model", Device::getModel);
        addColumn("Status", Device::getStatus);
        addColumn("Assigned To", Device::getAssignedTo);
        addColumn("Screen Size", device -> device instanceof Laptop
                ? ((Laptop) device).getScreenSize() + " inches" : "");
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setPlaceholder(new Label("No devices match your search."));

        search.setPromptText("Search by tag, manufacturer, model, or type");
        search.textProperty().addListener((obs, oldValue, newValue) -> refresh());

        Button add = new Button("Add Device");
        Button edit = new Button("Edit Device");
        Button remove = new Button("Remove Selected");
        add.setOnAction(e -> addDevice());
        edit.setOnAction(e -> editDevice());
        remove.setOnAction(e -> removeDevice());

        HBox buttons = new HBox(8, add, edit, remove);
        VBox root = new VBox(10, new Label("AssetTrack Lite"), search, table, buttons,
                new Label("Demo inventory is not saved when you exit."));
        root.setPadding(new Insets(15));
        VBox.setVgrow(table, Priority.ALWAYS);
        refresh();

        stage.setTitle("AssetTrack Lite");
        stage.setScene(new Scene(root, 900, 440));
        stage.show();
    }

    private void addColumn(String heading, java.util.function.Function<Device, String> value) {
        TableColumn<Device, String> column = new TableColumn<>(heading);
        column.setCellValueFactory(data ->
                new ReadOnlyStringWrapper(value.apply(data.getValue())));
        table.getColumns().add(column);
    }

    private void refresh() {
        table.setItems(FXCollections.observableArrayList(inventory.search(search.getText())));
        // Device uses plain Java fields, so existing cells need to reread their values.
        table.refresh();
    }

    private void addDevice() {
        showDeviceForm(null);
    }

    private void editDevice() {
        Device selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Select a device first.");
            return;
        }
        showDeviceForm(selected);
    }

    /** Shared form: edits are applied only after successful validation and Save. */
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
        ComboBox<String> type = new ComboBox<>(FXCollections.observableArrayList("Desktop", "Laptop"));
        type.setValue(editing ? original.getDeviceType() : "Desktop");
        ComboBox<String> status = new ComboBox<>(FXCollections.observableArrayList(
                "Available", "Deployed", "In Repair", "Retired"));
        status.setValue(editing ? original.getStatus() : "Available");
        TextField screen = new TextField(original instanceof Laptop
                ? Double.toString(((Laptop) original).getScreenSize()) : "14");
        screen.disableProperty().bind(type.valueProperty().isNotEqualTo("Laptop"));

        GridPane fields = new GridPane();
        fields.setHgap(10);
        fields.setVgap(10);
        fields.setPadding(new Insets(12));
        String[] labels = {"Asset tag", "Manufacturer", "Model", "Assigned to", "Type", "Status", "Screen size (inches)"};
        Control[] inputs = {tag, manufacturer, model, assigned, type, status, screen};
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
                        if ("Laptop".equals(type.getValue())) {
                            double size;
                            try { size = Double.parseDouble(screen.getText().trim()); }
                            catch (NumberFormatException ex) {
                                throw new IllegalArgumentException("Enter a numeric screen size in inches.");
                            }
                            validated[0] = new Laptop(tag.getText(), manufacturer.getText(), model.getText(),
                                    status.getValue(), assigned.getText(), size);
                        } else {
                            validated[0] = new Device(tag.getText(), manufacturer.getText(), model.getText(),
                                    status.getValue(), assigned.getText());
                        }
                    } catch (IllegalArgumentException ex) {
                        event.consume();
                        showError(ex.getMessage());
                    }
                });
        dialog.setResultConverter(button -> button == saveButton ? validated[0] : null);
        dialog.showAndWait().ifPresent(device -> {
            boolean saved = editing ? inventory.updateDevice(original.getAssetTag(), device)
                    : inventory.addDevice(device);
            if (!saved) showError("Unable to save: the asset tag is already in use or the device was removed.");
            refresh();
            if (saved && table.getItems().contains(device)) {
                table.getSelectionModel().select(device);
            }
        });
    }

    private void removeDevice() {
        Device selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Select a device first.");
            return;
        }
        inventory.removeDevice(selected.getAssetTag());
        refresh();
    }

    private void showError(String message) {
        new Alert(Alert.AlertType.WARNING, message, ButtonType.OK).showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
