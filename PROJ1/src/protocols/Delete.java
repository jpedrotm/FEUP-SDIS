package protocols;


import channels.ControlChannel;
import server.Metadata;
import utils.Message;

import java.io.IOException;

public class Delete {
    public static void DeleteFile(Metadata metadata, ControlChannel mc, String path, String version, String senderId) throws IOException {
        Protocol.FileInfo fi = Protocol.generateFileInfo(path);
        String hashFileId = Message.buildHash(fi.fileId);

        if (metadata.contains(Metadata.InfoRequest.HASH, hashFileId)) {
            metadata.deleteMetadata(fi.filename, hashFileId);
            String header = Message.buildHeader(Protocol.MessageType.Delete, version, senderId, hashFileId);
            Message msg = new Message(header);
            mc.send(msg);
        }
    }
}
