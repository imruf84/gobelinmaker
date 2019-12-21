package org.gobelinmaker.gobelinmaker.devicemanager;

public interface IDevicaCommandCallback {
	public void onSuccess(String response);
	public void onError(Exception e);
	public void onAlways();
}
