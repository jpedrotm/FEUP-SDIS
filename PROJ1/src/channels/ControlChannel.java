package channels;

import filesystem.FileChunk;
import filesystem.FileManager;
import protocols.Protocol;
import server.Metadata;
import server.Server;
import utils.Message;
import utils.Message.FieldIndex;
import utils.PathHelper;

import java.beans.FeatureDescriptor;
import java.io.*;
import java.net.DatagramPacket;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

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

            /*if (headerFields[FieldIndex.SenderId].equals(server.getServerID())) {
                return;
            }*/


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

    private void restore(String[] headerFiles){
        String version=headerFiles[FieldIndex.Version];
        String fileID=headerFiles[FieldIndex.FileId];
        String senderID=headerFiles[FieldIndex.SenderId];
        String chunkNo=headerFiles[FieldIndex.ChunkNo];

        if(server.getMetadata().contains(Metadata.InfoRequest.HASH,fileID) && FileManager.instance().getFile(fileID).hasChunk(Integer.parseInt(chunkNo))){
            String header=Message.buildHeader(Protocol.MessageType.Chunk,version,senderID,fileID,chunkNo);
            String chunkPath= PathHelper.buildPath(senderID,fileID,Integer.parseInt(chunkNo));
            File chunkFile=new File(chunkPath);
            byte[] content=new byte[Message.MAX_CHUNK_SIZE];
            int bytesRead=-1;

            try {
                FileInputStream is=new FileInputStream(chunkFile);
                 bytesRead= is.read(content, 0, Message.MAX_CHUNK_SIZE - header.length() - 5);

            } catch (IOException e) {
                e.printStackTrace();
            }

            byte[] body = Arrays.copyOf(content, bytesRead);

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
        }

    }

}
