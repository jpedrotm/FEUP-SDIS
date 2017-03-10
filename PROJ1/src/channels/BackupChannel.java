package channels;

import java.io.IOException;
import java.net.DatagramPacket;

public class BackupChannel extends Channel{

    public BackupChannel(String addressStr, String portVar){
        super(addressStr,portVar);
    }

    @Override
    void start(){

    }

    @Override
    void handler() {

    }


}
