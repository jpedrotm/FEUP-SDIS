package metadata;

import java.io.Serializable;
import java.util.HashMap;

public class FileMetadata implements Serializable {
    private String filename;
    private String extension;
    private String path;
    private String hashFileId;
    private HashMap<Integer, ChunkMetadata> chunksMetadata;

    public FileMetadata(String filename, String extension, String path, String hashFileId) {
        this.filename = filename;
        this.extension = extension;
        this.path = path;
        this.hashFileId = hashFileId;
        chunksMetadata = new HashMap<>();
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

    @Override
    public String toString() {
        return "FileMetadata{" +
                "filename='" + filename + '\'' +
                ", extension='" + extension + '\'' +
                ", path='" + path + '\'' +
                ", hashFileId='" + hashFileId + '\'' +
                ", chunksMetadata=" + chunksMetadata +
                '}';
    }

    public String getHashFileId() {
        return hashFileId;
    }

    public void addChunk(ChunkMetadata chunkMetadata) {
        chunksMetadata.put(chunkMetadata.getChunkNo(), chunkMetadata);
    }

    public ChunkMetadata getChunk(String chunkNo) {
        return chunksMetadata.get(Integer.parseInt(chunkNo));
    }

    public void updateChunk(int chunkNo) {
        chunksMetadata.get(chunkNo).addReplication();
    }
}
