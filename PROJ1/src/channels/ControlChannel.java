package channels;

import java.io.IOException;
import java.net.DatagramPacket;

public class ControlChannel extends Channel{

    public ControlChannel(String addressStr, String portVar){
        super(addressStr,portVar);
    }

    @Override
    void start() {

    }

    @Override
    void handler() {


    }

}
