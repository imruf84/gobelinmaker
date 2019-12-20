package hu.galambo.gobelin.device;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jssc.SerialPortException;

public class Device {

	private static final Logger LOG = LoggerFactory.getLogger(Device.class);

	private final String id;
	private final ArduinoDevice arduino;

	
	public Device(String id, ArduinoDevice arduino) {
		this.id = id;
		this.arduino = arduino;
	}

	public String getID() {
		return id;
	}

	public String sendCommandAndWait(String command) {
		String s = "ERROR";

		try {
			getArduino().serialWrite(command + "\n");
		} catch (SerialPortException e) {
			LOG.error(String.format("Failed to send command:'%s', %s", command, e.getLocalizedMessage()));
			return s;
		}
		try {
			while ("".equals(s = getArduino().serialRead()))
				;
		} catch (InterruptedException e) {
			LOG.error(String.format("Failed to send command:'%s', %s", command, e.getLocalizedMessage()));
			Thread.currentThread().interrupt();
			return s;
		}

		return s.trim();
	}

	@Override
	public String toString() {
		return "Device{" + "ID=" + id + ", Port=" + getArduino().getSerialPort().getPortName() + '}';
	}

	public ArduinoDevice getArduino() {
		return arduino;
	}

}
