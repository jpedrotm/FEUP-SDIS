package server;


import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;

public class Metadata implements Serializable {
    HashSet<String> hashSet;
    HashMap<String, FileInfo> map;

    public Metadata() {
        map = new HashMap<>();
        hashSet = new HashSet<>();
    }

    public void addMetadata(String filename, String extension, String hash) {
        map.put(filename, new FileInfo(filename, extension, hash));
        hashSet.add(hash);
    }

    public boolean contains(InfoRequest infoRequest, String key) {
        switch (infoRequest) {
            case FILENAME:
                return map.containsKey(key);
            case HASH:
                return hashSet.contains(key);
            default:
                return false;
        }
    }

    @Override
    public String toString() {
        return "Metadata{" +
                "hashSet=" + hashSet +
                ", map=" + map +
                '}';
    }



    /*** Helper classes ***/
    public enum InfoRequest {
        FILENAME, HASH
    }

    private class FileInfo implements Serializable {
        private String filename;
        private String extension;

        public FileInfo(String filename, String extension, String hash) {
            this.filename = filename;
            this.extension = extension;
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
