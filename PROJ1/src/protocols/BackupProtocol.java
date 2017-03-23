package protocols;


import channels.ControlChannel;
import utils.Message;

import java.io.*;
import java.net.DatagramPacket;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class BackupProtocol extends Protocol {
                                        // BACKUP CHANNEL !!!
    public static boolean sendFileChunks(ControlChannel mc, String path, String version, String senderId, String replicationDeg) throws IOException {
        File file = new File(path);
        FileInputStream is = new FileInputStream(file);
        String filename = file.getName();

        Path filePath = Paths.get(path);
        BasicFileAttributes attr = Files.readAttributes(filePath, BasicFileAttributes.class);
        String lastModified = String.valueOf(attr.lastModifiedTime());
        String owner = String.valueOf(Files.getOwner(filePath));
        String fileId=filename+owner+lastModified;
        byte[] content = new byte[Message.MAX_CHUNK_SIZE];

        String hashFileId=Message.buildHash(fileId);

        int i=0;
        while (true) {
            String header = Message.buildHeader(MessageType.Putchunk, version, senderId,hashFileId,Integer.toString(i), replicationDeg);
            int bytesRead = is.read(content, 0, Message.MAX_CHUNK_SIZE - header.length() - 5);
            if (bytesRead == -1) {
                break;
            }

            byte[] body = Arrays.copyOf(content, bytesRead);
            Message msg = new Message(header, new String(body, StandardCharsets.US_ASCII));
            mc.send(msg);
            i++;
        }

        return true;
    }

}
