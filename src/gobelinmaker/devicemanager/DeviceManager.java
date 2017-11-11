package gobelinmaker.devicemanager;

import com.fazecast.jSerialComm.SerialPort;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class DeviceManager extends HashMap<String, Device> {

    public boolean scan() throws InterruptedException {

        System.out.println("Scanning for devices...");

        boolean devFound = false;
        for (SerialPort sp : SerialPort.getCommPorts()) {
            String portName = sp.getSystemPortName();

            MyArduino arduino = new MyArduino(portName, 9600);
            arduino.openConnection();

            TimeUnit.SECONDS.sleep(5);

            arduino.serialWrite("getID\n");
            String devID = arduino.serialRead().replace("\n", "").trim();

            if (!devID.isEmpty()) {
                System.out.println("Device found with ID:'" + devID + "' on port " + portName);
                put(devID, new Device(devID, arduino));
                devFound = true;
                continue;
            }

            arduino.closeConnection();
        }

        if (!devFound) {
            System.out.println("No devices found.");
        }

        return devFound;

    }

}
