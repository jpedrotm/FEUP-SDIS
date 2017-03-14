package filesystem;


import java.util.HashMap;

public class FileManager {
    private HashMap<String,File> files;


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
    }



    public void addFile(String fileId, File file) {
        files.put(fileId, file);
    }

    public boolean hasFile(String fileId) {
        return files.containsKey(fileId);
    }

    public File getFile(String fileId) {
        return files.get(fileId);
    }
}
