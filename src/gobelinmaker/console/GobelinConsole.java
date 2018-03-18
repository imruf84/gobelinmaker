package gobelinmaker.console;

import asg.cliche.CLIException;
import gobelinmaker.server.CommandResponse;
import gobelinmaker.server.CommandRequest;
import asg.cliche.Command;
import asg.cliche.Param;
import asg.cliche.Shell;
import asg.cliche.ShellDependent;
import asg.cliche.ShellFactory;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import gobelinmaker.MyLog;
import gobelinmaker.server.GobelinServer;
import gobelinmaker.server.GzipUtil;
import gobelinmaker.server.IServerCommands;
import gobelinmaker.server.ImageResponse;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.imageio.ImageIO;

/**
 * Konzol alaposztálya.
 *
 * @author igalambo
 */
public class GobelinConsole implements ShellDependent, IServerCommands {

    /**
     * Kliens.
     */
    private Client client = null;
    /**
     * Parancsértelmező.
     */
    private Shell sh = null;
    /**
     * TCP port.
     */
    public static final int PORT_TCP = 8086;
    /**
     * UDP port.
     */
    public static final int PORT_UDP = PORT_TCP + 1;
    /**
     * Válaszra való várakozás jelzője.
     */
    private final AtomicBoolean responseReceived = new AtomicBoolean(true);
    /**
     * Szerver válasz időtúllépés ideje.
     */
    private final long SERVER_COMMAND_TIMEOUT = 15000;
    /**
     * Legutóbbi csatlakozás ip címe.
     */
    private String prevIP = "";

    /**
     * Konzol indítása.
     *
     * @throws IOException kivétel
     * @throws java.lang.InterruptedException kivétel
     */
    public void start() throws IOException, InterruptedException {

        // Külön szálban futtatjuk a parsert, hogy programból is küldhessünk utasításokat.
        Thread consoleThread = new Thread(() -> {
            try {
                sh = ShellFactory.createConsoleShell("",
                        "Gobelin Maker Console Client v0.1\n"
                        + "Enter '?list-all' or '?la' to list all available commands or 'exit' to quit...",
                        new GobelinConsole());
                sh.setDisplayTime(true);
                sh.commandLoop();
            } catch (IOException ex) {
                MyLog.error(ex.getLocalizedMessage(), ex);
            }
        });

        consoleThread.start();
        TimeUnit.SECONDS.sleep(1);
    }

    /**
     * Shell lekérdezése.
     *
     * @return shell
     */
    private Shell getShell() {
        return sh;
    }

    /**
     * Parancs futtatása.
     *
     * @param command parancs
     * @throws asg.cliche.CLIException kivétel
     */
    public void runCommand(String command) throws CLIException {
        getShell().processLine(command);
    }

    /**
     * Parancs futtatása szerveren.
     *
     * @param command parancs
     */
    public void runCommandOnServer(String command) {
        runCommandOnServer(command, 0);
    }

    /**
     * Parancs futtatása szerveren.
     *
     * @param command parancs
     * @param timeout időtúllépés
     */
    public void runCommandOnServer(String command, long timeout) {

        // Ha nincs kapcsolat szerverrel akkor kilépünk.
        if (!isConnected()) {
            MyLog.warning("You are not connected!");
            return;
        }

        // Egyébként küldjük a parancsot...
        CommandRequest request = new CommandRequest();
        request.text = command;
        client.sendTCP(request);

        //...és megvárjuk a választ.
        responseReceived.set(false);
        long startTime = System.currentTimeMillis();
        while (!responseReceived.get()) {
            long estimatedTime = System.currentTimeMillis() - startTime;
            if (timeout > 0 && estimatedTime > timeout) {
                MyLog.info("Timeout");
                responseReceived.set(true);
                return;
            }
            try {
                TimeUnit.MILLISECONDS.sleep(1);
            } catch (InterruptedException ex) {
            }
        }
    }

    /**
     * Szöveg kiírása.
     *
     * @param text szöveg
     */
    @Command(description = "Prints a text to the console.")
    public void print(
            @Param(name = "text", description = "Text to print.") String text
    ) {
        MyLog.println(text);
    }

    /**
     * Szerverek felderítése.
     */
    @Command(description = "Prints all available servers on network.")
    public void discover() {
        MyLog.info("Discovering servers...");
        Client c = new Client();
        List<InetAddress> address = c.discoverHosts(PORT_UDP, 5000);
        c.close();
        if (address.isEmpty()) {
            MyLog.info("There are no running servers on the network.");
            return;
        }

        // Nincs szükségünk minden (redundáns) ip címre.
        address.forEach((addr) -> {
            String ip = addr.toString().replace("/", "");
            if (ip.startsWith("192.")) {
                MyLog.println(ip);
            }
        });

    }
    
    /**
     * Első szerver ip címének a lekérdezése.
     * 
     * @return első szerver ip címe vagy null hiányzó szerverek esetén
     */
    public String getFirstServerIp() {
        Client c = new Client();
        List<InetAddress> address = c.discoverHosts(PORT_UDP, 5000);
        c.close();
        if (address.isEmpty()) {
            return null;
        }

        // Nincs szükségünk minden (redundáns) ip címre.
        for (InetAddress a : address) {
            String ip = a.toString().replace("/", "");
            if (ip.startsWith("192.")) {
                return ip;
            }
        }
        
        return null;
    }

    /**
     * Csatlakozás állapotának a lekérdezése.
     *
     * @return szerverrel való kapcsolat esetén igaz egyébként hamis
     */
    @Command(description = "Test the connection to a server.")
    public boolean isConnected() {
        return client != null && client.isConnected();
    }

    /**
     * Újracsatlakozás.
     */
    @Command(description = "Reconnect to the server.")
    public void reconnect() {
        if (prevIP.isEmpty()) {
            MyLog.warning("There are no IP address from a previous connection.");
            return;
        }

        connect(prevIP);
    }
    
    /**
     * Csatlakozás az első szerverhez.
     */
    @Command(description = "Connects to the first server.")
    public void connect(
    ) {
        String ip = getFirstServerIp();
        
        if (null == ip) {
            MyLog.warning("There are no servers found to connect.");
        }
        
        connect(ip);
    }

    /**
     * Csatlakozás szerverhez.
     *
     * @param ip IP cím
     */
    @Command(description = "Connects to the specified server.")
    public void connect(
            @Param(name = "ip", description = "Server IP.") String ip
    ) {

        // Ha már csatlakoztunk akkor kilépünk.
        if (isConnected()) {
            MyLog.warning("You are already connected.");
            return;
        }

        prevIP = ip;

        // Csatlakozás.
        client = new Client(GobelinServer.BUFFER_SIZE, GobelinServer.BUFFER_SIZE);
        Kryo kryo = client.getKryo();
        kryo.register(CommandRequest.class);
        kryo.register(CommandResponse.class);
        kryo.register(byte[].class);
        kryo.register(ImageResponse.class);
        AtomicBoolean connected = new AtomicBoolean(false);
        client.addListener(new Listener() {
            @Override
            public void connected(Connection c) {
                connected.set(true);
            }

            @Override
            public void disconnected(Connection c) {
                client = null;
            }

            @Override
            public void received(Connection connection, Object pObject) {

                Object object = pObject;

                if (object instanceof ImageResponse) {
                    ImageResponse response = (ImageResponse) object;
                    MyLog.debug("Image received [" + response.data.length + "]");
                    CommandResponse cr = new CommandResponse();
                    cr.text = GzipUtil.unzip(response.data);
                    object = cr;
                }

                if (object instanceof CommandResponse) {
                    CommandResponse response = (CommandResponse) object;

                    MyLog.debug("SERVER:" + response.text.substring(0, Math.min(response.text.length(), 20)) + "...[" + response.text.length() + "]");

                    // Ha képet kaptunk, akkor megjelenítjük.
                    if (response.text.startsWith(GobelinServer.IMAGE_TAG)) {

                        String imageString = response.text.replaceFirst(GobelinServer.IMAGE_TAG, "");
                        BufferedImage image;
                        try {
                            image = ImageIO.read(new ByteArrayInputStream(Base64.getDecoder().decode(imageString)));
                            ImageViewer iw = new ImageViewer(image);
                            iw.setVisible(true);
                        } catch (IOException ex) {
                            MyLog.error(ex.getLocalizedMessage(), ex);
                        }

                    } else {

                        // Egyébként kiírjuk a szerver válaszát a konzolra.
                        MyLog.println("SERVER:" + response.text);
                    }
                    responseReceived.set(true);
                }
            }
        });
            try {
                client.start();
                client.connect(5000, ip, PORT_TCP, PORT_UDP);
                while (!connected.get()) {
                }
                getShell().setPath(Arrays.asList(ip));
                MyLog.info("Connected to server: " + client.getRemoteAddressTCP().getAddress().toString().replaceAll("/", ""));
            } catch (IOException e) {
                MyLog.error(e.getLocalizedMessage().replaceAll("/", ""), e);
            }

    }

    /**
     * Szerverrel való kapcsolat bontása.
     */
    @Command(description = "Disconnects from the server.")
    public void disconnect() {

        // Ha nem vagyunk csatlakozva akkor kilépünk.
        if (!isConnected()) {
            MyLog.warning("You are already disconnected.");
            return;
        }

        // Egyébként bontjuk a kapcsolatot.
        client.close();
        while (null != client) {
        }
        getShell().setPath(Arrays.asList(""));
        MyLog.info("Disconnected.");
    }

    /**
     * Shell megadása.
     *
     * @param shell shell
     */
    @Override
    public void cliSetShell(Shell shell) {
        sh = shell;
    }

    @Override
    @Command(description = "Prints a text to the server")
    public void serverPrint() {
        runCommandOnServer("server-print");
    }

    @Override
    public void serverPrint(int responseChannel) {
    }
    
    @Override
    @Command(description = "Send a command to the default device to run.")
    public void doCommand(
            @Param(name = "command", description = "The command to run.") String command) {
        runCommandOnServer("do-command '" + command + "'");
    }

    @Override
    public void doCommand(String command, int responseChannel) {
    }

    @Override
    @Command(description = "Send a command to the specified device to run.")
    public void doCommand(
            @Param(name = "deviceID", description = "Device ID.") String deviceID,
            @Param(name = "command", description = "The command to run.") String command) {
        runCommandOnServer("do-command '" + deviceID + "' '" + command + "'");
    }

    @Override
    public void doCommand(String deviceID, String command, int responseChannel) {
    }

    @Override
    @Command(description = "List connected devices.")
    public void listDevices() {
        runCommandOnServer("list-devices");
    }

    @Override
    public void listDevices(int responseChannel) {
    }

    @Override
    @Command(description = "List connected webcams.")
    public void listWebcams() {
        runCommandOnServer("list-webcams");
    }

    @Override
    public void listWebcams(int responseChannel) {
    }

    @Override
    @Command(description = "Opens the default webcam.")
    public void openWebcam(
    ) {
        runCommandOnServer("open-webcam");
    }
    
    @Override
    @Command(description = "Opens a webcam specified by index.")
    public void openWebcam(
            @Param(name = "index", description = "Index of webcam.") int index
    ) {
        runCommandOnServer("open-webcam " + index);
    }

    @Override
    public void openWebcam(int index, int responseChannel) {
    }

    @Override
    @Command(description = "Close the default webcam.")
    public void closeWebcam(
    ) {
        runCommandOnServer("close-webcam");
    }
    
    @Override
    @Command(description = "Close a webcam specified by index.")
    public void closeWebcam(
            @Param(name = "index", description = "Index of webcam.") int index
    ) {
        runCommandOnServer("close-webcam " + index);
    }

    @Override
    public void closeWebcam(int index, int responseChannel) {
    }

    @Override
    @Command(description = "Gets an image from the default webcam.")
    public void getWebcamImage(
    ) {
        runCommandOnServer("get-webcam-image");
    }
    
    @Override
    @Command(description = "Gets a webcam image specified by index.")
    public void getWebcamImage(
            @Param(name = "index", description = "Index of webcam.") int index
    ) {
        runCommandOnServer("get-webcam-image " + index);
    }

    @Override
    public void getWebcamImage(int index, int responseChannel) {
    }

}
