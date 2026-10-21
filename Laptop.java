/** A laptop is a kind of Device (inheritance). */
public class Laptop extends Device {
    private final double screenSize;

    public Laptop(String assetTag, String manufacturer, String model,
                  String status, String assignedTo, double screenSize) {
        super(assetTag, manufacturer, model, status, assignedTo);
        if (screenSize <= 0) {
            throw new IllegalArgumentException("Screen size must be positive.");
        }
        this.screenSize = screenSize;
    }

    public double getScreenSize() { return screenSize; }

    @Override
    public String getDeviceType() { return "Laptop"; }
}
