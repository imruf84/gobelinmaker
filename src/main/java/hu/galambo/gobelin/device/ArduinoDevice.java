package hu.galambo.gobelin.device;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;


public class ArduinoDevice {
	
	private static final Logger LOG = LoggerFactory.getLogger(ArduinoDevice.class);

	SerialPort serialPort;
	private PortReader pr;

	public ArduinoDevice(String portDescription) {
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

		serialPort.setParams(SerialPort.BAUDRATE_9600, SerialPort.DATABITS_8, SerialPort.STOPBITS_1,
				SerialPort.PARITY_NONE, false, false);

		if (pr == null) {
			pr = new PortReader();
			serialPort.addEventListener(pr, SerialPort.MASK_RXCHAR);
		}
	}

	void serialWrite(String s) throws SerialPortException {
		serialPort.writeString(s);
	}

	void closeConnection() throws SerialPortException {
		if (!serialPort.isOpened()) {
			return;
		}

		serialPort.purgePort(1);
		serialPort.purgePort(2);
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
				} catch (SerialPortException e) {
					LOG.error(String.format("Error in receiving string from COM-port: %s", e.getLocalizedMessage()));
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
