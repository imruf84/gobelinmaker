package gobelinmaker.devicemanager;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;

/**
 * Saját Arduino kiterjesztés.
 *
 * @author imruf84
 */
public class MyArduino {

    SerialPort serialPort;
    private PortReader pr;

    /**
     * Konstruktor.
     *
     * @param portDescription port leírása
     * @param baud_rate átviteli sebesség
     */
    public MyArduino(String portDescription, int baud_rate) {
        serialPort = new SerialPort(portDescription);
    }

    public String serialRead() throws InterruptedException {

        String result;
        while (pr.getResponse().isEmpty()) {
            TimeUnit.MILLISECONDS.sleep(1);
        }

        result = pr.getResponse();
        pr.init();

        return result;
    }

    public String serialRead(long timeout) throws InterruptedException {

        long startTime = System.currentTimeMillis();

        String result;
        while (pr.getResponse().isEmpty()) {

            long estimatedTime = System.currentTimeMillis() - startTime;
            if (estimatedTime > timeout) {
                return "";
            }

            TimeUnit.MILLISECONDS.sleep(1);
        }

        result = pr.getResponse();
        pr.init();

        return result;
    }

    void openConnection() throws SerialPortException {
        serialPort.openPort();

        serialPort.setParams(SerialPort.BAUDRATE_9600,
                SerialPort.DATABITS_8,
                SerialPort.STOPBITS_1,
                SerialPort.PARITY_NONE,
                false,
                false);

        if (pr == null) {
            pr = new PortReader();
            serialPort.addEventListener(pr, SerialPort.MASK_RXCHAR);
        }
    }

    void serialWrite(String id) throws SerialPortException {
        serialPort.writeString(id);
    }

    void closeConnection() throws SerialPortException {
        serialPort.closePort();
    }

    SerialPort getSerialPort() {
        return serialPort;
    }

    private class PortReader implements SerialPortEventListener {

        String r = "";
        AtomicReference<String> response = new AtomicReference<>("");

        @Override
        public void serialEvent(SerialPortEvent event) {
            if (event.isRXCHAR() && event.getEventValue() > 0) {
                try {
                    r += serialPort.readString(event.getEventValue());
                    if (r.endsWith("\n")) {
                        response.set(r);
                        r = "";
                    }
                } catch (SerialPortException ex) {
                    System.out.println("Error in receiving string from COM-port: " + ex);
                }
            }
        }

        public String getResponse() {
            return response.get();
        }

        public void init() {
            response.set("");
        }

    }

}
