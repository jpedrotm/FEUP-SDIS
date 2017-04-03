package protocols;


import channels.ControlChannel;
import metadata.Metadata;
import utils.Message;

import java.io.IOException;

public class Delete {
    public static void DeleteFile(ControlChannel mc, String path, String version, String senderId) throws IOException {
        int idx = path.replaceAll("\\\\", "/").lastIndexOf("/");
        String filename =  idx >= 0 ? path.substring(idx + 1) : path;

      //  Protocol.FileInfo fi = Protocol.generateFileInfo(path);
       // String hashFileId = Message.buildHash(fi.fileId);

        if (Metadata.instance().contains(Metadata.InfoRequest.FILENAME, filename)) {
            String hashFileId = Metadata.instance().getHashFileId(filename);
            Metadata.instance().deleteMetadata(filename, hashFileId);
            String header = Message.buildHeader(Protocol.MessageType.Delete, version, senderId, hashFileId);
            Message msg = new Message(header);
            mc.send(msg);
        }
    }
}
