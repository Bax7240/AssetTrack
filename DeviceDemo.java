/** Console demonstration of the Lite model, without JavaFX. */
public class DeviceDemo {
    public static void main(String[] args) {
        InventoryManager inventory = new InventoryManager();
        inventory.addDevice(new Device("TAG001", "Dell", "OptiPlex", "Available", ""));
        inventory.addDevice(new Laptop("TAG002", "Lenovo", "ThinkPad", "Available", "", 14));
        Device selected = inventory.findByTag("TAG002");
        selected.setStatus("Deployed");
        selected.setAssignedTo("Student");
        for (Device device : inventory.getDevices()) {
            System.out.println(device + " | " + device.getStatus() + " | " + device.getAssignedTo());
        }
    }
}
