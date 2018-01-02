package gobelinmaker.devicemanager;

import arduino.Arduino;
import com.fazecast.jSerialComm.SerialPort;
import java.util.Scanner;

/**
 * Saját Arduino kiterjesztés.
 *
 * @author imruf84
 */
public class MyArduino extends Arduino {

    /**
     * Konstruktor.
     */
    public MyArduino() {
        super();
    }

    /**
     * Konstruktor.
     *
     * @param portDescription port leírása
     */
    public MyArduino(String portDescription) {
        super(portDescription);
    }

    /**
     * Konstruktor.
     *
     * @param portDescription port leírása
     * @param baud_rate átviteli sebesség
     */
    public MyArduino(String portDescription, int baud_rate) {
        super(portDescription, baud_rate);
    }

    @Override
    public String serialRead() {
        getSerialPort().setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 0, 0);
        String out = "";
        try (Scanner in = new Scanner(getSerialPort().getInputStream())) {
            while (in.hasNext()) {
                out += (in.next() + " ");
            }
        }
        return out;
    }

}
