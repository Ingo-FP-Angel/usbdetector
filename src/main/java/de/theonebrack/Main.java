package de.theonebrack;

import org.usb4java.*;

public class Main {

    public static void main(String[] args) {
        var product = findMtpDevice();
        if (product != null) {
            System.out.println("Found product " + product);
        }
    }

    private static String findMtpDevice() {
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
                ConfigDescriptor configDescriptor = new ConfigDescriptor();
                result = LibUsb.getConfigDescriptor(device, (byte) 0, configDescriptor);
                if (result != LibUsb.SUCCESS) throw new LibUsbException("Unable to read config descriptor", result);

                System.out.println(String.format("0x%04X", descriptor.idVendor()) + "/" + String.format("0x%04X", descriptor.idProduct()));

                DeviceHandle handle = new DeviceHandle();
                result = LibUsb.open(device, handle);
                if (result != LibUsb.SUCCESS) continue;

                StringBuffer bufferProduct = new StringBuffer(100);
                LibUsb.getStringDescriptorAscii(handle, descriptor.iProduct(), bufferProduct);
                System.out.println(bufferProduct);

                StringBuffer bufferConfiguration = new StringBuffer(100);
                LibUsb.getStringDescriptorAscii(handle, configDescriptor.iConfiguration(), bufferConfiguration);
                System.out.println(bufferConfiguration);

                LibUsb.close(handle);

                if (bufferConfiguration.toString().equals("mtp"))
                    return bufferProduct.toString();
            }
        } finally {
            // Ensure the allocated device list is freed
            LibUsb.freeDeviceList(list, true);
            LibUsb.exit(null);
        }
        return null;
    }
}
