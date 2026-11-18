/** A laptop is a kind of Device (inheritance) that also has a screen size. */
public class Laptop extends Device {
    private final double screenSize;

    public Laptop(String assetTag, String manufacturer, String model,
                  Status status, String assignedTo, String location, double screenSize) {
        super(assetTag, manufacturer, model, status, assignedTo, location);
        if (!Double.isFinite(screenSize) || screenSize <= 0) {
            throw new IllegalArgumentException("Screen size must be positive.");
        }
        this.screenSize = screenSize;
    }

    public double getScreenSize() { return screenSize; }

    @Override
    public String getDeviceType() { return "Laptop"; }

    /** Shows 14" instead of 14.0, but keeps decimals like 15.6". */
    @Override
    public String getDetails() { return formatSize(screenSize) + "\" screen"; }

    @Override
    public String getExtraValue() { return formatSize(screenSize); }

    private static String formatSize(double size) {
        return size == Math.rint(size) ? String.valueOf((long) size) : String.valueOf(size);
    }
}
