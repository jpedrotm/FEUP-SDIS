package filesystem;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class FileChunk {
    private String id;
    private HashMap<Integer, Chunk> chunks;
    private String dirPath;

    public FileChunk(String id, String dirPath) {
        this.id = id;
        this.chunks = new HashMap<>();
        FileManager.instance().addFile(id, this);
        this.dirPath = dirPath;
    }

    public void addChunk(Chunk chunk) {
        chunks.put(chunk.getNumber(), chunk);
    }

    public Chunk getChunk(int chunkNo) {
        return chunks.get(chunkNo);
    }

    public boolean hasChunk(int chunkNo) {
        return chunks.containsKey(chunkNo);
    }

    public void updateChunk(int chunkNo, String serverId) {
        if (chunks.containsKey(chunkNo))
            chunks.get(chunkNo).addReplication(serverId);
    }

    public int getNumChunks(){
        return chunks.size();
    }

    public String getFileId(){
        return id;
    }

    @Override
    public String toString() {
        return chunks.toString();
    }

    public void delete() throws IOException {
        Iterator it = chunks.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            Chunk chunk = (Chunk) pair.getValue();
            chunk.deleteContent();
            it.remove(); // avoids a ConcurrentModificationException
        }


        for (Chunk chunk : chunks.values()) {
            chunk.deleteContent();
            chunks.remove(chunk.getNumber());
        }

        Path dir = Paths.get(dirPath);
        Files.delete(dir);
    }

    public ArrayList<Chunk> getChunksOverRep() {
        ArrayList<Chunk> chunkList = new ArrayList<>();

        Iterator it = chunks.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            Chunk chunk = (Chunk) pair.getValue();

            if (chunk.getActualReplicationDegree() > chunk.getReplicationDegree()) {
                chunkList.add(chunk);
            }
        }

        return chunkList;
    }

    public boolean deleteChunk(int chunkNo) {
        try {
            Chunk chunk = chunks.get(chunkNo);
            chunk.deleteContent();
            chunks.remove(chunkNo);
        } catch (Exception e) {
            return false;
        }

        return true;
    }

    public int getContentSize() {
        int size = 0;
        for (Chunk chunk : chunks.values()) {
            size += chunk.getContentSize();
        }

        return size;
    }
}
