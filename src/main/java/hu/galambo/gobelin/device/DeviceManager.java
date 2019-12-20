package hu.galambo.gobelin.device;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.gobelinmaker.gobelinmaker.MyLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import jssc.SerialPortException;
import jssc.SerialPortList;

@Component
@SuppressWarnings("serial")
public class DeviceManager extends HashMap<String, Device> {

	private static final Logger LOG = LoggerFactory.getLogger(DeviceManager.class);

	private static DeviceManager instance;

	private DeviceManager() {
	}

	private boolean scan() throws InterruptedException, SerialPortException {

		LOG.info("Scanning devices...");

		boolean devFound = false;
		for (String portName : SerialPortList.getPortNames()) {

			ArduinoDevice arduino = new ArduinoDevice(portName);
			arduino.openConnection();

			TimeUnit.SECONDS.sleep(5);

			arduino.serialWrite("getID\n");
			String devID = arduino.serialRead(1000).replace("\n", "").trim();

			if (!devID.isEmpty()) {
				MyLog.info("Device found with ID:'" + devID + "' on port " + portName);
				put(devID, new Device(devID, arduino));
				devFound = true;
				continue;
			}

			arduino.closeConnection();
		}

		if (!devFound) {
			LOG.warn("No devices found.");
		}

		return devFound;
	}

	@PostConstruct
	public static DeviceManager getInstance() {
		if (instance == null) {
			try {
				instance = new DeviceManager();
				instance.scan();
			} catch (InterruptedException | SerialPortException e) {
				LOG.error(String.format("Error init DeviceManager: %s", e.getLocalizedMessage()));
				Thread.currentThread().interrupt();
			}
		}
		return instance;
	}

	@PreDestroy
	public static void abc() {

		LOG.info("Shutting down devices: ");

		for (Device device : instance.values()) {
			LOG.info(String.format("ID:'%s' on port: %s", device.getID(),
					device.getArduino().getSerialPort().getPortName()));
			try {
				device.getArduino().closeConnection();
			} catch (SerialPortException e) {
				LOG.error(String.format("Failed to shutdown device: %s", e.getLocalizedMessage()));
			}
		}

	}

}
