package org.gobelinmaker.gobelinmaker.devicemanager;

@SuppressWarnings("serial")
public class DeviceCommandErrorException extends Exception {
	
	public DeviceCommandErrorException() {
		super();
	}
	
	public DeviceCommandErrorException(String message) {
		super(message);
	}
}
