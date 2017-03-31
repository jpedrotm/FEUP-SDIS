package protocols;

import channels.ControlChannel;
import server.Metadata;
import utils.Message;

import java.io.IOException;

public class Restore extends Protocol{

    public static void receiveFileChunks(Metadata metadata,ControlChannel mc,String path,String version,String senderID) throws IOException{
        int idx = path.replaceAll("\\\\", "/").lastIndexOf("/");
        String filename =  idx >= 0 ? path.substring(idx + 1) : path;

        int numChunks = metadata.getFileNumChunks(filename);
        String hashFileId = metadata.getHashFileId(filename);

        System.out.print("VOUUUUUUU");

        int i=0;
        while(i < numChunks){
            String header=Message.buildHeader(MessageType.GetChunk,version,senderID,hashFileId,Integer.toString(i));
            Message msg = new Message(header);
            mc.send(msg);
            i++;
        }
    }

}