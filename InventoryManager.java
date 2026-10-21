import java.util.ArrayList;
import java.util.List;

/** Owns the inventory. No database is needed for this class project. */
public class InventoryManager {
    private final ArrayList<Device> devices = new ArrayList<>();

    public boolean addDevice(Device device) {
        if (device == null || findByTag(device.getAssetTag()) != null) {
            return false; // prevent duplicate tags
        }
        return devices.add(device);
    }

    public boolean removeDevice(String tag) {
        Device found = findByTag(tag);
        return found != null && devices.remove(found);
    }

    /** Recursive search demonstrates the recursion course objective. */
    public Device findByTag(String tag) {
        return findByTag(tag, 0);
    }

    private Device findByTag(String tag, int index) {
        if (tag == null || index >= devices.size()) {
            return null; // base case
        }
        Device current = devices.get(index);
        if (current.getAssetTag().equalsIgnoreCase(tag.trim())) {
            return current;
        }
        return findByTag(tag, index + 1); // recursive step
    }

    /** Search strings using a loop and conditional checks. */
    public List<Device> search(String query) {
        String text = query == null ? "" : query.trim().toLowerCase();
        ArrayList<Device> matches = new ArrayList<>();
        for (Device device : devices) {
            if (device.getAssetTag().toLowerCase().contains(text)
                    || device.getManufacturer().toLowerCase().contains(text)
                    || device.getModel().toLowerCase().contains(text)
                    || device.getDeviceType().toLowerCase().contains(text)) {
                matches.add(device);
            }
        }
        return matches;
    }

    public List<Device> getDevices() {
        return new ArrayList<>(devices);
    }
}
