package gobelinmaker;

import gobelinmaker.theme.MyMetalTheme;
import com.martiansoftware.jsap.JSAPResult;
import com.martiansoftware.jsap.Parameter;
import com.martiansoftware.jsap.SimpleJSAP;
import com.martiansoftware.jsap.Switch;
import gobelinmaker.console.GobelinConsole;
import gobelinmaker.server.GobelinServer;
import gobelinmaker.simulator.SimulatorFrame;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.io.IOException;
import javax.swing.JFrame;
import javax.swing.UIManager;

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
        setLookAndFeel();
        
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
        
        // Szerver futtatása ha szükséges.
        if (config.getBoolean("server")) {
            GobelinServer server = new GobelinServer();
            server.startServer();
        }
        
        // Szimulátor futtatása ha szükséges.
        if (config.getBoolean("simulator")) {
            
            SimulatorFrame f = new SimulatorFrame();
            f.setVisible(true);
        }

        // Konzol futtatása ha szükséges.
        if (config.getBoolean("console")) {
            GobelinConsole console = new GobelinConsole();
            console.start();
        }

    }
    
    public static void setLookAndFeel() throws FontFormatException, IOException {
        /* Téma beállítása. */
        javax.swing.plaf.metal.MetalLookAndFeel.setCurrentTheme(new MyMetalTheme());
        // Az ablakkeret az operációs rendszeré szeretnénk, hogy legyen.
        JFrame.setDefaultLookAndFeelDecorated(false);
        // Egyes témák esetében az alapértelmezett Enter leütés nem csinál semmit, ezért engedélyezzük külön.
        UIManager.getLookAndFeelDefaults().put("Button.defaultButtonFollowsFocus", Boolean.TRUE);
        // Görgetősávok témájának megváltoztatása sajátra, mert a lila szerintem túl gagyi.
        UIManager.getLookAndFeelDefaults().put("ScrollBarUI", "gobelinmaker.theme.SimpleScrollBarUI");
        // Folyamatjelző felirata legyen fekete.
        UIManager.put("ProgressBar.selectionForeground", Color.BLACK);
        UIManager.put("ProgressBar.selectionBackground", Color.BLACK);
        // Betűtípusok beállítása.
        Font font = Font.createFont(Font.TRUETYPE_FONT, GobelinMaker.class.getResourceAsStream("/gobelinmaker/theme/cour.ttf")).deriveFont(Font.BOLD, 14);
        for (String s : new String[]{"Button.font","ToggleButton.font","RadioButton.font","CheckBox.font","ColorChooser.font","ComboBox.font","Label.font","List.font","MenuBar.font","MenuItem.font","RadioButtonMenuItem.font","CheckBoxMenuItem.font","Menu.font","PopupMenu.font","OptionPane.font","Panel.font","ProgressBar.font","ScrollPane.font","Viewport.font","TabbedPane.font","Table.font","TableHeader.font","TextField.font","PasswordField.font","TextArea.font","TextPane.font","EditorPane.font","TitledBorder.font","ToolBar.font","ToolTip.font","Tree.font"}) {
            UIManager.getLookAndFeelDefaults().put(s, font);
        }
    }
}
