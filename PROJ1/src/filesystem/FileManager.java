package filesystem;


import utils.Message;

import java.util.HashMap;

public class FileManager {
    private HashMap<String, FileChunk> files;


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

    public boolean chunkDegreeSatisfied(String fileId, String chunkNumber) {
        int chunkNo = Integer.parseInt(chunkNumber);
        FileChunk fileChunk = getFile(fileId);
        Chunk chunk = fileChunk.getChunk(chunkNo);

        return (chunk.getActualReplicationDegree() >= chunk.getReplicationDegree());
    }

    @Override
    public String toString() {
        return "FileManager{" +
                "files=" + files +
                '}';
    }
}
