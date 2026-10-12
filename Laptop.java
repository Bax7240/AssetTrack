public class Laptop{
    private Device device;
    private int batteryHealth;
    private double screenSize;

    public Laptop(Device computer, int health, double size) {
        device = computer;
        batteryHealth = health;
        screenSize = size;
    }

    public Device getDevice() {
        return device;
    }

    public int getBatteryHealth() {
        return batteryHealth;
    }

    public double getScreenSize() {
        return screenSize;
    }

    public void setDevice(Device newDevice) {
        device = newDevice;
    }

    public void setBatteryHealth(int newBatteryHealth) {
        batteryHealth = newBatteryHealth;
    }

    public void setScreenSize(double newScreenSize) {
        screenSize = newScreenSize;
    }

}