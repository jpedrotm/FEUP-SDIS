package filesystem;


import utils.PathHelper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

public class Chunk {
    private int number;
    private int replicationDegree;
    private byte[] content;
    private int actualReplicationDegree;
    private String path;

    public Chunk(int number, int replicationDegree, byte[] content, String path) {
        this.number = number;
        this.replicationDegree = replicationDegree;
        this.content = content;
        this.actualReplicationDegree = 0;
        this.path = path;
    }

    public int getNumber() {
        return number;
    }

    public byte[] getContent() {
        return content;
    }

    public int getReplicationDegree() {
        return replicationDegree;
    }

    public int getActualReplicationDegree() {
        return actualReplicationDegree;
    }

    public void addReplication() { actualReplicationDegree++; }

    public void subReplication() { actualReplicationDegree--; }

    public void resetReplication() { actualReplicationDegree = 0; }

    public void storeContent() throws IOException {
        Path pathToFile = Paths.get(path);
        Files.createDirectories(pathToFile.getParent());
        Files.write(pathToFile, content);
        content = null;     // free space when content is saved
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
                ", content=" + Arrays.toString(content) +
                ", actualReplicationDegree=" + actualReplicationDegree +
                '}';
    }
}
