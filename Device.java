/**
 * A computer tracked by AssetTrack.
 * Device is abstract: every real device is a specific kind (Desktop or Laptop),
 * and each subclass decides its own type name and details (polymorphism).
 */
public abstract class Device {
    private final String assetTag;
    private final String manufacturer;
    private final String model;
    private Status status;
    private String assignedTo;
    private String location;

    protected Device(String assetTag, String manufacturer, String model,
                     Status status, String assignedTo, String location) {
        if (assetTag == null || assetTag.isBlank()) {
            throw new IllegalArgumentException("Asset tag is required.");
        }
        this.assetTag = assetTag.trim();
        this.manufacturer = clean(manufacturer);
        this.model = clean(model);
        setStatus(status);
        setAssignedTo(assignedTo);
        setLocation(location);
    }

    public String getAssetTag() { return assetTag; }
    public String getManufacturer() { return manufacturer; }
    public String getModel() { return model; }
    public Status getStatus() { return status; }
    public String getAssignedTo() { return assignedTo; }
    public String getLocation() { return location; }

    public final void setStatus(Status status) {
        if (status == null) {
            throw new IllegalArgumentException("Choose a valid status.");
        }
        this.status = status;
    }

    public final void setAssignedTo(String assignedTo) {
        this.assignedTo = clean(assignedTo);
    }

    public final void setLocation(String location) {
        this.location = clean(location);
    }

    /** Each subclass names its own type, e.g. "Desktop" or "Laptop". */
    public abstract String getDeviceType();

    /** Type-specific details shown in the table, e.g. a laptop's screen size. */
    public abstract String getDetails();

    /**
     * Type-specific value saved in the CSV file (blank if the type has none).
     * Lets the storage code save any device without checking its class.
     */
    public abstract String getExtraValue();

    /** Text the search box looks through: every field a user might type. */
    public String getSearchText() {
        return String.join(" ", assetTag, manufacturer, model, getDeviceType(),
                status.toString(), assignedTo, location, getDetails());
    }

    private static String clean(String text) {
        return text == null ? "" : text.trim();
    }

    @Override
    public String toString() {
        return assetTag + " - " + manufacturer + " " + model + " (" + getDeviceType() + ")";
    }
}
