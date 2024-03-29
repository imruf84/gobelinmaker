package org.gobelinmaker.gobelinmaker.server;

import com.budhash.cliche.CLIException;
import com.budhash.cliche.Command;
import com.budhash.cliche.Param;
import com.budhash.cliche.Shell;
import com.budhash.cliche.ShellDependent;
import com.budhash.cliche.ShellFactory;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import javax.imageio.ImageIO;

import org.gobelinmaker.gobelinmaker.MyLog;
import org.gobelinmaker.gobelinmaker.console.GobelinConsole;
import org.gobelinmaker.gobelinmaker.devicemanager.Device;
import org.gobelinmaker.gobelinmaker.devicemanager.DeviceManager;
import org.imgscalr.Scalr;
import org.imgscalr.Scalr.Method;
import org.imgscalr.Scalr.Mode;

/**
 * Szerver osztálya.
 *
 * @author igalambo
 */
public class GobelinServer extends Server implements ShellDependent, IServerCommands {

    /**
     * Átviteli buffer mérete.
     */
    public static final int BUFFER_SIZE = 20 * 1024 * 1024;
    /**
     * Kép átvitele esetén használandó előtag.
     */
    public static final String IMAGE_TAG = "image:";

    /**
     * Parancsértelmező.
     */
    private Shell sh = null;
    /**
     * Eszközkezelő.
     */
    private static final DeviceManager DEVICE_MANAGER = new DeviceManager();
    /**
     * Webkamera kezelő.
     */
    private static final WebcamManager WEBCAM_MANAGER = new WebcamManager();

    /**
     * Konstruktor.
     */
    public GobelinServer() {
        super(BUFFER_SIZE, BUFFER_SIZE);
    }

    @Override
    public void cliSetShell(Shell shell) {
        sh = shell;
    }

    /**
     * IP cím lekérdezése.
     *
     * @return ip cím
     * @throws Exception kivétel
     */
    public String getIP() throws Exception {

        for (NetworkInterface iface : Collections.list(NetworkInterface.getNetworkInterfaces())) {
            for (InetAddress address : Collections.list(iface.getInetAddresses())) {
                if (!address.isLoopbackAddress() && !address.isLinkLocalAddress()) {
                    return address.getHostAddress().trim();
                }
            }
        }

        return "unknown";
    }

    /**
     * Szerver indítása.
     *
     * @throws java.lang.Exception kivétel
     */
    public void startServer() throws Exception {

        WEBCAM_MANAGER.scan();
        DEVICE_MANAGER.scan();

        sh = ShellFactory.createConsoleShell("", "", new GobelinServer());

        Kryo kryo = getKryo();
        kryo.register(CommandRequest.class);
        kryo.register(CommandResponse.class);
        kryo.register(byte[].class);
        kryo.register(ImageResponse.class);

        addListener(new Listener() {
            @Override
            public void received(Connection connection, Object object) {

                if (object instanceof CommandRequest) {
                    CommandRequest request = (CommandRequest) object;

                    if (null == request.text) {
                        return;
                    }

                    MyLog.debug("CLIENT:" + request.text);
                    int responseChannel = ResponseManager.add();

                    /* HACK: Szükséges külön szálon futtatni, hogy a hosszú ideig tartó folyamatok során
                       tudjon továbbra is kommunikálni a kyronet, mert ha nem tudja fenntartani a kapcsolatot
                       próbacsomagok küldésével, akkor megszakad a kapcsolat. */
                    Thread t = new Thread(() -> {
                        try {
                            sh.processLine(request.text + " " + responseChannel);
                        } catch (CLIException e) {
                            ResponseManager.set(responseChannel, e.getLocalizedMessage());
                        }

                        CommandResponse response = new CommandResponse();
                        response.text = ResponseManager.pop(responseChannel);
                        MyLog.debug("SERVER RESPONSE:" + response.text.substring(0, Math.min(40, response.text.length())) + "...[" + response.text.length() + "]");

                        if (response.text.startsWith(IMAGE_TAG)) {
                            MyLog.debug("SERVER: Compressing data...");
                            ImageResponse r = new ImageResponse();
                            r.data = GzipUtil.zip(response.text);
                            MyLog.debug("SERVER: Data compressed [" + response.text.getBytes().length + "->" + r.data.length + "=" + (response.text.getBytes().length - r.data.length) + "]");
                            connection.sendTCP(r);
                        } else {
                            connection.sendTCP(response);
                        }
                    });
                    t.start();
                }
            }
        });
        this.start();
        try {
            bind(GobelinConsole.PORT_TCP, GobelinConsole.PORT_UDP);
            MyLog.info("Server is listening on " + getIP() + ":" + GobelinConsole.PORT_TCP + "...");
        } catch (Exception ex) {
            MyLog.error("", ex);
        }

    }

    @Override
    @Command(description = "Prints a text to the server")
    public void serverPrint(int responseChannel) {
        String result = "serverPrint " + responseChannel;
        ResponseManager.set(responseChannel, result);
    }

    @Override
    public void serverPrint() {
    }

    @Override
    @Command(description = "Send a command to the default device")
    public void doCommand(String command, int responseChannel) {

        if (DEVICE_MANAGER.size() > 1) {
            ResponseManager.set(responseChannel, "There are more than one device.");
            return;
        }

        doCommand(DEVICE_MANAGER.getFirst().getID(), command, responseChannel);
    }

    @Override
    public void doCommand(String command) {
    }

    @Override
    @Command(description = "Send a command to the specified device")
    public void doCommand(String deviceID, String command, int responseChannel) {

        Device d = DEVICE_MANAGER.get(deviceID);
        if (null == d) {
            ResponseManager.set(responseChannel, "No device found with ID:'" + deviceID + "'");
            return;
        }

        String result = d.sendCommandAndWait(command);

        ResponseManager.set(responseChannel, result);
    }

    @Override
    public void doCommand(String deviceID, String command) {
    }

    @Override
    @Command(description = "List connected devices.")
    public void listDevices(int responseChannel) {

        String result = Arrays.toString(DEVICE_MANAGER.keySet().toArray());

        ResponseManager.set(responseChannel, result);
    }

    @Override
    public void listDevices() {
    }

    @Override
    @Command(description = "List connected webcams.")
    public void listWebcams(int responseChannel) {

        ResponseManager.set(responseChannel, "\n" + WEBCAM_MANAGER.toString());
    }

    @Override
    public void listWebcams() {
    }

    @Override
    @Command(description = "Opens a webcam specified by index.")
    public void openWebcam(
            @Param(name = "index", description = "Index of webcam.") int index,
            int responseChannel
    ) {

        ServerCamera sc = WEBCAM_MANAGER.get(index);

        if (null == sc) {
            ResponseManager.set(responseChannel, "There are no webcams with index:" + index);
            return;
        }

        if (sc.isOpened()) {
            ResponseManager.set(responseChannel, "Webcam is already opened.");
            return;
        }

        sc.open();
        ResponseManager.set(responseChannel, "Webcam is opened.");
    }

    @Override
    public void openWebcam() {
    }

    @Override
    @Command(description = "Opens the default webcam.")
    public void openWebcam(int responseChannel) {
        if (WEBCAM_MANAGER.size() > 1) {
            ResponseManager.set(responseChannel, "There are more than one webcam.");
            return;
        }

        openWebcam(0, responseChannel);
    }

    @Override
    @Command(description = "Close a webcam specified by index.")
    public void closeWebcam(
            @Param(name = "index", description = "Index of webcam.") int index,
            int responseChannel
    ) {

        ServerCamera sc = WEBCAM_MANAGER.get(index);

        if (null == sc) {
            ResponseManager.set(responseChannel, "There are no webcams with index:" + index);
            return;
        }

        if (!sc.isOpened()) {
            ResponseManager.set(responseChannel, "Webcam is already closed.");
            return;
        }

        sc.close();
        ResponseManager.set(responseChannel, "Webcam is closed.");
    }

    @Override
    public void closeWebcam() {
    }

    @Override
    @Command(description = "Close the default webcam.")
    public void closeWebcam(int responseChannel) {
        if (WEBCAM_MANAGER.size() > 1) {
            ResponseManager.set(responseChannel, "There are more than one webcam.");
            return;
        }

        closeWebcam(0, responseChannel);
    }

    @Override
    @Command(description = "Gets a webcam image specified by index.")
    public void getWebcamImage(
            @Param(name = "index", description = "Index of webcam.") int index,
            int responseChannel
    ) {

        getWebcamImageResized(0, "", responseChannel);
    }

    @Override
    public void getWebcamImage() {
    }

    @Override
    @Command(description = "Gets an image from the default webcam.")
    public void getWebcamImage(int responseChannel) {
        getWebcamImage(0, responseChannel);
    }

    @Override
    public void getWebcamImageResized(int index, String size) {
    }

    @Override
    @Command(description = "Gets a webcam image with size by a specified index.")
    public void getWebcamImageResized(
            @Param(name = "index", description = "Index of webcam.") int index,
            @Param(name = "size", description = "Image size.") String size,
            int responseChannel
    ) {
        ServerCamera sc = WEBCAM_MANAGER.get(index);

        if (null == sc) {
            ResponseManager.set(responseChannel, "There are no webcams with index:" + index);
            return;
        }

        if (!sc.isOpened()) {
            ResponseManager.set(responseChannel, "Webcam is closed.");
            return;
        }

        BufferedImage image = sc.getImage();

        // Kép átméretezése ha szükséges.
        String sizes[] = new String[]{"1080p", "720p", "480p", "360p", "240p"};
        if (!size.isEmpty()) {
            if (!Arrays.asList(sizes).contains(size)) {
                ResponseManager.set(responseChannel, "Unsupported image size: " + size + ".\nChoose one from the following list: " + Arrays.toString(sizes));
                return;
            }
            image = Scalr.resize(image, Method.ULTRA_QUALITY, Mode.FIT_TO_HEIGHT, 1, Integer.parseInt(size.replaceAll("p", "")));
        }
        String result = "";

        MyLog.debug("Converting to Base64...");

        final ByteArrayOutputStream os = new ByteArrayOutputStream();

        try {
            ImageIO.write(image, "png", Base64.getEncoder().wrap(os));
            MyLog.debug("Converted to Base64.");
            result = os.toString(StandardCharsets.UTF_8.name());
            MyLog.debug("Converting to string...");
        } catch (IOException e) {
            MyLog.error(e.getLocalizedMessage(), e);
        }

        MyLog.debug("Converted to string.");
        ResponseManager.set(responseChannel, IMAGE_TAG + result);
    }

    @Override
    public void getWebcamImageResized(String size) {
    }

    @Override
    @Command(description = "Gets an image from the default webcam with a specified size.")
    public void getWebcamImageResized(
            @Param(name = "size", description = "Image size.") String size,
            int responseChannel) {
        getWebcamImageResized(0, size, responseChannel);
    }

}
