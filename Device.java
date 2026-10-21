/**
 * A computer tracked by AssetTrack.
 * Keep device data small so it is easy to demonstrate and maintain.
 */
public class Device {
    private final String assetTag;
    private final String manufacturer;
    private final String model;
    private String status;
    private String assignedTo;

    public Device(String assetTag, String manufacturer, String model, String status, String assignedTo) {
        if (assetTag == null || assetTag.isBlank()) {
            throw new IllegalArgumentException("Asset tag is required.");
        }
        this.assetTag = assetTag.trim();
        this.manufacturer = manufacturer == null ? "" : manufacturer.trim();
        this.model = model == null ? "" : model.trim();
        setStatus(status);
        setAssignedTo(assignedTo);
    }

    public String getAssetTag() { return assetTag; }
    public String getManufacturer() { return manufacturer; }
    public String getModel() { return model; }
    public String getStatus() { return status; }
    public String getAssignedTo() { return assignedTo; }

    public void setStatus(String status) {
        if (!"Available".equals(status) && !"Deployed".equals(status)
                && !"In Repair".equals(status) && !"Retired".equals(status)) {
            throw new IllegalArgumentException("Choose a valid status.");
        }
        this.status = status;
    }

    public void setAssignedTo(String assignedTo) {
        this.assignedTo = assignedTo == null ? "" : assignedTo.trim();
    }

    /** Overridden by Laptop to demonstrate polymorphism. */
    public String getDeviceType() { return "Desktop"; }

    @Override
    public String toString() {
        return assetTag + " - " + manufacturer + " " + model + " (" + getDeviceType() + ")";
    }
}
