package protocols;


import utils.Message;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;

public abstract class Protocol {
    public class MessageType {
        public final static String Putchunk = "PUTCHUNK";
        public final static String Stored = "STORED";
        public final static String Delete = "DELETE";
        public final static String Chunk="CHUNK";
        public final static String GetChunk="GETCHUNK";
    }





    public static class FileInfo {
        public File file;
        public FileInputStream is;
        public Path path;
        public String filename;
        public String extension;
        public String owner;
        public String lastModified;
        public String fileId;

        public FileInfo(String filePath) throws IOException {
            // File info
            file = new File(filePath);
            is = new FileInputStream(file);
            filename = file.getName();
            extension = filename.substring(filename.lastIndexOf(".") + 1);

            // Path info
            path = Paths.get(filePath);
            BasicFileAttributes attr = Files.readAttributes(path, BasicFileAttributes.class);
            lastModified = String.valueOf(attr.lastModifiedTime());
            owner = String.valueOf(Files.getOwner(path));

            fileId = filename+owner+lastModified;
        }
    }

    public static FileInfo generateFileInfo(String filePath) throws IOException {
        return new FileInfo(filePath);
    }
}
