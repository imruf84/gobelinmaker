package gobelinmaker;

import gobelinmaker.theme.MyMetalTheme;
import com.martiansoftware.jsap.JSAPResult;
import com.martiansoftware.jsap.Parameter;
import com.martiansoftware.jsap.SimpleJSAP;
import com.martiansoftware.jsap.Switch;
import gobelinmaker.console.GobelinConsole;
import gobelinmaker.server.GobelinServer;
import gobelinmaker.visual.server.VisualServerFrame;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.io.IOException;
import javax.swing.JFrame;
import javax.swing.UIManager;

/**
 * GobelinMaker alkalmazás alaposztálya. NOTE: Orange Pi fix: apt-get install
 * libv4l-0 libv4l-dev libv4lconvert0 libjpeg8-dev apt-get install libcairo2-dev
 * libjpeg62 libpango1.0-dev libgif-dev build-essential g++ imagemagick
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

        MyLog.showDebugMessages = !true;
        setLookAndFeel();

        SimpleJSAP jsap = new SimpleJSAP(
                "gm.jar",
                "Runs Gobelin Maker Command Line Tool",
                new Parameter[]{
                    new Switch("server").setShortFlag('s').setLongFlag("server").setHelp("Starts a server."),
                    new Switch("console").setShortFlag('c').setLongFlag("console").setHelp("Starts a console."),
                    new Switch("vserver").setShortFlag('S').setLongFlag("vserver").setHelp("Starts a visual server."),}
        );

        // Ha nincsenek paraméterek, akkor kiírjuk a használat módját.
        if (args.length < 1) {
            jsap.parse(new String[]{"--help"});
            System.exit(0);
        }

        // Egyébként mehet a munka.
        JSAPResult config = jsap.parse(args);
        if (jsap.messagePrinted()) {
            System.exit(0);
        }

        // Szerver futtatása ha szükséges.
        if (config.getBoolean("server")) {
            GobelinServer server = new GobelinServer();
            server.startServer();
        }

        // Szerver oldali GUI futtatása ha szükséges.
        if (config.getBoolean("vserver")) {

            VisualServerFrame f = new VisualServerFrame();
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
        for (String s : new String[]{"Button.font", "ToggleButton.font", "RadioButton.font", "CheckBox.font", "ColorChooser.font", "ComboBox.font", "Label.font", "List.font", "MenuBar.font", "MenuItem.font", "RadioButtonMenuItem.font", "CheckBoxMenuItem.font", "Menu.font", "PopupMenu.font", "OptionPane.font", "Panel.font", "ProgressBar.font", "ScrollPane.font", "Viewport.font", "TabbedPane.font", "Table.font", "TableHeader.font", "TextField.font", "PasswordField.font", "TextArea.font", "TextPane.font", "EditorPane.font", "TitledBorder.font", "ToolBar.font", "ToolTip.font", "Tree.font"}) {
            UIManager.getLookAndFeelDefaults().put(s, font);
        }
    }
}
