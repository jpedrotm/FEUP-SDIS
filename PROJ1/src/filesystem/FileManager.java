package filesystem;


import javafx.util.Pair;
import utils.FileChunkPair;
import utils.Message;

import java.io.IOException;
import java.util.*;

public class FileManager {
    private HashMap<String, FileChunk> files;
    private int storedContentSize;
    private int maxContentSize = 64000 * 10;


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
        storedContentSize = 0;
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

    public boolean chunkDegreeSatisfied(String fileId, String chunkNumber) {
        int chunkNo = Integer.parseInt(chunkNumber);
        FileChunk fileChunk = getFile(fileId);
        if (fileChunk == null)
            return false;

        Chunk chunk = fileChunk.getChunk(chunkNo);
        if (chunk == null)
            return false;

        return (chunk.getActualReplicationDegree() >= chunk.getReplicationDegree());
    }

    public void deleteFile(String fileID) throws IOException {
        FileChunk file = FileManager.instance().getFile(fileID);
        file.delete();
        files.remove(fileID);
    }

    public void updateStoredSize(Chunk chunk) {
        storedContentSize += chunk.getContentSize();
    }

    public boolean canStore(int newContentSize) {
        return storedContentSize + newContentSize <= maxContentSize;
    }

    public FileChunkPair getRemovableChunk() {
        ArrayList<FileChunkPair> pairs = new ArrayList<>();

        Iterator it = files.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            FileChunk file = (FileChunk) pair.getValue();

            ArrayList<Chunk> chunks = file.getChunksOverRep();
            Collections.sort(chunks, (a, b) -> a.getActualReplicationDegree() < b.getActualReplicationDegree() ? 1 : a.getActualReplicationDegree() == b.getActualReplicationDegree() ? 0 : -1);

            if (chunks.size() > 0) {
                pairs.add(new FileChunkPair(file, chunks.get(0)));
            }
        }

        Collections.sort(pairs, (a, b) -> a.chunk.getActualReplicationDegree() < b.chunk.getActualReplicationDegree() ? 1 : a.chunk.getActualReplicationDegree() == b.chunk.getActualReplicationDegree() ? 0 : -1);
        if (pairs.size() > 0)
            return pairs.get(0);
        else
            return null;
    }

    @Override
    public String toString() {
        return "FileManager{" +
                "files=" + files +
                ", storedContentSize=" + storedContentSize +
                ", maxContentSize=" + maxContentSize +
                '}';
    }
}
