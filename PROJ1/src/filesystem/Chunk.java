package filesystem;


import utils.PathHelper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Chunk {
    private int number;
    private int replicationDegree;
    private byte[] content;
    private int actualReplicationDegree;

    public Chunk(int number, int replicationDegree, byte[] content) {
        this.number = number;
        this.replicationDegree = replicationDegree;
        this.content = content;
        this.actualReplicationDegree = 0;
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

    public void storeContent(String fileId) throws IOException {
        Path pathToFile = Paths.get(PathHelper.buildPath(fileId, number));
        Files.createDirectories(pathToFile.getParent());
        Files.write(pathToFile, content);
        content = null;     // free space when content is saved
    };
}