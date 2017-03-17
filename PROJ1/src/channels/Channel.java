package channels;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.nio.charset.StandardCharsets;

import server.Server;
import utils.Message;

abstract class Channel implements Runnable {
    final int MAX_PACKET_SIZE = 64 * 1024;

    protected MulticastSocket socket;
    protected InetAddress address;
    protected int port;
    protected Server server;

    public Channel(Server server, String addressStr, String portVar) {

        this.server = server;

        try {
            this.address = InetAddress.getByName(addressStr);
            this.port = Integer.parseInt(portVar);

            socket = new MulticastSocket(port);
            socket.joinGroup(address); //provavelmente o join não é feito aqui depois julgo eu
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    abstract void handler();


    protected static class FieldIndex {
        public static final int MessageType = 0;
        public static final int Version = 1;
        public static final int SenderId = 2;
        public static final int FileId = 3;
        public static final int ChunkNo = 4;
        public static final int ReplicationDeg = 5;
    }

    public void send(Message msg) {
        byte[] sendMsg = msg.getMessage().getBytes(StandardCharsets.US_ASCII);
        DatagramPacket packet = new DatagramPacket(sendMsg, sendMsg.length, address, port);
    }
}
