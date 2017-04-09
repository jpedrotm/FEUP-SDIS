package channels;

import metadata.Metadata;
import protocols.Protocol;
import server.Server;
import utils.Message;

import java.io.*;
import java.net.DatagramPacket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

public class BackupChannel extends Channel {
    private HashMap<String, ChunkRestore> filesToRead;

    public BackupChannel(Server server, String addressStr, String portVar){
        super(server, addressStr,portVar);
        filesToRead = new HashMap<>();
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

            if (headerFields[Message.FieldIndex.SenderId].equals(server.getServerID()))
                continue;

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

        if (!Metadata.instance().hasFile(fileID))
            return;

        String fileName = Metadata.instance().getFileName(fileID);

        try {
            String path="storage/restored/"+fileName;
            Path pathToFile= Paths.get(path);

            if(chunkNo == 0 && !filesToRead.containsKey(fileID)) {
                Files.createDirectories(pathToFile.getParent());
                filesToRead.put(fileID, new ChunkRestore());
            }

            if(filesToRead.containsKey(fileID)){
                if(filesToRead.get(fileID).getCurrentChunkNo() == chunkNo) {
                    System.out.println("Escreve   "  + chunkNo + "    " + path);
                    FileOutputStream fos = new FileOutputStream (new File(path),true);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    baos.write(body);
                    baos.writeTo(fos);
                    filesToRead.get(fileID).verifyIfHasNextChunks(baos,fos);
                    baos.close();
                    fos.close();
                    this.updateFilesToRead(fileID, chunkNo);
                }
                else{
                    if(chunkNo>filesToRead.get(fileID).getCurrentChunkNo()){
                        filesToRead.get(fileID).addChunk(chunkNo,body);
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    private class ChunkRestore {

        private HashMap<Integer,byte[]> chunkBody;
        private int currentChunkNo;

        public ChunkRestore(){
            chunkBody=new HashMap<>();
            currentChunkNo=0;
        }

        public void addChunk(int chunkNo,byte[] body){
            chunkBody.put(chunkNo,body);
        }

        public void deleteChunk(int chunkNo){
            chunkBody.remove(chunkNo);
        }

        public void updateChunkNo(){
            this.currentChunkNo++;
        }

        public int getCurrentChunkNo(){
            return currentChunkNo;
        }

        public void verifyIfHasNextChunks(ByteArrayOutputStream baos,FileOutputStream fos){

            while (chunkBody.containsKey(currentChunkNo)) {
                System.out.println("VOU ESCREVER   "  + currentChunkNo);
                updateChunkNo();
                try {
                    baos.write(chunkBody.get(currentChunkNo));
                    baos.writeTo(fos);
                    deleteChunk(currentChunkNo);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    private void updateFilesToRead(String fileId, int chunkNo){

        System.out.println("RECEIVED CHUNK No: "+chunkNo);

        int numChunks=Metadata.instance().getFileNumChunks(fileId, Metadata.InfoRequest.HASH);
        if(chunkNo == (numChunks-1)){
            filesToRead.remove(fileId);
            System.out.println("Apaguei file: "+fileId+"->"+filesToRead.size());
        }
        else {
            filesToRead.get(fileId).updateChunkNo();
        }

    }

}
