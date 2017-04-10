package protocols;

import channels.ControlChannel;
import utils.Message;

import java.io.IOException;

public class Reclaim extends Protocol{
    public static void sendRemoved(ControlChannel mc,String version,String senderID,String fileId,String chunkNo){
        String header= Message.buildHeader(MessageType.Removed,version,senderID,fileId,chunkNo);

        try {
            Message msg=new Message(header);
            mc.send(msg);
        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
            return;
        }
    }

}
