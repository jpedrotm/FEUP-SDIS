package channels;

import filesystem.Chunk;
import filesystem.FileChunk;
import filesystem.FileManager;
import metadata.FileMetadata;
import metadata.Metadata;
import protocols.Protocol;
import server.Server;
import utils.GoodGuy;
import utils.Message;
import utils.Message.FieldIndex;
import utils.Tuplo3;

import java.io.*;
import java.net.DatagramPacket;

public class ControlChannel extends Channel {

    public ControlChannel(Server server, String addressStr, String portVar){
        super(server, addressStr,portVar);
    }

    @Override
    public void run() {

        while (!shutdown) {

            DatagramPacket packet = new DatagramPacket(new byte[Message.MAX_CHUNK_SIZE], Message.MAX_CHUNK_SIZE);
            try {
                this.socket.receive(packet);
            } catch (IOException e) {
                System.err.println("Error: " + e.getMessage());
                continue;
            }

            Message message = new Message(packet);
            String[] headerFields = message.getHeaderFields();

            if (headerFields[FieldIndex.SenderId].equals(server.getServerID()) || !headerFields[FieldIndex.Version].equals(server.getVersion()))
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
        String senderID = headerFields[FieldIndex.SenderId];
        String fileID = headerFields[FieldIndex.FileId];
        int chunkNo = Integer.parseInt(headerFields[FieldIndex.ChunkNo]);
        String version = headerFields[FieldIndex.Version];

        if(FileManager.instance().hasFile(fileID)){
            if(FileManager.instance().getFile(fileID).hasChunk(chunkNo))
            {

                Chunk chunk=FileManager.instance().getFile(fileID).getChunk(chunkNo);
                chunk.subReplication(senderID);
                System.out.println("ChunkNo: "+chunkNo+" , actualRepDegree: "+chunk.getActualReplicationDegreeSync()+", RepDegree: "+chunk.getReplicationDegree());
                if(chunk.isReplicationDegreeDown()){
                    System.out.println("Verificou que está a baixo"); //falta testar esta parte agora
                    server.newRemovedTuple(new Tuplo3(fileID,chunkNo));
                    GoodGuy.sleepRandomTime(0,400);

                    if(!server.getRemovedTuple().receivedPutChunk()) {
                        System.out.println("Não recebeu PutChunk");
                        server.resetRemovedTuple(); //tenho de fazer logo reset caso contrário envia o putchunk e ignora-o também (não tenho a certeza que o reserRemovedTuple() que tem em baxo chega)
                        String header = Message.buildHeader(Protocol.MessageType.Putchunk, version, server.getServerID(), fileID, Integer.toString(chunkNo), Integer.toString(chunk.getReplicationDegree()));
                        try {
                            byte[] body = FileManager.instance().getFile(fileID).getChunk(chunkNo).getContent(Message.MAX_CHUNK_SIZE - header.length() - 5);
                            Message message = new Message(header, body);
                            server.getDataChannel().sendRemoved(message);
                        } catch (IOException e) {
                            System.err.println("Error: " + e.getMessage());
                            return;
                        }
                    }
                    else
                        server.resetRemovedTuple();
                }
            }
        }

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
        String senderID = headerFields[FieldIndex.SenderId];
        String fileID = headerFields[FieldIndex.FileId];
        String chunkNumber = headerFields[FieldIndex.ChunkNo];

        if (FileManager.instance().hasFile(fileID)) {
            FileChunk file = FileManager.instance().getFile(fileID);
            int chunkNo = Integer.parseInt(chunkNumber);
            file.updateChunk(chunkNo, senderID);
        }
        else if (Metadata.instance().hasFile(fileID)) {
            FileMetadata f = Metadata.instance().getFileMetadata(fileID);
            int chunkNo = Integer.parseInt(chunkNumber);
            f.updateChunk(chunkNo, senderID);
        }
    }

    private void restore(String[] headerFields){

        GoodGuy.sleepRandomTime(0,400);

        String version=headerFields[FieldIndex.Version];
        String fileID=headerFields[FieldIndex.FileId];
        String chunkNo=headerFields[FieldIndex.ChunkNo];

        if(FileManager.instance().getFile(fileID).hasChunk(Integer.parseInt(chunkNo))){
            String header=Message.buildHeader(Protocol.MessageType.Chunk,version,server.getServerID(),fileID,chunkNo);

            try {
                byte[] body=FileManager.instance().getFile(fileID).getChunk(Integer.parseInt(chunkNo)).getContent(Message.MAX_CHUNK_SIZE - header.length() - 5);//new byte[Message.MAX_CHUNK_SIZE];
                if (body == null)
                  return;

                Message msg = new Message(header, body);
                server.getBackupChannel().send(msg);
            } catch (IOException e) {
                System.err.println("Error: " + e.getMessage());
                return;
            }
        }

    }

}
