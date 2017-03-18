package server;

import channels.ControlChannel;
import protocols.BackupProtocol;

import java.io.IOException;

public class Server {
    private String serverID;
    private ControlChannel mc;
    //private BackupChannel mdr;
    //private DataChannel mdb;

    public static void main(String[] args) {
        Server server = new Server(args);
        server.start();
    }

    public Server(String[] commands) {
        this.mc = new ControlChannel(this, commands[0], commands[1]);
        //this.mdr=new BackupChannel(commands[0],commands[1]);
        //this.mdb=new DataChannel(commands[0],commands[1]);

        try {
            BackupProtocol.sendFileChunks(mc, "storage/asdas.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void start() {
        new Thread(mc).start();
    }

    public String getServerID() {
        return serverID;
    }

    public void readFile() {

    }
}
