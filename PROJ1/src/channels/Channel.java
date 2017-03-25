package channels;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.nio.charset.StandardCharsets;

import server.Server;
import utils.Message;

abstract class Channel implements Runnable {
    protected MulticastSocket socket;
    protected InetAddress address;
    protected int port;
    protected Server server;
    protected volatile boolean shutdown;

    public Channel(Server server, String addressStr, String portVar) {

        this.server = server;

        try {
            this.address = InetAddress.getByName(addressStr);
            this.port = Integer.parseInt(portVar);

            socket = new MulticastSocket(port);
            socket.joinGroup(address);
        } catch (IOException e) {
            e.printStackTrace();
        }

        shutdown = false;
    }

    abstract void handler();

    public void send(Message msg) throws IOException {
        byte[] sendMsg = msg.getMessage().getBytes(StandardCharsets.US_ASCII);
        DatagramPacket packet = new DatagramPacket(sendMsg, sendMsg.length, address, port);
        socket.send(packet);
    }

    public MulticastSocket getSocket() {
        return socket;
    }

    public void shutdown() {
        shutdown = true;
    }
}
