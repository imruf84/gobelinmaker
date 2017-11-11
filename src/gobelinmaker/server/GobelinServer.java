package gobelinmaker.server;

import asg.cliche.CLIException;
import asg.cliche.Command;
import asg.cliche.Shell;
import asg.cliche.ShellDependent;
import asg.cliche.ShellFactory;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import gobelinmaker.MyLog;
import gobelinmaker.console.GobelinConsole;
import gobelinmaker.devicemanager.Device;
import gobelinmaker.devicemanager.DeviceManager;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Arrays;
import java.util.Collections;

/**
 * Szerver osztálya.
 *
 * @author igalambo
 */
public class GobelinServer extends Server implements ShellDependent, IServerCommands {

    /**
     * Parancsértelmező.
     */
    private Shell sh = null;
    private static final DeviceManager DEVICE_MANAGER = new DeviceManager();

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

        DEVICE_MANAGER.scan();

        sh = ShellFactory.createConsoleShell("", "", new GobelinServer());

        Kryo kryo = getKryo();
        kryo.register(CommandRequest.class);
        kryo.register(CommandResponse.class);

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

                    try {
                        sh.processLine(request.text + " " + responseChannel);
                    } catch (CLIException e) {
                        ResponseManager.set(responseChannel, e.getLocalizedMessage());
                    }

                    CommandResponse response = new CommandResponse();
                    response.text = ResponseManager.pop(responseChannel);
                    connection.sendTCP(response);
                }
            }
        });
        start();
        bind(GobelinConsole.PORT_TCP, GobelinConsole.PORT_UDP);
        MyLog.info("Server is listening on " + getIP() + ":" + GobelinConsole.PORT_TCP + "...");
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

}
