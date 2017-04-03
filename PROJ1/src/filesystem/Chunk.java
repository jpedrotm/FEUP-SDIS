package filesystem;


import utils.Message;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

public class Chunk {
    private int number;
    private int replicationDegree;
    private int actualReplicationDegree;
    private String path;
    private int contentSize;

    public Chunk(int number, int replicationDegree, byte[] content, String path) throws IOException {
        this.number = number;
        this.replicationDegree = replicationDegree;
        this.actualReplicationDegree = 0;
        this.path = path;
        contentSize = content.length;

        storeContent(content);
    }

    public int getNumber() {
        return number;
    }

    public byte[] getContent(int byteReadSize) throws IOException {

        File chunkFile=new File(path);
        byte[] content=new byte[Message.MAX_CHUNK_SIZE];
        int bytesRead;

        FileInputStream is=new FileInputStream(chunkFile);
        bytesRead=is.read(content, 0, byteReadSize);

        byte[] body = Arrays.copyOf(content, bytesRead);
        return body;
    }

    public int getReplicationDegree() {
        return replicationDegree;
    }

    public int getActualReplicationDegree() {
        return actualReplicationDegree;
    }

    public String getPath() {
        return path;
    }

    public int getContentSize() {
        return contentSize;
    }

    public synchronized void addReplication() { actualReplicationDegree++; }

    public synchronized void subReplication() { actualReplicationDegree--; }

    public synchronized void resetReplication() { actualReplicationDegree = 1; }

    public boolean isReplicationDegreeDown(){
        return replicationDegree<actualReplicationDegree;
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
        return "Chunk{" +
                "number=" + number +
                ", replicationDegree=" + replicationDegree +
                ", actualReplicationDegree=" + actualReplicationDegree +
                ", path='" + path + '\'' +
                ", contentSize=" + contentSize +
                '}';
    }
}
