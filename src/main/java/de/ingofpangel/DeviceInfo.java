package de.ingofpangel;

public class DeviceInfo {
    private final String manufacturer;
    private final String product;
    private final String mode;

    public DeviceInfo(String manufacturer, String product, String mode) {
        this.manufacturer = manufacturer;
        this.product = product;
        this.mode = mode;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public String getProduct() {
        return product;
    }

    public String getMode() {
        return mode;
    }

    @Override
    public String toString() {
        return "Manufacturer: " + manufacturer + System.lineSeparator() +
               "Product     : " + product + System.lineSeparator() +
               "Mode        : " + mode;
    }
}
