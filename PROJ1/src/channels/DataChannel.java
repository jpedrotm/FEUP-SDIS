package channels;

import filesystem.Chunk;
import filesystem.FileChunk;
import filesystem.FileManager;
import server.Server;
import utils.Message;

import java.io.IOException;
import java.net.DatagramPacket;
import java.nio.charset.StandardCharsets;

public class DataChannel extends Channel {

    public DataChannel(Server server, String addressStr, String portVar){
        super(server, addressStr,portVar);
    }

    @Override
    void handler() {

        DatagramPacket packet = new DatagramPacket(new byte[256], 256);

        try {
            socket.receive(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println(new String(packet.getData(), 0, packet.getLength()));

    }

    @Override
    public void run() {

    }
}
