package protocols;

import channels.ControlChannel;
import metadata.Metadata;
import utils.Message;

import java.io.IOException;

public class Restore extends Protocol{

    public static void receiveFileChunks(ControlChannel mc,String path,String version,String senderID) throws IOException{

        int numChunks = Metadata.instance().getFileNumChunks(path,Metadata.InfoRequest.FILEPATH);
        String hashFileId = Metadata.instance().getHashFileId(path);

        int i = 0;
        while(i < numChunks){
            String header=Message.buildHeader(MessageType.GetChunk,version,senderID,hashFileId,Integer.toString(i));
            Message msg = new Message(header);
            mc.send(msg);
            i++;
        }
    }

}