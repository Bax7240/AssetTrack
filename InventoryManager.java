import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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

    /** Replace a row atomically, allowing tag changes and conversion between types. */
    public boolean updateDevice(String originalTag, Device replacement) {
        Device original = findByTag(originalTag);
        if (original == null || replacement == null) return false;
        Device duplicate = findByTag(replacement.getAssetTag());
        if (duplicate != null && duplicate != original) return false;
        devices.set(devices.indexOf(original), replacement);
        return true;
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
        String text = query == null ? "" : query.trim().toLowerCase(Locale.ROOT);
        ArrayList<Device> matches = new ArrayList<>();
        for (Device device : devices) {
            if (device.getAssetTag().toLowerCase(Locale.ROOT).contains(text)
                    || device.getManufacturer().toLowerCase(Locale.ROOT).contains(text)
                    || device.getModel().toLowerCase(Locale.ROOT).contains(text)
                    || device.getDeviceType().toLowerCase(Locale.ROOT).contains(text)) {
                matches.add(device);
            }
        }
        return matches;
    }

    public List<Device> getDevices() {
        return new ArrayList<>(devices);
    }
}
