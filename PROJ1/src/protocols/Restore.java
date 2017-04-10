package protocols;

import channels.BackupChannel;
import channels.ControlChannel;
import metadata.Metadata;
import utils.Message;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Restore extends Protocol {

    public static void receiveFileChunks(ControlChannel mc, BackupChannel mdr, String path, String version, String senderID) throws IOException {
        boolean repeatedChunk = false;
        int numChunks = Metadata.instance().getFileNumChunks(path, Metadata.InfoRequest.FILEPATH);
        String hashFileId = Metadata.instance().getHashFileId(path);

        ServerSocket ss = new ServerSocket(mdr.getPort(), 1);
        int i = 0;

        while(i < numChunks) {
            if (!repeatedChunk) {
                String header = Message.buildHeader(MessageType.GetChunk, version, senderID, hashFileId,Integer.toString(i));
                Message msg = new Message(header);
                mc.send(msg);
            }

            try {
                Thread.sleep(400);
            } catch (InterruptedException e) {}
            Socket socket = ss.accept();

            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            try {
                Message receiveMsg = (Message) ois.readObject();
                socket.close();
                System.out.println(receiveMsg.getHeader());

                if (Integer.parseInt(receiveMsg.getHeaderFields()[Message.FieldIndex.ChunkNo]) != i) {
                    repeatedChunk = true;
                    continue;
                }
                else {
                    repeatedChunk = false;
                }

                String fileName = Metadata.instance().getFileName(hashFileId);
                try {
                    String storePath="storage/restored/"+fileName;
                    Path pathToFile= Paths.get(storePath);

                    if (i == 0) {
                        Files.createDirectories(pathToFile.getParent());
                    }

                    FileOutputStream fos = new FileOutputStream (new File(storePath),true);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    baos.write(receiveMsg.getBody());
                    baos.writeTo(fos);
                    baos.close();
                    fos.close();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

            i++;
        }

        ss.close();
    }

}