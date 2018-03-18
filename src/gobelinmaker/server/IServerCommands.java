package gobelinmaker.server;

import gobelinmaker.devicemanager.IDeviceCommand;

/**
 * Szerverparancsok interfésze.
 *
 * @author igalambo
 */
public interface IServerCommands extends IDeviceCommand {

    /**
     * Teszt szöveg kiírása.
     */
    public void serverPrint();

    /**
     * Teszt szöveg kiírása.
     *
     * @param responseChannel válasz csatorna sorszáma
     */
    public void serverPrint(int responseChannel);

    /**
     * Csatlakoztatott eszközök kilistázása.
     */
    public void listDevices();

    /**
     * Csatlakoztatott eszközök kilistázása.
     *
     * @param responseChannel válasz csatorna sorszáma
     */
    public void listDevices(int responseChannel);

    /**
     * Csatlakoztatott webkamerák kilistázása.
     */
    public void listWebcams();

    /**
     * Csatlakoztatott webkamerák kilistázása.
     *
     * @param responseChannel válasz csatorna sorszáma
     */
    public void listWebcams(int responseChannel);

    /**
     * Alapértelmezett webkamera megnyitása.
     */
    public void openWebcam();
    
    /**
     * Webkamera megnyitása.
     *
     * @param index webkamera sorszáma
     */
    public void openWebcam(int index);

    /**
     * Webkamera megnyitása.
     *
     * @param index webkamera sorszáma
     * @param responseChannel válasz csatorna sorszáma
     */
    public void openWebcam(int index, int responseChannel);

    /**
     * Alapértelmezett webkamera bezárása.
     */
    public void closeWebcam();
    
    /**
     * Webkamera bezárása.
     *
     * @param index webkamera sorszáma
     */
    public void closeWebcam(int index);

    /**
     * Webkamera bezárása.
     *
     * @param index webkamera sorszáma
     * @param responseChannel válasz csatorna sorszáma
     */
    public void closeWebcam(int index, int responseChannel);

    /**
     * Kép készítése az alapértelmezett webkamerával.
     */
    public void getWebcamImage();
    
    /**
     * Kép készítése webkamerával.
     *
     * @param index webkamera sorszáma
     */
    public void getWebcamImage(int index);

    /**
     * Kép készítése webkamerával.
     *
     * @param index webkamera sorszáma
     * @param responseChannel válasz csatorna sorszáma
     */
    public void getWebcamImage(int index, int responseChannel);
    
    /**
     * Kép készítése az alapértelmezett webkamerával.
     * 
     * @param size képméret
     */
    public void getWebcamImageResized(String size);
    
    /**
     * Kép készítése az alapértelmezett webkamerával.
     * 
     * @param size képméret
     * @param responseChannel válasz csatorna sorszáma
     */
    public void getWebcamImageResized(String size, int responseChannel);
    
    /**
     * Kép készítése webkamerával.
     *
     * @param index webkamera sorszáma
     * @param size képméret
     */
    public void getWebcamImageResized(int index, String size);

    /**
     * Kép készítése webkamerával.
     *
     * @param index webkamera sorszáma
     * @param size képméret
     * @param responseChannel válasz csatorna sorszáma
     */
    public void getWebcamImageResized(int index, String size, int responseChannel);
}
