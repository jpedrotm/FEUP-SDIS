package filesystem;

import channels.ControlChannel;
import protocols.Reclaim;
import utils.FileChunkListener;
import utils.FileChunkPair;

import java.io.IOException;
import java.io.Serializable;
import java.util.*;

public class FileManager implements Serializable {
    private HashMap<String, FileChunk> files;
    private HashSet<String> pendingLeases;
    private int maxContentSize = 64000 * 100;


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
        pendingLeases = new HashSet<>();
    }

    public void refresh(FileChunkListener fileChunkListener){
        for (FileChunk fc : files.values()){
            fc.refreshFileChunkListener(fileChunkListener);
        }
    }

    public void addPendingLease(String fileId) {
        pendingLeases.add(fileId);
    }

    public boolean hasPendingLease(String fileId) {
        return pendingLeases.contains(fileId);
    }

    public void updatePendingLease(String fileId, int maxTimestamp) {
        FileChunk f = files.get(fileId);
        f.loadLease(maxTimestamp);
        pendingLeases.remove(fileId);
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

    public void deleteFile(String fileID) throws IOException {
        FileChunk file = FileManager.instance().getFile(fileID);
        file.delete();
        files.remove(fileID);
    }

    public boolean canStore(int newContentSize) {
        return getStoredSize() + newContentSize <= maxContentSize;
    }

    public ArrayList<FileChunkPair> getChunksOrdered() {
        ArrayList<FileChunkPair> pairs = new ArrayList<>();

        Iterator it = files.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            FileChunk file = (FileChunk) pair.getValue();

            ArrayList<Chunk> chunks = file.getChunks();
            for (Chunk chunk : chunks) {
                pairs.add(new FileChunkPair(file, chunk));
            }
        }

        Collections.sort(pairs, (a, b) -> a.chunk.getActualReplicationDegree() - a.chunk.getReplicationDegree() < b.chunk.getActualReplicationDegree() - b.chunk.getReplicationDegree() ?
                                    1 : a.chunk.getActualReplicationDegree() - a.chunk.getReplicationDegree() == b.chunk.getActualReplicationDegree() - b.chunk.getReplicationDegree() ?
                                    0 : -1);
        return pairs;
    }


    public FileChunkPair getRemovableChunk(int newContentSize) {
        ArrayList<FileChunkPair> pairs = new ArrayList<>();

        Iterator it = files.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            FileChunk file = (FileChunk) pair.getValue();

            ArrayList<Chunk> chunks = file.getChunksOverRep();
            for (Chunk chunk : chunks) {
                if (getStoredSize() - chunk.getContentSize() + newContentSize <= maxContentSize) {
                    pairs.add(new FileChunkPair(file, chunk));
                }
            }
        }

        Collections.sort(pairs, (a, b) -> a.chunk.getActualReplicationDegree() - a.chunk.getReplicationDegree() < b.chunk.getActualReplicationDegree() - b.chunk.getReplicationDegree() ?
                1 : a.chunk.getActualReplicationDegree() - a.chunk.getReplicationDegree() == b.chunk.getActualReplicationDegree() - b.chunk.getReplicationDegree() ?
                0 : -1);

        if (pairs.isEmpty()) return null;
        else                 return pairs.get(0);
    }


    public void updateLimitContentSize(int newMaxContentSize, ControlChannel mc,String serverID, String version){

        if(newMaxContentSize>this.maxContentSize){
            this.maxContentSize=newMaxContentSize;
        }
        else{
            ArrayList<FileChunkPair> chunksPair=getChunksOrdered();

            int i=0;
            while(getStoredSize()>newMaxContentSize){
                String chunkNo=Integer.toString(chunksPair.get(i).chunk.getNumber());
                String fileId=chunksPair.get(i).file.getFileId();
                deleteChunk(chunksPair.get(i));
                Reclaim.sendRemoved(mc,version,serverID,fileId,chunkNo);
                i++;
            }
            this.maxContentSize=newMaxContentSize;
        }
    }

    public boolean deleteChunk(FileChunkPair pair) {
        if (pair.file.deleteChunk(pair.chunk.getNumber())) {
            return true;
        }

        return false;
    }

    public int getStoredSize() {
        int storedSize = 0;
        for (FileChunk file : files.values()) {
            storedSize += file.getContentSize();
        }

        return storedSize;
    }

    public boolean chunkDegreeSatisfied(String fileId, int chunkNumber) {
        FileChunk f = files.get(fileId);
        Chunk c = f.getChunk(chunkNumber);
        return c.getActualReplicationDegreeSync() >= c.getReplicationDegree();
    }

    public void startChunkTransaction(String fileId, int chunkNumber) {
        FileChunk file = this.files.get(fileId);
        Chunk chunk = file.getChunk(chunkNumber);
        chunk.startTransaction();
    }

    public void stopChunkTransaction(String fileId, int chunkNumber) {
        FileChunk file = this.files.get(fileId);
        Chunk chunk = file.getChunk(chunkNumber);
        chunk.stopTransaction();
    }

    @Override
    public String toString() {
        String result="";
        result="FileManager{" +"\n";
        if(files.size()==0){
            result+="No chunks stored."+"\n";
        }
        else{
            result+=files.toString() +"\n";

        }

        result+="Storage capacity=" + maxContentSize +"\n"+
                "Free space=" + (maxContentSize-getStoredSize()) +"\n"+
                "Space used= "+getStoredSize()+"\n"+
                '}';

        return result;
    }

    public static void load(FileManager fileManager) {
        instance = fileManager;
    }

    public HashMap<String,FileChunk> getFiles() {
        return files;
    }
}
