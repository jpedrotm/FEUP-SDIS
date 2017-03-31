package channels;

import filesystem.FileChunk;
import filesystem.FileManager;
import protocols.Protocol;
import server.Metadata;
import server.Server;
import utils.GoodGuy;
import utils.Message;
import utils.Message.FieldIndex;

import java.io.*;
import java.net.DatagramPacket;

public class ControlChannel extends Channel {

    public ControlChannel(Server server, String addressStr, String portVar){
        super(server, addressStr,portVar);
    }

    @Override
    void handler() {


    }

    @Override
    public void run() {

        while (!shutdown) {

            DatagramPacket packet = new DatagramPacket(new byte[Message.MAX_CHUNK_SIZE], Message.MAX_CHUNK_SIZE);
            try {
                this.socket.receive(packet);
            } catch (IOException e) {
                e.printStackTrace();
            }

            Message message = new Message(packet);
            String[] headerFields = message.getHeaderFields();

            if (headerFields[FieldIndex.SenderId].equals(server.getServerID()))
                continue;

            System.out.println(message.getHeader());

            switch (headerFields[FieldIndex.MessageType]) {
                case "STORED":
                    store(headerFields);
                    break;
                case "DELETE":
                    delete(headerFields);
                    break;
                case "REMOVED":
                    removed(headerFields);
                    break;
                case "GETCHUNK":
                    restore(headerFields); //envia para o mdr a CHUNK message
                    break;
            }

        }
    }

    private void removed(String[] headerFields) {

    }

    private void delete(String[] headerFields) {
        String fileID = headerFields[FieldIndex.FileId];

        if (FileManager.instance().hasFile(fileID)) {
            try {
                FileManager.instance().deleteFile(fileID);
            } catch (IOException e) {}
        }
    }

    private void store(String[] headerFields) {
        String fileID = headerFields[FieldIndex.FileId];
        String chunkNumber = headerFields[FieldIndex.ChunkNo];

        if (FileManager.instance().hasFile(fileID)) {
            FileChunk file = FileManager.instance().getFile(fileID);
            int chunkNo = Integer.parseInt(chunkNumber);
            file.updateChunk(chunkNo);
        }
    }

    private void restore(String[] headerFields){

        try {
            Thread.sleep(GoodGuy.sleepTime(0,400));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("BOAS");

        String version=headerFields[FieldIndex.Version];
        String fileID=headerFields[FieldIndex.FileId];
        String chunkNo=headerFields[FieldIndex.ChunkNo];

        if(FileManager.instance().getFile(fileID).hasChunk(Integer.parseInt(chunkNo))){
            String header=Message.buildHeader(Protocol.MessageType.Chunk,version,server.getServerID(),fileID,chunkNo);

            try {
                byte[] body=FileManager.instance().getFile(fileID).getChunk(Integer.parseInt(chunkNo)).getContent(Message.MAX_CHUNK_SIZE - header.length() - 5);//new byte[Message.MAX_CHUNK_SIZE];
                Message msg = null;
                try {
                    msg = new Message(header, body);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                try{
                    server.getBackupChannel().send(msg); //precisei de criar o get para aceder ao mdr
                } catch(IOException e){
                    e.printStackTrace();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

}
