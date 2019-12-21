package hu.galambo.gobelin.device;

import org.gobelinmaker.gobelinmaker.devicemanager.DeviceCommandErrorException;
import org.gobelinmaker.gobelinmaker.devicemanager.IDevicaCommandCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jssc.SerialPortException;

public class Device {

	@SuppressWarnings("unused")
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

	public String sendCommandSync(String command) throws SerialPortException, InterruptedException, DeviceCommandErrorException {

		getArduino().serialWrite(command + "\n");
		String response;
		while ("".equals(response = getArduino().serialRead()));

		response = response.trim();
		
		if (response.startsWith("[ERROR]")) {
			throw new DeviceCommandErrorException(response.substring("[ERROR]".length()));
		}
		
		return response;
	}
	
	public Thread sendCommandAsync(String command, IDevicaCommandCallback callback) {
		
		Thread thread = new Thread(() -> {
			try {
				String response = sendCommandSync(command);
				callback.onSuccess(response);
			} catch (SerialPortException | DeviceCommandErrorException e1) {
				callback.onError(e1);
			} catch (InterruptedException e2) {
				callback.onError(e2);
				Thread.currentThread().interrupt();
			}
			
			callback.onAlways();
		});
		
		thread.start();
		
		return thread;
	}

	@Override
	public String toString() {
		return "Device{" + "ID=" + id + ", Port=" + getArduino().getSerialPort().getPortName() + '}';
	}

	public ArduinoDevice getArduino() {
		return arduino;
	}

}
