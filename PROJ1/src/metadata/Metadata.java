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
        fileInfoHashMap.put(filename, new FileMetadata(filename, extension, path, hash));
        hashMap.put(hash,filename);
    }

    public void deleteMetadata(String filename, String hash) {
        fileInfoHashMap.remove(filename);
        hashMap.remove(hash);
    }

    public String getFileName(String key){
        return hashMap.get(key);
    }

    public int getFileNumChunks(String fileID){
        return fileInfoHashMap.get(fileID).getNumChunks();
    }

    public boolean contains(InfoRequest infoRequest, String key) {
        switch (infoRequest) {
            case FILENAME:
                return fileInfoHashMap.containsKey(key);
            case HASH:
                System.out.println("KEY: "+hashMap.get(key));
                return hashMap.containsKey(key);
            default:
                return false;
        }
    }

    public String getHashFileId(String path) {
        return fileInfoHashMap.get(path).getHashFileId();
    }

    public void addChunkMetadata(String filename, int chunkNo, int repDegree) {
        FileMetadata f = fileInfoHashMap.get(filename);
        ChunkMetadata c = new ChunkMetadata(chunkNo, repDegree);
        f.addChunk(c);
    }

    @Override
    public String toString() {
        return "Metadata{" +
                "hashSet=" + hashMap +
                ", map=" + fileInfoHashMap +
                '}';
    }

    public boolean chunkDegreeSatisfied(String fileId, String chunkNumber) {
        String filename = hashMap.get(fileId);
        FileMetadata f = fileInfoHashMap.get(filename);
        ChunkMetadata c = f.getChunk(chunkNumber);

        return c.getActualRepDegree() >= c.getRepDegree();
    }

    public boolean hasFile(String fileID) {
        return hashMap.containsKey(fileID);
    }

    public FileMetadata getFileMetadata(String fileID) {
        String filename = hashMap.get(fileID);
        return fileInfoHashMap.get(filename);
    }

    /*** Helper classes ***/
    public enum InfoRequest {
        FILENAME, HASH
    }
}
