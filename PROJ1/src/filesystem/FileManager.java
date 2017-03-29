package filesystem;


import utils.Message;

import java.io.IOException;
import java.util.HashMap;

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

    @Override
    public String toString() {
        return "FileManager{" +
                "files=" + files +
                ", storedContentSize=" + storedContentSize +
                ", maxContentSize=" + maxContentSize +
                '}';
    }
}
