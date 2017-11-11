package gobelinmaker.devicemanager;

import arduino.Arduino;
import com.fazecast.jSerialComm.SerialPort;
import java.io.PrintWriter;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class MyArduino extends Arduino {

    public MyArduino() {
        super();
    }

    public MyArduino(String portDescription) {
        super(portDescription);
    }

    public MyArduino(String portDescription, int baud_rate) {
        super(portDescription, baud_rate);
    }

    @Override
    public String serialRead() {
        getSerialPort().setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 0, 0);
        String out = "";
        try (Scanner in = new Scanner(getSerialPort().getInputStream())) {
            while (in.hasNext()) {
                out += (in.next()+" ");
            }
        }
        return out;
    }

}
