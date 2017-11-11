package gobelinmaker.devicemanager;

public interface IDeviceCommand {
    public void doCommand(String deviceID, String command, int responseChannel);
    public void doCommand(String deviceID, String command);
}
