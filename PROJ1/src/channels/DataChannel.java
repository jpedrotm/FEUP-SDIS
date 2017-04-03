package channels;

import filesystem.Chunk;
import filesystem.FileManager;
import filesystem.FileChunk;
import metadata.Metadata;
import protocols.Protocol;
import protocols.Reclaim;
import server.Server;
import utils.FileChunkPair;
import utils.Message;
import utils.Message.FieldIndex;
import utils.PathHelper;

import java.io.IOException;
import java.net.DatagramPacket;
import java.nio.charset.StandardCharsets;
import java.util.Timer;
import java.util.TimerTask;

public class DataChannel extends Channel {

    public DataChannel(Server server, String addressStr, String portVar){
        super(server, addressStr,portVar);
    }

    @Override
    void handler() {

        DatagramPacket packet = new DatagramPacket(new byte[256], 256);

        try {
            socket.receive(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }

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
            byte[] body = message.getBody();

            if (headerFields[FieldIndex.SenderId].equals(server.getServerID()))
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
        String fileID = headerFields[FieldIndex.FileId];
        int chunkNo = Integer.parseInt(headerFields[FieldIndex.ChunkNo]);
        int replicationDegree = Integer.parseInt(headerFields[FieldIndex.ReplicationDeg]);
        String version=headerFields[FieldIndex.Version];

        server.updateRemovedTuple(fileID,chunkNo); //verificação que atualiza o removedTuple que indica em caso de um comando removed ser utilizado

        if (!FileManager.instance().canStore(body.length)) {
            FileChunkPair pair = FileManager.instance().getRemovableChunk(body.length);
            if (pair != null) {
                if (!FileManager.instance().deleteChunk(pair))
                {
                    Reclaim.sendRemoved(server.getControlChannel(),version,server.getServerID(),fileID,Integer.toString(chunkNo));
                    return;
                }
            }
            else
                return;
        }

        FileChunk file;
        if (FileManager.instance().hasFile(fileID))
            file = FileManager.instance().getFile(fileID);
        else {
            String dirPath = PathHelper.buildDir(server.getServerID(), fileID);
            file = new FileChunk(fileID, dirPath);
        }

        if (file.hasChunk(chunkNo)) {
            server.sendStored(fileID, chunkNo);
            return;
        }
        else {
            String path = PathHelper.buildPath(server.getServerID(), fileID, chunkNo);
            try {
                Chunk chunk = new Chunk(chunkNo, replicationDegree, body, path);
                file.addChunk(chunk);
                FileManager.instance().updateStoredSize(chunk);
                server.sendStored(fileID, chunkNo);
            } catch (IOException e) { /* Do nothing */ }
        }
    }


    @Override
    public void send(Message msg) throws IOException {
        super.send(msg);


        Limiter limiter = new Limiter(5);
        new Timer().schedule(new TimerTask() {
            String[] headerFields = msg.getHeaderFields();
            String fileId = headerFields[FieldIndex.FileId];
            String chunkNumber = headerFields[FieldIndex.ChunkNo];

            @Override
            public void run() {
                if (limiter.limitReached() || Metadata.instance().chunkDegreeSatisfied(fileId, chunkNumber)) {
                    this.cancel();
                    return;
                }

                try {
                    DataChannel.super.send(msg);
                } catch (IOException e) {
                    limiter.untick();
                }

                limiter.tick();
            }
        }, 1000, limiter.getCurrentTry() * 1000);
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
}
