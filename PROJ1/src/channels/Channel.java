package channels;

import server.Server;
import utils.Message;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

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
            System.err.println("Error: " + e.getMessage());
            return;
        }

        shutdown = false;
    }

    public void send(Message msg) throws IOException {
        byte[] sendMsg = msg.getMessage();
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
