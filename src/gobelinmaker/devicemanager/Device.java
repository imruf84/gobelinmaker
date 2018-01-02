package gobelinmaker.devicemanager;

/**
 * Eszköz alaposztálya.
 *
 * @author imruf84
 */
public class Device {

    /**
     * Eszköz azonosítója.
     */
    private final String ID;
    /**
     * Arduino segédosztály.
     */
    private final MyArduino arduino;

    /**
     * Konstruktor.
     *
     * @param ID eszköz azonosítója
     * @param arduino Arduino segédosztály
     */
    public Device(String ID, MyArduino arduino) {
        this.ID = ID;
        this.arduino = arduino;
    }

    /**
     * Azonosító lekérdezése.
     *
     * @return azonosító
     */
    public String getID() {
        return ID;
    }

    /**
     * Parancs küldése és várakozás a végrehajtás végéig.
     *
     * @param command parancs
     * @return válasz szövege
     */
    public String sendCommandAndWait(String command) {
        arduino.serialWrite(command + "\n");
        String s;
        while ("".equals(s = arduino.serialRead())) {
        }
        return s.trim();
    }

    @Override
    public String toString() {
        return "Device{" + "ID=" + ID + ", Port=" + arduino.getSerialPort().getSystemPortName() + '}';
    }

}
