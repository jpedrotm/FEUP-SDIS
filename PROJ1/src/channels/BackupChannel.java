package channels;

import server.Server;

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
