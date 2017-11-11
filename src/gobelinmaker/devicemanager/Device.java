package gobelinmaker.devicemanager;

public class Device {

    private final String ID;
    private final MyArduino arduino;

    public Device(String ID, MyArduino arduino) {
        this.ID = ID;
        this.arduino = arduino;
    }

    public String getID() {
        return ID;
    }

    public String sendCommandAndWait(String command) {
        arduino.serialWrite(command + "\n");
        String s;
        while ("".equals(s = arduino.serialRead())){}
        return s.trim();
    }

    @Override
    public String toString() {
        return "Device{" + "ID=" + ID + ", Port=" + arduino.getSerialPort().getSystemPortName() + '}';
    }

}
