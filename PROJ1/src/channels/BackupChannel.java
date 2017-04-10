package channels;

import metadata.Metadata;
import protocols.Protocol;
import server.Server;
import utils.Message;

import java.io.*;
import java.net.DatagramPacket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class BackupChannel extends Channel {

    public BackupChannel(Server server, String addressStr, String portVar){
        super(server, addressStr,portVar);
    }


    @Override
    public void run() {
        while(!shutdown)
        {
            DatagramPacket packet = new DatagramPacket(new byte[Message.MAX_CHUNK_SIZE],Message.MAX_CHUNK_SIZE);
            try {
                this.socket.receive(packet);
            } catch (IOException e) {
                System.err.println("Error: " + e.getMessage());
                continue;
            }

            Message message = new Message(packet);
            String[] headerFields = message.getHeaderFields();
            byte[] body = message.getBody();

            if (headerFields[Message.FieldIndex.SenderId].equals(server.getServerID()) || !headerFields[Message.FieldIndex.Version].equals(server.getVersion()))
                continue;

            System.out.println(message.getHeader());

            switch(headerFields[Message.FieldIndex.MessageType]) {
                case Protocol.MessageType.Chunk:
                    restoreFileChunk(headerFields, body);
                    break;
                default:
                    break;
            }
        }

    }

    public void restoreFileChunk(String[] headerFields, byte[] body){
        String fileID = headerFields[Message.FieldIndex.FileId];
        int chunkNo = Integer.parseInt(headerFields[Message.FieldIndex.ChunkNo]);

        if (!Metadata.instance().hasFile(fileID))
            return;

        String fileName = Metadata.instance().getFileName(fileID);

        try {
            String path="storage/restored/"+fileName;
            Path pathToFile= Paths.get(path);

            if (chunkNo == 0) {
                Files.createDirectories(pathToFile.getParent());
            }

            FileOutputStream fos = new FileOutputStream (new File(path),true);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            baos.write(body);
            baos.writeTo(fos);
            baos.close();
            fos.close();

        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
            return;
        }


    }

    public void sendChunk(Message msg, DatagramPacket packet) {
        Socket replySocket = null;
        try {
            replySocket = new Socket(packet.getAddress(), port);
            ObjectOutputStream oos = new ObjectOutputStream(replySocket.getOutputStream());
            oos.writeObject(msg);
        } catch (IOException e) {}
        finally {
            if (replySocket != null)
                try {
                    replySocket.close();
                } catch (IOException e) {}
        }
    }
}
