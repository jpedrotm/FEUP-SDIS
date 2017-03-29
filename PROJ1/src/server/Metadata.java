package server;


import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;

public class Metadata implements Serializable {
    HashMap<String,String> hashMap;
    HashMap<String, FileInfo> fileInfoHashMap;

    public Metadata() {
        fileInfoHashMap = new HashMap<>();
        hashMap = new HashMap<>();
    }

    public void addMetadata(String filename, String extension, String path, String hash) {
        fileInfoHashMap.put(filename, new FileInfo(filename, extension, path));
        hashMap.put(hash,filename);
    }

    public void deleteMetadata(String filename, String hash) {
        fileInfoHashMap.remove(filename);
        hashMap.remove(hash);
    }

    public String getFileName(String key){
        return hashMap.get(key);
    }

    public boolean contains(InfoRequest infoRequest, String key) {
        switch (infoRequest) {
            case FILENAME:
                return fileInfoHashMap.containsKey(key);
            case HASH:
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



    /*** Helper classes ***/
    public enum InfoRequest {
        FILENAME, HASH
    }

    private class FileInfo implements Serializable {
        private String filename;
        private String extension;
        private String path;

        public FileInfo(String filename, String extension, String path) {
            this.filename = filename;
            this.extension = extension;
            this.path = path;
        }

        public String getFilename() {
            return filename;
        }

        public String getExtension() {
            return extension;
        }

        @Override
        public String toString() {
            return "FileInfo{" +
                    "filename='" + filename + '\'' +
                    ", extension='" + extension + '\'' +
                    '}';
        }
    }
}
