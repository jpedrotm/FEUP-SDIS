package channels;

import filesystem.Chunk;
import filesystem.File;
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
        while (true) {
            DatagramPacket packet = new DatagramPacket(new byte[MAX_PACKET_SIZE], MAX_PACKET_SIZE);
            try {
                this.socket.receive(packet);
            } catch (IOException e) {
                e.printStackTrace();
            }

            Message message = new Message(packet);
            String[] headerFields = message.getHeaderFields();
            String body = message.getBody();

            switch (headerFields[FieldIndex.MessageType]) {
                case "PUTCHUNK":
                    putChunk(headerFields, body);
                    break;
            }
        }
    }


    private void putChunk(String[] headerFields, String body) {
        String senderID = headerFields[FieldIndex.SenderId];
        String fileID = headerFields[FieldIndex.FileId];
        int chunkNo = Integer.parseInt(headerFields[FieldIndex.ChunkNo]);
        int replicationDegree = Integer.parseInt(headerFields[FieldIndex.ReplicationDeg]);

        if (senderID == server.getServerID())
            return;

        File file;
        if (FileManager.instance().hasFile(fileID))
            file = FileManager.instance().getFile(fileID);
        else
            file = new File(fileID);


        if (file.hasChunk(chunkNo)) {
            // server.sendStored(fileId, chunkNo);
            return;
        }
        else {
            Chunk chunk = new Chunk(chunkNo, replicationDegree, body.getBytes(StandardCharsets.US_ASCII));
            file.addChunk(chunk);
            try {
                chunk.storeContent(fileID);
                // server.sendStored(fileId, chunkNo);
            } catch (IOException e) { /* Do nothing */ }
        }
    }
}
