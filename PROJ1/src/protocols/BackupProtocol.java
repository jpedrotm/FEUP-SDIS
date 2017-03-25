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

public class BackupProtocol extends Protocol {
    public static void sendFileChunks(Metadata metadata, DataChannel mdb, String path, String version, String senderId, String replicationDeg) throws IOException {
        // File info
        File file = new File(path);
        FileInputStream is = new FileInputStream(file);
        String filename = file.getName();
        String extension = filename.substring(filename.lastIndexOf(".") + 1);

        // Path info
        Path filePath = Paths.get(path);
        BasicFileAttributes attr = Files.readAttributes(filePath, BasicFileAttributes.class);
        String lastModified = String.valueOf(attr.lastModifiedTime());
        String owner = String.valueOf(Files.getOwner(filePath));

        String fileId = filename+owner+lastModified;
        String hashFileId = Message.buildHash(fileId);

        // Metadata
        metadata.addMetadata(filename, extension, hashFileId);

        int i = 0;
        while (true) {
            byte[] content = new byte[Message.MAX_CHUNK_SIZE];
            String header = Message.buildHeader(MessageType.Putchunk, version, senderId,hashFileId,Integer.toString(i), replicationDeg);
            int bytesRead = is.read(content, 0, Message.MAX_CHUNK_SIZE - header.length() - 5);
            if (bytesRead == -1) {
                break;
            }

            byte[] body = Arrays.copyOf(content, bytesRead);
            Message msg = new Message(header, new String(body, StandardCharsets.US_ASCII));
            try {
                Thread.sleep(GoodGuy.sleepTime(400, 800));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            mdb.send(msg);
            i++;
        }
    }

    public static void sendStoredMessage(ControlChannel mc,String fileID, int chunkNo, String serverID) {
        String header = Message.buildHeader(MessageType.Stored,"1.0", serverID, fileID, Integer.toString(chunkNo));
        Message msg = new Message(header);
        try {
            mc.send(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
