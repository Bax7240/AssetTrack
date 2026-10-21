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
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setPlaceholder(new Label("No devices match your search."));

        search.setPromptText("Search by tag, manufacturer, model, or type");
        search.textProperty().addListener((obs, oldValue, newValue) -> refresh());

        Button add = new Button("Add Device");
        Button update = new Button("Update Status");
        Button remove = new Button("Remove Selected");
        add.setOnAction(e -> addDevice());
        update.setOnAction(e -> updateStatus());
        remove.setOnAction(e -> removeDevice());

        HBox buttons = new HBox(8, add, update, remove);
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
    }

    private void addDevice() {
        Dialog<Device> dialog = new Dialog<>();
        dialog.setTitle("Add Device");
        ButtonType addButton = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButton, ButtonType.CANCEL);

        TextField tag = new TextField();
        TextField manufacturer = new TextField();
        TextField model = new TextField();
        TextField assigned = new TextField();
        ComboBox<String> type = new ComboBox<>(FXCollections.observableArrayList("Desktop", "Laptop"));
        type.setValue("Desktop");
        ComboBox<String> status = new ComboBox<>(FXCollections.observableArrayList(
                "Available", "Deployed", "In Repair", "Retired"));
        status.setValue("Available");

        GridPane fields = new GridPane();
        fields.setHgap(10);
        fields.setVgap(10);
        fields.setPadding(new Insets(12));
        String[] labels = {"Asset tag", "Manufacturer", "Model", "Assigned to", "Type", "Status"};
        Control[] inputs = {tag, manufacturer, model, assigned, type, status};
        for (int i = 0; i < labels.length; i++) {
            fields.add(new Label(labels[i]), 0, i);
            fields.add(inputs[i], 1, i);
        }
        dialog.getDialogPane().setContent(fields);
        dialog.setResultConverter(button -> {
            if (button != addButton) return null;
            try {
                if ("Laptop".equals(type.getValue())) {
                    return new Laptop(tag.getText(), manufacturer.getText(), model.getText(),
                            status.getValue(), assigned.getText(), 14);
                }
                return new Device(tag.getText(), manufacturer.getText(), model.getText(),
                        status.getValue(), assigned.getText());
            } catch (IllegalArgumentException ex) {
                showError(ex.getMessage());
                return null;
            }
        });
        dialog.showAndWait().ifPresent(device -> {
            if (!inventory.addDevice(device)) {
                showError("That asset tag already exists.");
            }
            refresh();
        });
    }

    private void updateStatus() {
        Device selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Select a device first.");
            return;
        }
        ChoiceDialog<String> dialog = new ChoiceDialog<>(selected.getStatus(),
                "Available", "Deployed", "In Repair", "Retired");
        dialog.setTitle("Update Status");
        dialog.setHeaderText(selected.getAssetTag());
        dialog.setContentText("New status:");
        dialog.showAndWait().ifPresent(value -> {
            selected.setStatus(value);
            refresh();
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
