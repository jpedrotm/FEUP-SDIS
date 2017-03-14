package channels;

import server.Server;

import java.io.IOException;
import java.net.DatagramPacket;

public class BackupChannel extends Channel{

    public BackupChannel(Server server, String addressStr, String portVar){
        super(server, addressStr,portVar);
    }

    @Override
    void handler() {

    }


    @Override
    public void run() {

    }
}
