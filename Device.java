public class Device{
    private String assetTag;
    private String manufacturer;
    private String model;
    private String serialNumber;
    private String processor;
    private int ramGB;
    private int storageGB;
    private String status;
    private String assignedTo;

    public Device(String tag, String companyName, String modelName, String serial, String cpu, int memoryGB, int diskGB, String initialStatus, String user) {
        assetTag = tag;
        manufacturer = companyName;
        model = modelName;
        serialNumber = serial;
        processor = cpu;
        ramGB = memoryGB;
        storageGB = diskGB;
        status = initialStatus;
        assignedTo = user;
    }

    // All Nine Getters

    public String getAssetTag() {
        return assetTag;
    }

    public String getManufacturer(){
        return manufacturer;
    }

    public String getModel(){
        return model;
    }

    public String getSerialNumber(){
        return serialNumber;
    }

    public String getProcessor(){
        return processor;
    }

    public int getRamGB(){
        return ramGB;
    }

    public int getStorageGB(){
        return storageGB;
    }

    public String getStatus(){
        return status;
    }

    public String getAssignedTo(){
        return assignedTo;
    }

    // All Nine Setters

    public void setAssetTag(String newAssetTag){
        assetTag = newAssetTag;
    }

    public void setManufacturer(String newManufacturer){
        manufacturer = newManufacturer;
    }

    public void setModel(String newModel){
        model = newModel;
    }

    public void setSerialNumber(String newSerialNumber){
        serialNumber = newSerialNumber;
    }

    public void setProcessor(String newProcessor){
        processor = newProcessor;
    }

    public void setRamGB(int newRamGB){
        ramGB = newRamGB;
    }

    public void setStorageGB(int newStorageGB){
        storageGB = newStorageGB;
    }

    public void setStatus(String newStatus) {
        status = newStatus;
    }

    public void setAssignedTo(String newAssignedTo){
        assignedTo = newAssignedTo;
    }
}