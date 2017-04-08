package metadata;

import java.io.Serializable;
import java.util.HashMap;

public class FileMetadata implements Serializable {
    private String filename;
    private String extension;
    private String path;
    private String hashFileId;
    private int repDegree;
    private HashMap<Integer, ChunkMetadata> chunksMetadata;

    public FileMetadata(String filename, String extension, String path, String hashFileId,int repDegree) {
        this.filename = filename;
        this.extension = extension;
        this.path = path;
        this.hashFileId = hashFileId;
        this.repDegree=repDegree;
        chunksMetadata = new HashMap<>();
    }


    public boolean isOnTransaction() {
        for (ChunkMetadata chunkMetadata : chunksMetadata.values()) {
            if (chunkMetadata.isOnTransaction()) {
                return true;
            }
        }
        return false;
    }

    public String getFilename() {
        return filename;
    }

    public String getExtension() {
        return extension;
    }

    public int getNumChunks() {
        return chunksMetadata.size();
    }

    public int getRepDegree(){
        return repDegree;
    }

    public String getPath() {
        return path;
    }

    public HashMap<Integer, ChunkMetadata> getChunksMetadata() {
        return chunksMetadata;
    }

    public String getHashFileId() {
        return hashFileId;
    }

    public ChunkMetadata getChunk(String chunkNo) {
        return chunksMetadata.get(Integer.parseInt(chunkNo));
    }

    @Override
    public String toString() {
        return "FileMetadata{" +
                "path='" + path + '\'' +
                ", hashFileId='" + hashFileId + '\'' +
                ", replicationDegree=" + repDegree + "\'\n"+
                chunksMetadata.toString()+
                "}\n";
    }

    public void addChunk(ChunkMetadata chunkMetadata) {
        chunksMetadata.put(chunkMetadata.getChunkNo(), chunkMetadata);
    }

    public void updateChunk(int chunkNo, String serverID) {
        chunksMetadata.get(chunkNo).addReplication(serverID);
    }
}
