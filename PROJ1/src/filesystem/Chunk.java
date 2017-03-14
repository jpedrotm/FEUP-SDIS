package filesystem;


public class Chunk implements Comparable<Chunk> {
    private int number;
    private byte[] content;
    private int replicationDegree;
    private int actualReplicationDegree;

    public Chunk(int number, byte[] content, int replicationDegree) {
        this.number = number;
        this.content = content;
        this.replicationDegree = replicationDegree;
        this.actualReplicationDegree = 0;
    }

    public int getNumber() {
        return number;
    }

    public byte[] getContent() {
        return content;
    }

    public int getReplicationDegree() {
        return replicationDegree;
    }

    public void addReplication() { actualReplicationDegree++; }

    public void subReplication() { actualReplicationDegree--; }

    @Override
    public int compareTo(Chunk chunk) {
        if (this.number < chunk.number)
            return -1;

        if (this.number > chunk.number) {
            return 1;
        }

        return 0;
    }
}
