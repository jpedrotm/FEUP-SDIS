package protocols;

import channels.ControlChannel;
import utils.Message;

import java.io.IOException;

/**
 * Created by jpedrotm on 31-03-2017.
 */
public class Reclaim extends Protocol{
    public static void sendRemoved(ControlChannel mc,String version,String senderID,String fileId,String chunkNo){
        String header= Message.buildHeader(MessageType.Removed,version,senderID,fileId,chunkNo);

        Message msg=null;
        try {
            msg=new Message(header);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            mc.send(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
