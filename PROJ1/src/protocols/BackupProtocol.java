package protocols;


import channels.ControlChannel;
import utils.Message;

import java.io.*;

public class BackupProtocol extends Protocol {

    public static boolean sendFileChunks(ControlChannel mc, String path) throws IOException {
        File file = new File(path);
        FileInputStream is = new FileInputStream(file);
        byte[] content = new byte[Message.MAX_CHUNK_SIZE];
        int chunkLength = 0;

        while ((chunkLength = is.read(content)) != -1) {
            System.out.println(new String(content));
        }

        return true;
    }
}
