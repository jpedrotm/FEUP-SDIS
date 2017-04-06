package protocols;


import channels.ControlChannel;
import metadata.Metadata;
import utils.Message;

import java.io.IOException;

public class Delete {
    public static void DeleteFile(ControlChannel mc, String path, String version, String senderId) throws IOException {

        //Protocol.FileInfo fi = Protocol.generateFileInfo(path);
        //String hashFileId = Message.buildHash(fi.fileId);

        if (Metadata.instance().contains(Metadata.InfoRequest.FILENAME, path)) {
            String hashFileId = Metadata.instance().getHashFileId(path);
            Metadata.instance().deleteMetadata(path, hashFileId);
            String header = Message.buildHeader(Protocol.MessageType.Delete, version, senderId, hashFileId);
            Message msg = new Message(header);
            mc.send(msg);
        }
    }
}
