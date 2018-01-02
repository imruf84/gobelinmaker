package gobelinmaker.devicemanager;

import com.fazecast.jSerialComm.SerialPort;
import gobelinmaker.MyLog;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

/**
 * Eszközkezelő osztálya.
 *
 * @author imruf84
 */
public class DeviceManager extends HashMap<String, Device> {

    /**
     * Csatlakoztatott eszközök felderítése.
     *
     * @return csatlakoztatott eszköz(ök) esetén igaz egyébként hamis
     * @throws InterruptedException kivétel
     */
    public boolean scan() throws InterruptedException {

        MyLog.info("Scanning for devices...");

        boolean devFound = false;
        for (SerialPort sp : SerialPort.getCommPorts()) {
            String portName = sp.getSystemPortName();

            MyArduino arduino = new MyArduino(portName, 9600);
            arduino.openConnection();

            TimeUnit.SECONDS.sleep(5);

            arduino.serialWrite("getID\n");
            String devID = arduino.serialRead().replace("\n", "").trim();

            if (!devID.isEmpty()) {
                MyLog.info("Device found with ID:'" + devID + "' on port " + portName);
                put(devID, new Device(devID, arduino));
                devFound = true;
                continue;
            }

            arduino.closeConnection();
        }

        if (!devFound) {
            MyLog.warning("No devices found.");
        }

        return devFound;

    }

}
