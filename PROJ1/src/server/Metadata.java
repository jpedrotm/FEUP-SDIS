package server;


import java.io.Serializable;
import java.util.HashMap;

public class Metadata implements Serializable {
    HashMap<String,String> hashMap;
    HashMap<String, FileMetadata> fileInfoHashMap;

    public Metadata() {
        fileInfoHashMap = new HashMap<>();
        hashMap = new HashMap<>();
    }

    public void addMetadata(String filename, String extension, String path, String hash,int numChunks) {
        fileInfoHashMap.put(filename, new FileMetadata(filename, extension, path, numChunks, hash));
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

    @Override
    public String toString() {
        return "Metadata{" +
                "hashSet=" + hashMap +
                ", map=" + fileInfoHashMap +
                '}';
    }

    public String getHashFileId(String path) {
        return fileInfoHashMap.get(path).getHashFileId();
    }


    /*** Helper classes ***/
    public enum InfoRequest {
        FILENAME, HASH
    }

    private class FileMetadata implements Serializable {
        private String filename;
        private String extension;
        private String path;
        private int numChunks;
        private String hashFileId;

        public FileMetadata(String filename, String extension, String path, int numChunks, String hashFileId) {
            this.filename = filename;
            this.extension = extension;
            this.path = path;
            this.numChunks=numChunks;
            this.hashFileId = hashFileId;
        }

        public String getFilename() {
            return filename;
        }

        public String getExtension() {
            return extension;
        }

        public int getNumChunks(){
            return numChunks;
        }

        @Override
        public String toString() {
            return "FileMetadata{" +
                    "filename='" + filename + '\'' +
                    ", extension='" + extension + '\'' +
                    '}';
        }

        public String getHashFileId() {
            return hashFileId;
        }
    }
}
