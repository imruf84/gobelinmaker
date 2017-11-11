package gobelinmaker;

import java.util.Arrays;
import gobelinmaker.console.GobelinConsole;
import gobelinmaker.devicemanager.DeviceManager;
import gobelinmaker.server.GobelinServer;

/**
 * GobelinMaker alkalmazás alaposztálya.
 *
 * @author igalambo
 */
public class GobelinMaker {

    /**
     * Alkalmazás belépési pontja.
     *
     * @param args parancssori argumentumok
     * @throws java.lang.Exception kivétel
     */
    public static void main(String[] args) throws Exception {

        /*DeviceManager dm = new DeviceManager();
        dm.scan();
        System.out.println(dm.get("gm").sendCommandAndWait(":abc"));*/

        // Szerver futtatása ha szükséges.
        if (Arrays.asList(args).contains("-s") || Arrays.asList(args).contains("-server")) {
            GobelinServer server = new GobelinServer();
            server.startServer();
        }
        
        // Konzol futtatása ha szükséges.
        if (Arrays.asList(args).contains("-c") || Arrays.asList(args).contains("-console")) {
            GobelinConsole console = new GobelinConsole();
            console.start();
        }

    }
}
