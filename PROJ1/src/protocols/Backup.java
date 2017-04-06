package protocols;


import channels.ControlChannel;
import channels.DataChannel;
import metadata.Metadata;
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

            byte[] body = Arrays.copyOf(content, bytesRead);
            Message msg = new Message(header, body);

            mdb.send(msg);
            Metadata.instance().addChunkMetadata(path, i, Integer.parseInt(replicationDeg));
            i++;
        }
    }

    public static void sendStoredMessage(ControlChannel mc, String fileID, int chunkNo, String serverID) {
        String header = Message.buildHeader(MessageType.Stored,"1.0", serverID, fileID, Integer.toString(chunkNo));
        Message msg = null;

        try {
            msg = new Message(header);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            mc.send(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
