package server;

import channels.ControlChannel;
import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class Server implements PeerInterface {
    private String serverID;
    private ControlChannel mc;
    //private BackupChannel mdr;
    //private DataChannel mdb;

    public static void main(String[] args) {

        try {
            Server server = new Server(args);
            PeerInterface stub=(PeerInterface) UnicastRemoteObject.exportObject(server,0);
            Registry registry= LocateRegistry.getRegistry();
            registry.bind(server.getServerID(),stub);

            System.err.println("Server is ready.");

        } catch (RemoteException | AlreadyBoundException e) {
            e.printStackTrace();
        }

        //server.start();
    }

    public Server(String[] commands) {
        this.mc = new ControlChannel(this, commands[0], commands[1]);
        //this.mdr=new BackupChannel(commands[0],commands[1]);
        //this.mdb=new DataChannel(commands[0],commands[1]);
        this.serverID=commands[2]; //temporario só para testar o RMI
    }

    public void start() {
        new Thread(mc).start();
    }

    public void writeID(){
        System.out.println("My id: "+this.serverID);
    }

    public String getServerID() {
        return serverID;
    }
}