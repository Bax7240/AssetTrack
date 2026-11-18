import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/** Owns the inventory and saves it to / loads it from a CSV file. */
public class InventoryManager {
    private static final String CSV_HEADER = "Type,AssetTag,Manufacturer,Model,Status,AssignedTo,Location,Extra";

    private final ArrayList<Device> devices = new ArrayList<>();

    // ---------------------------------------------------------------- CRUD

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

    /**
     * Search every field (tag, make, model, type, status, assignee, location, details)
     * using a loop and conditional checks. Blank search returns everything.
     */
    public List<Device> search(String query) {
        String text = query == null ? "" : query.trim().toLowerCase(Locale.ROOT);
        ArrayList<Device> matches = new ArrayList<>();
        for (Device device : devices) {
            if (device.getSearchText().toLowerCase(Locale.ROOT).contains(text)) {
                matches.add(device);
            }
        }
        return matches;
    }

    public List<Device> getDevices() {
        return new ArrayList<>(devices);
    }

    public int size() {
        return devices.size();
    }

    // ------------------------------------------------------------ Factory

    /**
     * Creates the right subclass from a type name. Used by both the GUI form
     * and the CSV loader so there is one place that knows about device types.
     *
     * @param extra type-specific value (a laptop's screen size); ignored for desktops
     */
    public static Device createDevice(String type, String tag, String manufacturer, String model,
                                      Status status, String assignedTo, String location, String extra) {
        if ("Laptop".equalsIgnoreCase(type)) {
            double size;
            try {
                size = Double.parseDouble(extra == null ? "" : extra.trim());
            } catch (NumberFormatException ex) {
                throw new IllegalArgumentException("Enter a numeric screen size in inches.");
            }
            return new Laptop(tag, manufacturer, model, status, assignedTo, location, size);
        }
        if ("Desktop".equalsIgnoreCase(type)) {
            return new Desktop(tag, manufacturer, model, status, assignedTo, location);
        }
        throw new IllegalArgumentException("Unknown device type: " + type);
    }

    // -------------------------------------------------------- Save / Load

    /** Writes every device to a CSV file. Writes a temp file first so a crash can't corrupt the save. */
    public void saveToFile(Path file) throws IOException {
        ArrayList<String> lines = new ArrayList<>();
        lines.add(CSV_HEADER);
        for (Device d : devices) {
            String[] fields = {d.getDeviceType(), d.getAssetTag(), d.getManufacturer(), d.getModel(),
                    d.getStatus().toString(), d.getAssignedTo(), d.getLocation(), d.getExtraValue()};
            lines.add(toCsvLine(fields));
        }
        Path absolute = file.toAbsolutePath();
        Path temp = absolute.resolveSibling(absolute.getFileName() + ".tmp");
        Files.write(temp, lines, StandardCharsets.UTF_8);
        try {
            Files.move(temp, absolute, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        } catch (AtomicMoveNotSupportedException ex) {
            Files.move(temp, absolute, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    /**
     * Replaces the inventory with the devices in a CSV file.
     * Bad rows are skipped instead of stopping the whole load.
     *
     * @return the number of rows that were skipped
     */
    public int loadFromFile(Path file) throws IOException {
        List<String> lines = Files.readAllLines(file, StandardCharsets.UTF_8);
        devices.clear();
        int skipped = 0;
        for (int i = 1; i < lines.size(); i++) { // line 0 is the header
            String line = lines.get(i);
            if (line.isBlank()) continue;
            try {
                String[] f = parseCsvLine(line);
                if (f.length < 8) throw new IllegalArgumentException("Missing columns");
                Device device = createDevice(f[0], f[1], f[2], f[3], Status.fromText(f[4]), f[5], f[6], f[7]);
                if (!addDevice(device)) skipped++; // duplicate tag
            } catch (IllegalArgumentException ex) {
                skipped++;
            }
        }
        return skipped;
    }

    /** Wraps each field in quotes and doubles any quotes inside it, so commas in names are safe. */
    private static String toCsvLine(String[] fields) {
        StringBuilder line = new StringBuilder();
        for (int i = 0; i < fields.length; i++) {
            if (i > 0) line.append(',');
            line.append('"').append(fields[i].replace("\"", "\"\"")).append('"');
        }
        return line.toString();
    }

    /** Splits one CSV line character by character, honoring quoted fields. */
    private static String[] parseCsvLine(String line) {
        ArrayList<String> fields = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inQuotes = false;
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (inQuotes) {
                if (c == '"' && i + 1 < line.length() && line.charAt(i + 1) == '"') {
                    current.append('"'); // escaped quote
                    i++;
                } else if (c == '"') {
                    inQuotes = false;
                } else {
                    current.append(c);
                }
            } else if (c == '"') {
                inQuotes = true;
            } else if (c == ',') {
                fields.add(current.toString());
                current.setLength(0);
            } else {
                current.append(c);
            }
        }
        fields.add(current.toString());
        return fields.toArray(new String[0]);
    }
}
