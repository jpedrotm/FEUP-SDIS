package protocols;


import channels.ControlChannel;
import channels.DataChannel;
import utils.Message;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Arrays;

public class BackupProtocol extends Protocol {
    public static boolean sendFileChunks(DataChannel mdb, String path, String version, String senderId, String replicationDeg) throws IOException {
        File file = new File(path);
        FileInputStream is = new FileInputStream(file);
        String filename = file.getName();

        Path filePath = Paths.get(path);
        BasicFileAttributes attr = Files.readAttributes(filePath, BasicFileAttributes.class);
        String lastModified = String.valueOf(attr.lastModifiedTime());
        String owner = String.valueOf(Files.getOwner(filePath));
        String fileId=filename+owner+lastModified;

        String hashFileId=Message.buildHash(fileId);

        int i=0;
        while (true) {
            byte[] content = new byte[Message.MAX_CHUNK_SIZE];
            String header = Message.buildHeader(MessageType.Putchunk, version, senderId,hashFileId,Integer.toString(i), replicationDeg);
            int bytesRead = is.read(content, 0, Message.MAX_CHUNK_SIZE - header.length() - 5);
            if (bytesRead == -1) {
                break;
            }

            byte[] body = Arrays.copyOf(content, bytesRead);
            Message msg = new Message(header, new String(body, StandardCharsets.US_ASCII));
            System.out.println(msg.getMessage());
            mdb.send(msg);
            i++;
        }

        return true;
    }

    public static void sendStoredMessage(ControlChannel mc,String fileID, int chunckNo, String serverID){

        String header=Message.buildHeader(MessageType.Stored,"1.0",serverID,fileID,Integer.toString(chunckNo));
        Message msg=new Message(header);
        try {
            mc.send(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
