package utils;


import java.nio.file.FileSystemNotFoundException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class PathHelper {
    final static String basePath = "storage/";

    /**
     *
     * @param fileId
     * @param chunkNo
     * @return Path used for storage
     */
    public static String buildPath(String serverId, String fileId, int chunkNo) {
        return basePath + serverId + "/" + fileId + "/" + chunkNo + ".txt";
    }

    /**
     *
     * @param path
     * @return Name and extension of the file in the path
     */
    public static String getFilename(String path) {
        try {
            Path p = Paths.get(path);
            return p.getFileName().toString();
        }
        catch (FileSystemNotFoundException e) {
            return null;
        }
    }
}
