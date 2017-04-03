package metadata;


public class ChunkMetadata {
    private int chunkNo;
    private int repDegree;
    private int actualRepDegree;

    public ChunkMetadata(int chunkNo, int repDegree) {
        this.chunkNo = chunkNo;
        this.repDegree = repDegree;
        this.actualRepDegree = 0;
    }

    public synchronized void addReplication() {
        actualRepDegree++;
    }

    public synchronized void resetReplication() {
        actualRepDegree = 0;
    }

    public int getChunkNo() {
        return chunkNo;
    }

    public int getRepDegree() {
        return repDegree;
    }

    public int getActualRepDegree() {
        return actualRepDegree;
    }

    @Override
    public String toString() {
        return "ChunkMetadata{" +
                "chunkNo=" + chunkNo +
                ", repDegree=" + repDegree +
                ", actualRepDegree=" + actualRepDegree +
                '}';
    }
}
