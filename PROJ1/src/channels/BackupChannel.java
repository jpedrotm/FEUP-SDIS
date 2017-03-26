package channels;

import protocols.Protocol;
import server.Server;
import utils.Message;

import java.io.*;
import java.net.DatagramPacket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class BackupChannel extends Channel{

    public BackupChannel(Server server, String addressStr, String portVar){
        super(server, addressStr,portVar);
    }

    @Override
    void handler() {

    }


    @Override
    public void run() {
        while(!shutdown)
        {
            DatagramPacket packet=new DatagramPacket(new byte[Message.MAX_CHUNK_SIZE],Message.MAX_CHUNK_SIZE);
            try {
                this.socket.receive(packet);
            } catch (IOException e) {
                e.printStackTrace();
            }

            Message message=new Message(packet);
            String[] headerFields=message.getHeaderFields();
            String body=message.getBody();

            switch(headerFields[Message.FieldIndex.MessageType]){
                case Protocol.MessageType.Chunk:
                    restoreFileChunk(headerFields,body);
                    break;
            }
        }

    }

    public void restoreFileChunk(String[] headerFields,String body){
        String fileID=headerFields[Message.FieldIndex.FileId];
        int chunkNo = Integer.parseInt(headerFields[Message.FieldIndex.ChunkNo]);

        System.err.println("escreveu1");

        try {
            String path="storage/ola.txt";
            Path pathToFile= Paths.get(path);
            Files.createDirectories(pathToFile.getParent());
            Files.write(pathToFile,"ola".getBytes());

            System.err.println("escreveu2");

            //ByteArrayOutputStream baos = new ByteArrayOutputStream();
            //baos.write(body.getBytes());

        } catch (IOException e) {
            e.printStackTrace();
        }


    }

}
