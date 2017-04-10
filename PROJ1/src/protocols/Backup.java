package protocols;


import channels.ControlChannel;
import channels.DataChannel;
import filesystem.Chunk;
import filesystem.FileChunk;
import metadata.Metadata;
import utils.GoodGuy;
import utils.Message;

import java.io.IOException;
import java.util.Arrays;

public class Backup extends Protocol {
    public static void sendFileChunks(DataChannel mdb, String path, String version, String senderId, String replicationDeg) throws IOException {
        FileInfo fi = Protocol.generateFileInfo(path);
        String hashFileId = Message.buildHash(fi.fileId);

        // Metadata
        Metadata.instance().addMetadata(fi.filename, fi.extension, path, hashFileId, Integer.parseInt(replicationDeg));
        Metadata.instance().getFileMetadata(hashFileId).startTransaction();

        int i = 0;
        while (true) {
            byte[] content = new byte[Message.MAX_CHUNK_SIZE];
            String header = Message.buildHeader(MessageType.Putchunk, version, senderId, hashFileId, Integer.toString(i), replicationDeg);
            int bytesRead = fi.is.read(content, 0, Message.MAX_CHUNK_SIZE - header.length() - 5);
            if (bytesRead == -1) {
                break;
            }

            Metadata.instance().addChunkMetadata(path, i, Integer.parseInt(replicationDeg));

            byte[] body = Arrays.copyOf(content, bytesRead);
            Message msg = new Message(header, body);

            GoodGuy.sleepRandomTime(0, 200);
            mdb.send(msg);
            i++;
        }

        Metadata.instance().getFileMetadata(hashFileId).stopTransaction();
    }

    public static void sendPutchunkFromRemoved(DataChannel mdb, String serverID, FileChunk fileChunk, Chunk chunk) {
        String header = Message.buildHeader(Protocol.MessageType.Putchunk, "1.0", serverID, fileChunk.getFileId(), Integer.toString(chunk.getNumber()), Integer.toString(chunk.getReplicationDegree()));
        byte[] body = new byte[0];
        try {
            body = chunk.getContent(Message.MAX_CHUNK_SIZE - header.length() - 5);
            Message message = new Message(header, body);
            mdb.sendPutchunkFromRemoved(message);
        } catch (IOException e) {}
    }

    public static void sendStoredMessage(ControlChannel mc, String fileID, int chunkNo, String serverID, String version) {
        String header = Message.buildHeader(MessageType.Stored,version, serverID, fileID, Integer.toString(chunkNo));

        try {
            Message msg = new Message(header);
            mc.send(msg);
        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
            return;
        }
    }

}
