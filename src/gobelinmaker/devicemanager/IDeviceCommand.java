package gobelinmaker.devicemanager;

/**
 * Eszközre küldött parancs interfésze.
 *
 * @author imruf84
 */
public interface IDeviceCommand {

    /**
     * Parancs végrehajtása.
     *
     * @param deviceID eszköz azonosítója
     * @param command parancs szövege
     * @param responseChannel válaszcsatorna
     */
    public void doCommand(String deviceID, String command, int responseChannel);

    /**
     * Parancs végrehajtása.
     *
     * @param deviceID eszköz azonosítója
     * @param command parancs szövege
     */
    public void doCommand(String deviceID, String command);
}
