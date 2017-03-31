package protocols;


import channels.ControlChannel;
import channels.DataChannel;
import server.Metadata;
import utils.GoodGuy;
import utils.Message;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Arrays;

public class Backup extends Protocol {
    public static void sendFileChunks(Metadata metadata, DataChannel mdb, String path, String version, String senderId, String replicationDeg) throws IOException {
        FileInfo fi = Protocol.generateFileInfo(path);
        String hashFileId = Message.buildHash(fi.fileId);

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

            try {
                Thread.sleep(400);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            mdb.send(msg);
            i++;
        }

        // Metadata
        metadata.addMetadata(fi.filename, fi.extension, path, hashFileId,i);

    }

    public static void sendStoredMessage(ControlChannel mc,String fileID, int chunkNo, String serverID) {
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
