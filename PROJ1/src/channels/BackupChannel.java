package channels;

import filesystem.Chunk;
import filesystem.FileChunk;
import filesystem.FileManager;
import server.Server;
import utils.Message;

import java.io.IOException;
import java.net.DatagramPacket;
import java.nio.charset.StandardCharsets;

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
