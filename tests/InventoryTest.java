import java.util.Locale;

/** Dependency-free regression checks; run with assertions enabled. */
public class InventoryTest {
    public static void main(String[] args) {
        InventoryManager inventory = new InventoryManager();
        Device desktop = new Device(" TAG001 ", "Dell", "OptiPlex", "Available", null);
        Device laptop = new Laptop("TAG002", "Lenovo", "ThinkPad", "In Repair", "Student", 14);
        assert inventory.addDevice(desktop);
        assert inventory.addDevice(laptop);
        assert !inventory.addDevice(null);
        assert !inventory.addDevice(new Device("tag001", "", "", "Available", ""));
        assert inventory.findByTag(" tag002 ") == laptop;
        assert inventory.findByTag(null) == null;
        assert inventory.findByTag("missing") == null;
        assert inventory.search(null).size() == 2;
        assert inventory.search("laptop").get(0) == laptop;
        assert inventory.search("DELL").get(0) == desktop;
        assert inventory.search("missing").isEmpty();
        inventory.getDevices().clear();
        assert inventory.getDevices().size() == 2;
        desktop.setStatus("Deployed");
        desktop.setAssignedTo(" Scott ");
        assert desktop.getStatus().equals("Deployed");
        assert desktop.getAssignedTo().equals("Scott");
        invalid(() -> desktop.setStatus("invalid"));
        assert desktop.getStatus().equals("Deployed");
        invalid(() -> new Device(" ", "", "", "Available", ""));
        for (double size : new double[]{0, -1, Double.NaN, Double.POSITIVE_INFINITY}) {
            invalid(() -> new Laptop("X", "", "", "Available", "", size));
        }
        Locale previous = Locale.getDefault();
        try {
            Locale.setDefault(Locale.forLanguageTag("tr-TR"));
            assert inventory.search("THINKPAD").get(0) == laptop;
        } finally { Locale.setDefault(previous); }
        assert inventory.removeDevice("tag002");
        assert !inventory.removeDevice("tag002");
        assert inventory.findByTag("TAG002") == null;
        Device renamed = new Laptop("NEW001", "HP", "EliteBook", "Retired", "Alex", 15.6);
        assert !inventory.updateDevice("missing", renamed);
        assert !inventory.updateDevice("TAG001", null);
        assert inventory.updateDevice("TAG001", renamed);
        assert inventory.findByTag("TAG001") == null;
        assert inventory.findByTag("NEW001") == renamed;
        assert inventory.search("EliteBook").get(0) == renamed;
        assert ((Laptop) renamed).getScreenSize() == 15.6;
        assert renamed.getStatus().equals("Retired");
        assert renamed.getAssignedTo().equals("Alex");
        assert inventory.addDevice(desktop);
        assert !inventory.updateDevice("NEW001", new Device("tag001", "", "", "Available", ""));
        assert inventory.findByTag("NEW001") == renamed;
        Device converted = new Device("new001", "Dell", "OptiPlex", "Available", "");
        assert inventory.updateDevice("NEW001", converted);
        assert inventory.findByTag("NEW001").getDeviceType().equals("Desktop");
        assert inventory.removeDevice("NEW001");
        assert inventory.removeDevice("TAG001");
        assert inventory.getDevices().isEmpty();
        System.out.println("All Lite inventory checks passed.");
    }
    private static void invalid(Runnable action) {
        try { action.run(); } catch (IllegalArgumentException expected) { return; }
        throw new AssertionError("Expected invalid input to be rejected");
    }
}
