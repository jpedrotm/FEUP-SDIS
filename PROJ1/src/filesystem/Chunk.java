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

    public byte[] getContent() {
        return null;
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

    public void addReplication() { actualReplicationDegree++; }

    public void subReplication() { actualReplicationDegree--; }

    public void resetReplication() { actualReplicationDegree = 0; }

    private void storeContent(byte[] content) throws IOException {
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
                ", actualReplicationDegree=" + actualReplicationDegree +
                ", path='" + path + '\'' +
                ", contentSize=" + contentSize +
                '}';
    }
}
