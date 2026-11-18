/** A desktop computer is a kind of Device (inheritance). */
public class Desktop extends Device {

    public Desktop(String assetTag, String manufacturer, String model,
                   Status status, String assignedTo, String location) {
        super(assetTag, manufacturer, model, status, assignedTo, location);
    }

    @Override
    public String getDeviceType() { return "Desktop"; }

    @Override
    public String getDetails() { return ""; }

    @Override
    public String getExtraValue() { return ""; }
}
