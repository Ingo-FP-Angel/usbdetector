package de.ingofpangel;

import org.usb4java.*;

public class Main {

    public static void main(String[] args) {
        var product = findMtpDevice();
        if (product != null) {
            System.out.println(product);
        }
    }

    private static DeviceInfo findMtpDevice() {
        int result = LibUsb.init(null);
        if (result != LibUsb.SUCCESS) return null;

        DeviceList list = new DeviceList();
        result = LibUsb.getDeviceList(null, list);
        if (result < 0) throw new LibUsbException("Unable to get device list", result);

        try {
            for (Device device : list) {
                DeviceDescriptor descriptor = new DeviceDescriptor();
                result = LibUsb.getDeviceDescriptor(device, descriptor);
                if (result != LibUsb.SUCCESS) throw new LibUsbException("Unable to read device descriptor", result);

                System.out.println(System.lineSeparator()
                        + String.format("0x%04x", descriptor.idVendor())
                        + "/"
                        + String.format("0x%04x", descriptor.idProduct()));

                ConfigDescriptor configDescriptor = new ConfigDescriptor();
                result = LibUsb.getConfigDescriptor(device, (byte) 0, configDescriptor);
                if (result != LibUsb.SUCCESS) {
                    var ex = new LibUsbException("Unable to read config descriptor", result);
                    System.out.println(ex.getMessage());
                    continue;
                }

                DeviceHandle handle = new DeviceHandle();
                result = LibUsb.open(device, handle);
                if (result != LibUsb.SUCCESS) {
                    var ex = new LibUsbException("Unable to open device handle", result);
                    System.out.println(ex.getMessage());
                    continue;
                }

                StringBuffer bufferManufacturer = new StringBuffer(30);
                LibUsb.getStringDescriptorAscii(handle, descriptor.iManufacturer(), bufferManufacturer);
                System.out.println("Manufacturer: " + bufferManufacturer);

                StringBuffer bufferProduct = new StringBuffer(30);
                LibUsb.getStringDescriptorAscii(handle, descriptor.iProduct(), bufferProduct);
                System.out.println("Product     : " + bufferProduct);

                StringBuffer bufferConfiguration = new StringBuffer(30);
                LibUsb.getStringDescriptorAscii(handle, configDescriptor.iConfiguration(), bufferConfiguration);
                System.out.println("Config      : " + bufferConfiguration);

                LibUsb.close(handle);

                if (bufferConfiguration.toString().equalsIgnoreCase("mtp")
                        || bufferConfiguration.toString().equalsIgnoreCase("adb"))
                    return new DeviceInfo(
                            bufferManufacturer.toString(),
                            bufferProduct.toString(),
                            bufferConfiguration.toString()
                    );
            }
        } finally {
            // Ensure the allocated device list is freed
            LibUsb.freeDeviceList(list, true);
            LibUsb.exit(null);
        }
        return null;
    }
}
