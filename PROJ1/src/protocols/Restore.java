package protocols;

import channels.BackupChannel;
import channels.ControlChannel;
import filesystem.FileManager;
import server.Metadata;
import utils.Message;

import java.io.IOException;

public class Restore extends Protocol{

    public static void receiveFileChunks(ControlChannel mc,String path,String version,String senderID) throws IOException{

        Protocol.FileInfo fi=Protocol.generateFileInfo(path);
        String hashFileId=Message.buildHash(fi.fileId);
        int numChunks = FileManager.instance().getFile(hashFileId).getNumChunks();

        int i=0;
        while(i < numChunks){
            String header=Message.buildHeader(MessageType.GetChunk,version,senderID,hashFileId,Integer.toString(i));
            Message msg = new Message(header);
            mc.send(msg);
            i++;
        }
    }

}