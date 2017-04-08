package protocols;

import channels.ControlChannel;
import utils.Message;

import java.io.IOException;
import java.net.DatagramPacket;

public class LeaseProto {
    public static void sendGetLease(ControlChannel mc, String version, String senderID, String fileId){
        String header= Message.buildHeader(Protocol.MessageType.GetLease,version,senderID,fileId);

        try {
            Message msg = new Message(header);
            mc.sendGetLease(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
