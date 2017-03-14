package filesystem;


import java.util.ArrayList;

public class File {
    private ArrayList<Chunk> chunks;

    public File() {
        chunks = new ArrayList<>();
    }

    public void addChunk(Chunk chunk) {
        chunks.add(chunk);
        chunks.sort(Chunk::compareTo);
    }

    public Chunk getChunk(int chunkNo) {
        return chunks.get(chunkNo);
    }

    public boolean hasChunk(int chunkNo) {
        try {
            chunks.get(chunkNo);
            return true;
        }
        catch (IndexOutOfBoundsException e) {
            return false;
        }
    }

    public void updateChunk(int chunkNo) {
        chunks.get(chunkNo).addReplication();
    }
}
