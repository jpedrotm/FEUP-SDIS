package channels;

import filesystem.Chunk;
import filesystem.FileChunk;
import filesystem.FileManager;
import metadata.FileMetadata;
import metadata.Metadata;
import protocols.Backup;
import protocols.Protocol;
import server.Server;
import utils.*;
import utils.Message.FieldIndex;

import java.io.IOException;
import java.net.DatagramPacket;
import java.util.Timer;
import java.util.TimerTask;

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
                    restore(headerFields, packet);
                    break;
                case "GETLEASE":
                    lease(headerFields,packet);
                    break;
                case "LEASE":
                    restartLease(headerFields);
                    break;
                default:
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
                if(chunk.isReplicationDegreeDown()){
                    server.newRemovedTuple(new Tuplo3(fileID,chunkNo));
                    GoodGuy.sleepRandomTime(0, 400);

                    if(!server.getRemovedTuple().receivedPutChunk()) {
                        FileManager.instance().startChunkTransaction(fileID, chunkNo);
                        server.resetRemovedTuple();

                        FileChunk fileChunk = FileManager.instance().getFile(fileID);
                        Chunk chunkToResend = fileChunk.getChunk(chunkNo);
                        Backup.sendPutchunkFromRemoved(server.getDataChannel(), server.getServerID(), fileChunk, chunkToResend);

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

    private void restore(String[] headerFields, DatagramPacket packet){

        GoodGuy.sleepRandomTime(0,400);

        String version=headerFields[FieldIndex.Version];
        String fileID=headerFields[FieldIndex.FileId];
        String chunkNo=headerFields[FieldIndex.ChunkNo];

        if(FileManager.instance().getFile(fileID).hasChunk(Integer.parseInt(chunkNo))){
            String header=Message.buildHeader(Protocol.MessageType.Chunk,version,server.getServerID(),fileID,chunkNo);

            try {
                byte[] body = FileManager.instance().getFile(fileID).getChunk(Integer.parseInt(chunkNo)).getContent(Message.MAX_CHUNK_SIZE - header.length() - 5);//new byte[Message.MAX_CHUNK_SIZE];
                if (body == null) {
                  return;
                }

                Message msg = new Message(header, body);
                server.getBackupChannel().sendChunk(msg, packet);
            } catch (IOException e) {
                System.err.println("Error: " + e.getMessage());
                return;
            }
        }

    }

    private void lease(String[] headerFields,DatagramPacket packet){
        String version=headerFields[FieldIndex.Version];
        String fileID=headerFields[FieldIndex.FileId];

        if(Metadata.instance().hasFile(fileID)){
            String header = Message.buildHeader(Protocol.MessageType.Lease, version, server.getServerID(), fileID, Long.toString(GoodGuy.randomBetween(10, 20)));
            try {
                Message msg = new Message(header);
                server.getControlChannel().sendLease(msg, packet);
            } catch (IOException e) {}
        }
    }

    private void sendLease(Message msg, DatagramPacket packet) {
        try {
            super.send(msg);
        } catch (IOException e) {}
    }

    public void sendGetLease(Message msg) {
        try {
            super.send(msg);
        } catch (IOException e) {}

        Limiter limiter = new Limiter(5);
        startTimer(msg, limiter);
    }

    private void startTimer(Message msg, Limiter limiter) {
        new Timer().schedule(new TimerTask() {
            String[] headerFields = msg.getHeaderFields();
            String fileId = headerFields[FieldIndex.FileId];

            @Override
            public void run() {
                if (limiter.limitReached()) {
                    if (FileManager.instance().hasPendingLease(fileId)) {
                        try {
                            FileManager.instance().deleteFile(fileId);
                        } catch (IOException e) {}
                    }

                    this.cancel();
                    return;
                }
                else if (!FileManager.instance().hasPendingLease(fileId)) {
                    this.cancel();
                    return;
                }

                try {
                    ControlChannel.super.send(msg);
                } catch (IOException e) {
                    limiter.untick();
                }

                limiter.tick();
                this.cancel();
                startTimer(msg,limiter);
            }
        }, limiter.getCurrentTry() * 1000);
    }

    private void restartLease(String[] headerFields){
        String fileId = headerFields[FieldIndex.FileId];
        String leaseTime = headerFields[FieldIndex.ChunkNo];

        if (FileManager.instance().hasFile(fileId) && FileManager.instance().hasPendingLease(fileId)) {
            FileManager.instance().updatePendingLease(fileId, Integer.parseInt(leaseTime));
        }
    }
}
