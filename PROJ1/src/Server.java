import channels.ControlChannel;

public class Server {

    private ControlChannel mc;
    //private BackupChannel mdr;
    //private DataChannel mdb;

    public static void main(String[] args) {
        Server server = new Server(args);
        server.start();
    }

    public Server(String[] commands) {
        this.mc=new ControlChannel(commands[0],commands[1]);
        //this.mdr=new BackupChannel(commands[0],commands[1]);
        //this.mdb=new DataChannel(commands[0],commands[1]);
    }

    public void start() {

    }
}
