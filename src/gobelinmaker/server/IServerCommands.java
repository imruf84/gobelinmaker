package gobelinmaker.server;

import gobelinmaker.devicemanager.IDeviceCommand;

/**
 * Szerverparancsok interfésze.
 *
 * @author igalambo
 */
public interface IServerCommands extends IDeviceCommand {

    /**
     * Szöveg kiírása.
     */
    public void serverPrint();

    /**
     * Szöveg kiírása.
     *
     * @param responseChannel válasz sorszáma
     */
    public void serverPrint(int responseChannel);
    public void listDevices();
    public void listDevices(int responseChannel);
}
