package metadata;


import java.io.Serializable;
import java.util.HashMap;

public class Metadata implements Serializable {
    HashMap<String,String> hashMap;
    HashMap<String, FileMetadata> fileInfoHashMap;


    private static Metadata instance = null;

    public static Metadata instance() {

        if(instance == null){
            synchronized (Metadata.class) {
                if(instance == null){
                    instance = new Metadata();
                }
            }
        }

        return instance;
    }

    private Metadata() {
        fileInfoHashMap = new HashMap<>();
        hashMap = new HashMap<>();
    }

    public void addMetadata(String filename, String extension, String path, String hash) {
        fileInfoHashMap.put(path, new FileMetadata(filename, extension, path, hash));
        hashMap.put(hash,path);
    }

    public void deleteMetadata(String path, String hash) {
        fileInfoHashMap.remove(path);
        hashMap.remove(hash);
    }

    public String getFileName(String key){

        String fileName=fileInfoHashMap.get(hashMap.get(key)).getFilename();
        return fileName;
    }

    public int getFileNumChunks(String path){
        return fileInfoHashMap.get(path).getNumChunks();
    }

    public boolean contains(InfoRequest infoRequest, String key) {
        switch (infoRequest) {
            case FILENAME:
                return fileInfoHashMap.containsKey(key);
            case HASH:
                return hashMap.containsKey(key);
            default:
                return false;
        }
    }

    public String getHashFileId(String path) {
        return fileInfoHashMap.get(path).getHashFileId();
    }

    public void addChunkMetadata(String path, int chunkNo, int repDegree) {
        FileMetadata f = fileInfoHashMap.get(path);
        ChunkMetadata c = new ChunkMetadata(chunkNo, repDegree);
        f.addChunk(c);
    }

    public boolean chunkDegreeSatisfied(String fileId, String chunkNumber) {
        String path = hashMap.get(fileId);
        FileMetadata f = fileInfoHashMap.get(path);
        ChunkMetadata c = f.getChunk(chunkNumber);

        return c.getActualRepDegree() >= c.getRepDegree();
    }

    public boolean hasFile(String fileID) {
        return hashMap.containsKey(fileID);
    }

    public FileMetadata getFileMetadata(String fileID) {
        String path = hashMap.get(fileID);
        return fileInfoHashMap.get(path);
    }

    @Override
    public String toString() {
        return "Metadata{" +
                "hashSet=" + hashMap +
                ", map=" + fileInfoHashMap +
                '}';
    }

    /*** Helper classes ***/
    public enum InfoRequest {
        FILENAME, HASH
    }
}
