package metadata;


import java.util.HashSet;

public class ChunkMetadata {
    private int chunkNo;
    private int repDegree;
    private HashSet<String> storeds;

    public ChunkMetadata(int chunkNo, int repDegree) {
        this.chunkNo = chunkNo;
        this.repDegree = repDegree;
        this.storeds = new HashSet<>();
    }

    public synchronized void addReplication(String serverID) {
        if (!storeds.contains(serverID))
            storeds.add(serverID);
    }

    public int getChunkNo() {
        return chunkNo;
    }

    public int getRepDegree() {
        return repDegree;
    }

    public synchronized int getActualRepDegree() {
        return storeds.size();
    }

    @Override
    public String toString() {
        return "ChunkMetadata{" +
                "chunkNo=" + chunkNo +
                ", repDegree=" + repDegree +
                ", actualRepDegree=" + storeds.size() +
                ", storeds=" + storeds +
                '}';
    }
}
