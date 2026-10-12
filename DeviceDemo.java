public class DeviceDemo {
    public static void main(String[] args) {
        Device device = new Device("TAG001", "Dell", "XPS 13", "SN123456", "Intel i7", 16, 512, "Available", "John Doe");
        System.out.println(device.getAssetTag());
        System.out.println(device.getManufacturer());
        System.out.println(device.getModel());
        System.out.println(device.getSerialNumber());
        System.out.println(device.getProcessor());
        System.out.println(device.getRamGB());
        System.out.println(device.getStorageGB());
        System.out.println(device.getStatus());
        System.out.println(device.getAssignedTo());
        device.setStatus("Deployed");
        System.out.println("Updated Status: " + device.getStatus());
        device.setAssignedTo("Doe John");
        System.out.println("Updated Assigned To: " + device.getAssignedTo());

        Laptop laptop = new Laptop(device, 85, 14);
        System.out.println("Laptop Device Serial Number: " + laptop.getDevice().getSerialNumber());
        System.out.println("Laptop Battery Health: " + laptop.getBatteryHealth() + "%");
        System.out.println("Laptop Screen Size: " + laptop.getScreenSize() + " inches");
    }
}
