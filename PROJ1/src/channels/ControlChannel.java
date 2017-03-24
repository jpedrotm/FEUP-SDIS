package channels;

import filesystem.FileChunk;
import filesystem.FileManager;
import server.Server;
import utils.Message;

import java.io.IOException;
import java.net.DatagramPacket;

public class ControlChannel extends Channel {

    public ControlChannel(Server server, String addressStr, String portVar){
        super(server, addressStr,portVar);
    }

    @Override
    void handler() {


    }

    @Override
    public void run() {

        while (!shutdown) {

            DatagramPacket packet = new DatagramPacket(new byte[Message.MAX_CHUNK_SIZE], Message.MAX_CHUNK_SIZE);
            try {
                this.socket.receive(packet);
            } catch (IOException e) {
                e.printStackTrace();
            }

            Message message = new Message(packet);
            String[] headerFields = message.getHeaderFields();

            System.out.println(message.getMessage());

            switch (headerFields[FieldIndex.MessageType]) {
                case "STORED":
                    store(headerFields);
                    break;
                case "DELETE":
                    delete(headerFields);
                    break;
                case "REMOVED":
                    removed(headerFields);
                    break;
            }
        }
    }

    private void removed(String[] headerFields) {

    }

    private void delete(String[] headerFields) {

    }

    private void store(String[] headerFields) {
        String senderID = headerFields[FieldIndex.SenderId];
        String fileID = headerFields[FieldIndex.FileId];
        String chunkNumber = headerFields[FieldIndex.ChunkNo];

        if (senderID.equals(server.getServerID()))
            return;

        if (FileManager.instance().hasFile(fileID)) {
            FileChunk file = FileManager.instance().getFile(fileID);
            int chunkNo = Integer.parseInt(chunkNumber);
            file.updateChunk(chunkNo);
        }
    }
}
