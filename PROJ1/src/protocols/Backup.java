package protocols;


import channels.ControlChannel;
import channels.DataChannel;
import metadata.Metadata;
import utils.GoodGuy;
import utils.Message;

import java.io.*;
import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class Backup extends Protocol {
    public static void sendFileChunks(DataChannel mdb, String path, String version, String senderId, String replicationDeg) throws IOException {
        FileInfo fi = Protocol.generateFileInfo(path);
        String hashFileId = Message.buildHash(fi.fileId);

        // Metadata
        Metadata.instance().addMetadata(fi.filename, fi.extension, path, hashFileId,Integer.parseInt(replicationDeg));

        int i = 0;
        while (true) {
            byte[] content = new byte[Message.MAX_CHUNK_SIZE];
            String header = Message.buildHeader(MessageType.Putchunk, version, senderId,hashFileId,Integer.toString(i), replicationDeg);
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
