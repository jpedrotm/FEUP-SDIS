package filesystem;


import utils.Message;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashSet;

public class Chunk  implements Serializable {
    private int number;
    private int replicationDegree;
    private HashSet<String> storeds;
    private String path;
    private int contentSize;
    private boolean onTransaction;

    public Chunk(int number, int replicationDegree, byte[] content, String path) throws IOException {
        this.number = number;
        this.replicationDegree = replicationDegree;
        this.path = path;
        this.contentSize = content.length;
        this.storeds = new HashSet<>();
        onTransaction = false;

        storeContent(content);
        System.err.println("content stored!!");
    }

    public boolean isOnTransaction() {
        return onTransaction;
    }

    public void startTransaction() {
        onTransaction = true;
    }

    public void stopTransaction() {
        onTransaction = false;
    }

    public int getNumber() {
        return number;
    }

    public byte[] getContent(int byteReadSize) throws IOException {

        File chunkFile=new File(path);
        byte[] content=new byte[Message.MAX_CHUNK_SIZE];
        int bytesRead;

        FileInputStream is=new FileInputStream(chunkFile);
        bytesRead = is.read(content, 0, byteReadSize);
        if (bytesRead < 0)
          return null;

        byte[] body = Arrays.copyOf(content, bytesRead);
        return body;
    }

    public synchronized int getReplicationDegree() {
        return replicationDegree;
    }

    public String getPath() {
        return path;
    }

    public int getContentSize() {
        return contentSize;
    }

    public int getActualReplicationDegree() {
        return storeds.size();
    }

    public synchronized int getActualReplicationDegreeSync() { return  storeds.size(); }

    public synchronized void addReplication(String serverId) {
        if (!storeds.contains(serverId)) {
            storeds.add(serverId);
        }
    }

    public synchronized void subReplication(String serverId) { storeds.remove(serverId); }

    public synchronized boolean isReplicationDegreeDown(){
        return replicationDegree > storeds.size();
    }

    private void storeContent(byte[] content) throws IOException {
        Path pathToFile = Paths.get(path);
        Files.createDirectories(pathToFile.getParent());
        Files.write(pathToFile, content);
    };

    public void deleteContent() throws IOException {
        Path pathToFile = Paths.get(path);
        Files.delete(pathToFile);
    }

    @Override
    public String toString() {
        return "Chunk{ ontransaction=" + onTransaction +
                "id: "+number+" , "+
                "size: "+contentSize+" , "+
                "perceived repDegree: "+storeds.size()+
                '}';
    }
}
