package channels;

import filesystem.FileChunk;
import filesystem.FileManager;
import server.Server;
import utils.Message;
import utils.Message.FieldIndex;

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

            /*if (headerFields[FieldIndex.SenderId].equals(server.getServerID())) {
                return;
            }*/


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

            System.out.println(message.getHeader());
        }
    }

    private void removed(String[] headerFields) {

    }

    private void delete(String[] headerFields) {
        String fileID = headerFields[FieldIndex.FileId];

        if (FileManager.instance().hasFile(fileID)) {
            try {
                FileManager.instance().deleteFile(fileID);
            } catch (IOException e) {}
        }
    }

    private void store(String[] headerFields) {
        String fileID = headerFields[FieldIndex.FileId];
        String chunkNumber = headerFields[FieldIndex.ChunkNo];

        if (FileManager.instance().hasFile(fileID)) {
            FileChunk file = FileManager.instance().getFile(fileID);
            int chunkNo = Integer.parseInt(chunkNumber);
            file.updateChunk(chunkNo);
        }
    }
}
