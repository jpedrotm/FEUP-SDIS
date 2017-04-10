package channels;

import filesystem.Chunk;
import filesystem.FileChunk;
import filesystem.FileManager;
import metadata.Metadata;
import protocols.Protocol;
import protocols.Reclaim;
import server.Server;
import utils.FileChunkPair;
import utils.GoodGuy;
import utils.Message;
import utils.Message.FieldIndex;
import utils.PathHelper;

import java.io.IOException;
import java.net.DatagramPacket;
import java.util.Timer;
import java.util.TimerTask;

public class DataChannel extends Channel {

    public DataChannel(Server server, String addressStr, String portVar){
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
            byte[] body = message.getBody();

            if (headerFields[FieldIndex.SenderId].equals(server.getServerID()) || !headerFields[FieldIndex.Version].equals(server.getVersion()))
                continue;

            System.out.println(message.getHeader());

            switch (headerFields[FieldIndex.MessageType]) {
                case Protocol.MessageType.Putchunk:
                    putChunk(headerFields, body);
                    break;
            }
        }
    }


    private void putChunk(String[] headerFields, byte[] body) {
        String senderID = headerFields[FieldIndex.SenderId];
        String fileID = headerFields[FieldIndex.FileId];
        int chunkNo = Integer.parseInt(headerFields[FieldIndex.ChunkNo]);
        int replicationDegree = Integer.parseInt(headerFields[FieldIndex.ReplicationDeg]);
        String version=headerFields[FieldIndex.Version];

        server.updateRemovedTuple(fileID,chunkNo);

        if (!FileManager.instance().canStore(body.length) || Metadata.instance().hasFile(fileID))
            return;


        if (!FileManager.instance().canStore(body.length)) {
            FileChunkPair pair = FileManager.instance().getRemovableChunk(body.length);
            if (pair != null) {
                if (FileManager.instance().deleteChunk(pair))
                    Reclaim.sendRemoved(server.getControlChannel(), version, server.getServerID(), fileID, Integer.toString(chunkNo));
                else return;
            }
            else return;
        }


        FileChunk file;
        if (FileManager.instance().hasFile(fileID))
            file = FileManager.instance().getFile(fileID);
        else {
            String dirPath = PathHelper.buildDir(server.getServerID(), fileID);
            file = new FileChunk(fileID, dirPath);
        }

        if (file.hasChunk(chunkNo)) {
            server.sendStored(fileID, chunkNo,version);
            return;
        }
        else {
            String path = PathHelper.buildPath(server.getServerID(), fileID, chunkNo);
            try {
                Chunk chunk = new Chunk(chunkNo, replicationDegree, body, path);
                file.addChunk(chunk);

                GoodGuy.sleepRandomTime(0,800);
                if (FileManager.instance().chunkDegreeSatisfied(fileID, chunkNo)) {
                    file.deleteChunk(chunk.getNumber());
                }
                else {
                    server.sendStored(fileID, chunkNo, version);
                    chunk.addReplication(server.getServerID());
                }
            }
            catch (Exception e) { /* Do nothing */ }
        }
    }


    @Override
    public void send(Message msg) throws IOException {
        super.send(msg);

        Limiter limiter = new Limiter(5);
        startTimer(msg, limiter,StartTimerType.NORMAL);
    }

    public void sendRemoved(Message msg) throws IOException{
        super.send(msg);

        Limiter limiter = new Limiter(5);
        startTimer(msg, limiter,StartTimerType.REMOVED);
    }



    private void startTimer(Message msg, Limiter limiter,StartTimerType type) {
        new Timer().schedule(new TimerTask() {
            String[] headerFields = msg.getHeaderFields();
            String fileId = headerFields[FieldIndex.FileId];
            String chunkNumber = headerFields[FieldIndex.ChunkNo];

            @Override
            public void run() {
                if(type==StartTimerType.NORMAL){
                    if (limiter.limitReached() || Metadata.instance().chunkDegreeSatisfied(fileId, chunkNumber)) {
                        this.cancel();
                        return;
                    }
                }
                else if(type==StartTimerType.REMOVED){
                    if (limiter.limitReached() || FileManager.instance().chunkDegreeSatisfied(fileId,Integer.parseInt(chunkNumber))) {
                        this.cancel();
                        return;
                    }
                }

                try {
                    DataChannel.super.send(msg);
                } catch (IOException e) {
                    limiter.untick();
                }

                limiter.tick();
                this.cancel();
                startTimer(msg, limiter,type);
            }
        }, limiter.getCurrentTry() * 1000);
    }


    private class Limiter {
        private int maxTries;
        private int currentTry;

        public Limiter(int maxTries) {
            this.maxTries = maxTries;
            this.currentTry = 1;
        }

        public void tick() {
            currentTry++;
        }

        public void untick() {
            currentTry--;
        }

        public boolean limitReached() {
            return currentTry >= maxTries;
        }

        public int getCurrentTry() {
            return currentTry;
        }
    }

    private enum StartTimerType{
        NORMAL,REMOVED;
    }
}
