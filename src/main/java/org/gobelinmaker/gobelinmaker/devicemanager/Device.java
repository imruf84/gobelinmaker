package org.gobelinmaker.gobelinmaker.devicemanager;

import org.gobelinmaker.gobelinmaker.MyLog;

import jssc.SerialPortException;

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
        String s = "ERROR";

        try {
            arduino.serialWrite(command + "\n");
        } catch (SerialPortException ex) {
            MyLog.error("Failed to send command: " + command, ex);
            return s;
        }
        try {
            while ("".equals(s = arduino.serialRead())) {
            }
        } catch (InterruptedException ex) {
            MyLog.error("Failed to send command: " + command, ex);
            return s;
        }

        return s.trim();
    }

    @Override
    public String toString() {
        return "Device{" + "ID=" + ID + ", Port=" + arduino.getSerialPort().getPortName() + '}';
    }

}
