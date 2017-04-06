package filesystem;

import utils.FileChunkPair;

import java.io.IOException;
import java.util.*;

public class FileManager {
    private HashMap<String, FileChunk> files;
    private int maxContentSize = 64000 * 100;


    private static FileManager instance = null;

    public static FileManager instance() {

        if(instance == null){
            synchronized (FileManager.class) {
                if(instance == null){
                    instance = new FileManager();
                }
            }
        }

        return instance;
    }

    private FileManager() {
        files = new HashMap<>();
    }



    public void addFile(String fileId, FileChunk file) {
        files.put(fileId, file);
    }

    public boolean hasFile(String fileId) {
        return files.containsKey(fileId);
    }

    public FileChunk getFile(String fileId) {
        return files.get(fileId);
    }

    public void deleteFile(String fileID) throws IOException {
        FileChunk file = FileManager.instance().getFile(fileID);
        file.delete();
        files.remove(fileID);
    }

    public boolean canStore(int newContentSize) {
        return getStoredSize() + newContentSize <= maxContentSize;
    }

    public ArrayList<FileChunkPair> getChunksOrdered(int newContentSize) {
        ArrayList<FileChunkPair> pairs = new ArrayList<>();

        Iterator it = files.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            FileChunk file = (FileChunk) pair.getValue();

            ArrayList<Chunk> chunks = file.getChunksOverRep();
            for (Chunk chunk : chunks) {
                pairs.add(new FileChunkPair(file, chunk));
            }
        }

        Collections.sort(pairs, (a, b) -> a.chunk.getActualReplicationDegree() - a.chunk.getReplicationDegree() < b.chunk.getActualReplicationDegree() - b.chunk.getReplicationDegree() ?
                                    1 : a.chunk.getActualReplicationDegree() - a.chunk.getReplicationDegree() == b.chunk.getActualReplicationDegree() - b.chunk.getReplicationDegree() ?
                                    0 : -1);
        return pairs;
    }

    public boolean deleteChunk(FileChunkPair pair) {
        if (pair.file.deleteChunk(pair.chunk.getNumber())) {
            return true;
        }

        return false;
    }

    public int getStoredSize() {
        int storedSize = 0;
        for (FileChunk file : files.values()) {
            storedSize += file.getContentSize();
        }

        return storedSize;
    }

    public boolean chunkDegreeSatisfied(String fileId, int chunkNumber) {
        FileChunk f = files.get(fileId);
        Chunk c = f.getChunk(chunkNumber);

        return c.getActualReplicationDegreeSync() >= c.getReplicationDegree();
    }

    @Override
    public String toString() {
        return "FileManager{" +
                "files=" + files +
                ", storedContentSize=" + getStoredSize() +
                ", maxContentSize=" + maxContentSize +
                '}';
    }
}
