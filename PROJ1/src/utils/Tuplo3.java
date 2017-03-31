package utils;

/**
 * Created by jpedrotm on 31-03-2017.
 */
public class Tuplo3 {
    private String fileId;
    private int chunkNo;
    private boolean gotPutChunk;

    public Tuplo3(String fileID,int chunkNo){
        this.fileId=fileID;
        this.chunkNo=chunkNo;
        this.gotPutChunk=false;
    }

    public void verifyEquality(String tmpFileId, int tmpChunkNo){
        gotPutChunk=(fileId.equals(tmpFileId) && chunkNo==tmpChunkNo);
    }

    public boolean receivedPutChunk(){
        return gotPutChunk;
    }
}
