import java.nio.file.Files;
import java.nio.file.Path;

/** Console demonstration of the model and CSV save/load, without JavaFX. */
public class DeviceDemo {
    public static void main(String[] args) throws Exception {
        InventoryManager inventory = new InventoryManager();
        inventory.addDevice(new Desktop("TAG001", "Dell", "OptiPlex 7090", Status.AVAILABLE, "", "Kresge Library"));
        inventory.addDevice(new Laptop("TAG002", "Dell", "Latitude 5440", Status.AVAILABLE, "", "Tech Center", 14));

        Device selected = inventory.findByTag("tag002"); // recursive, case-insensitive
        selected.setStatus(Status.DEPLOYED);
        selected.setAssignedTo("Student");

        System.out.println("All devices:");
        for (Device device : inventory.getDevices()) {
            // getDetails() runs the Desktop or Laptop version depending on the object (polymorphism)
            System.out.println("  " + device + " | " + device.getStatus() + " | "
                    + device.getAssignedTo() + " | " + device.getLocation() + " | " + device.getDetails());
        }

        System.out.println("Search \"deployed\": " + inventory.search("deployed"));
        System.out.println("Search \"library\": " + inventory.search("library"));

        Path file = Files.createTempFile("assettrack-demo", ".csv");
        inventory.saveToFile(file);
        InventoryManager reloaded = new InventoryManager();
        int skipped = reloaded.loadFromFile(file);
        System.out.println("Reloaded " + reloaded.size() + " devices from CSV (" + skipped + " skipped).");
        Files.deleteIfExists(file);
    }
}
