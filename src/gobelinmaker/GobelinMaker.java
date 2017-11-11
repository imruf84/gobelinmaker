package gobelinmaker;

import com.martiansoftware.jsap.JSAPResult;
import com.martiansoftware.jsap.Parameter;
import com.martiansoftware.jsap.SimpleJSAP;
import com.martiansoftware.jsap.Switch;
import java.util.Arrays;
import gobelinmaker.console.GobelinConsole;
import gobelinmaker.server.GobelinServer;
import java.awt.BorderLayout;
import java.awt.Dimension;
import javax.swing.JButton;
import javax.swing.JFrame;

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

        MyLog.showDebugMessages = true;
        
        SimpleJSAP jsap = new SimpleJSAP(
                "gm.jar",
                "Runs Gobelin Maker Command Line Tool",
                new Parameter[]{
                    new Switch("server").setShortFlag('s').setLongFlag("server").setHelp("Starts a server."),
                    new Switch("console").setShortFlag('c').setLongFlag("console").setHelp("Starts a console."),
                    new Switch("simulator").setShortFlag('S').setLongFlag("simulator").setHelp("Starts a simulator."),}
        );

        JSAPResult config = jsap.parse(args);
        if (jsap.messagePrinted()) {
            System.exit(0);
        }

        /*DeviceManager dm = new DeviceManager();
        dm.scan();
        System.out.println(dm.get("gm").sendCommandAndWait(":abc"));*/
        
        // Szerver futtatása ha szükséges.
        if (config.getBoolean("server")) {
            GobelinServer server = new GobelinServer();
            server.startServer();
        }
        
        // Szimulátor futtatása ha szükséges.
        if (config.getBoolean("simulator")) {
            
            JFrame f = new JFrame();
            f.getContentPane().setLayout(new BorderLayout());
            f.add(new JButton("gomb"));
            f.pack();
            f.setLocationRelativeTo(null);
            f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            f.setVisible(true);
        }

        // Konzol futtatása ha szükséges.
        if (config.getBoolean("console")) {
            GobelinConsole console = new GobelinConsole();
            console.start();
        }

    }
}
