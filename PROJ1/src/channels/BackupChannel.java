package channels;

import protocols.Protocol;
import server.Server;
import utils.Message;

import java.io.*;
import java.net.DatagramPacket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class BackupChannel extends Channel {

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
            DatagramPacket packet = new DatagramPacket(new byte[Message.MAX_CHUNK_SIZE],Message.MAX_CHUNK_SIZE);
            try {
                this.socket.receive(packet);
            } catch (IOException e) {
                e.printStackTrace();
            }

            Message message = new Message(packet);
            String[] headerFields = message.getHeaderFields();
            byte[] body = message.getBody();

            System.out.println(message.getHeader());

            switch(headerFields[Message.FieldIndex.MessageType]) {
                case Protocol.MessageType.Chunk:
                    restoreFileChunk(headerFields,body);
                    break;
            }
        }

    }

    public void restoreFileChunk(String[] headerFields, byte[] body){
        String fileID=headerFields[Message.FieldIndex.FileId];
        int chunkNo = Integer.parseInt(headerFields[Message.FieldIndex.ChunkNo]);

        try {
            String path="storage/restored/ola.pdf";
            Path pathToFile= Paths.get(path);

            if(chunkNo==0){
                Files.createDirectories(pathToFile.getParent());
            }

            FileOutputStream fos = new FileOutputStream (new File(path),true);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            baos.write(body);
            baos.writeTo(fos);
            baos.close();
            fos.close();
            //Não sei se é preciso fechar o ByteArrayOupuyStream

        } catch (IOException e) {
            e.printStackTrace();
        }


    }

}
