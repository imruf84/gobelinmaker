package gobelinmaker;

import com.martiansoftware.jsap.JSAPResult;
import com.martiansoftware.jsap.Parameter;
import com.martiansoftware.jsap.SimpleJSAP;
import com.martiansoftware.jsap.Switch;
import java.util.Arrays;
import gobelinmaker.console.GobelinConsole;
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

        SimpleJSAP jsap = new SimpleJSAP(
                "gm.jar",
                "Runs Gobelin Maker Command Line Tool",
                new Parameter[]{
                    new Switch("server").setShortFlag('s').setLongFlag("server").setHelp("Starts a server."),
                    new Switch("client").setShortFlag('c').setLongFlag("client").setHelp("Starts a client."),
                    new Switch("simulator").setShortFlag('S').setLongFlag("simulator").setHelp("Starts a simulator."),}
        );

        JSAPResult config = jsap.parse(args);
        if (jsap.messagePrinted()) {
            System.exit(0);
        }

        /*DeviceManager dm = new DeviceManager();
        dm.scan();
        System.out.println(dm.get("gm").sendCommandAndWait(":abc"));*/
        // Help megjelenítése ha szükséges.
        if (Arrays.asList(args).contains("-h") || Arrays.asList(args).contains("-help")) {
            System.out.println("");
        }
        
        // Szimulátor futtatása ha szükséges.
        if (config.getBoolean("simulator")) {
            System.out.println("simulator");
        }

        // Szerver futtatása ha szükséges.
        if (config.getBoolean("server")) {
            GobelinServer server = new GobelinServer();
            server.startServer();
        }

        // Konzol futtatása ha szükséges.
        if (config.getBoolean("client")) {
            GobelinConsole console = new GobelinConsole();
            console.start();
        }

    }
}
