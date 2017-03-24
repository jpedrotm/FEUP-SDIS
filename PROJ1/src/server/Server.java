package server;

import channels.*;
import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Timer;
import java.util.TimerTask;

import channels.DataChannel;
import protocols.BackupProtocol;
import utils.GoodGuy;

public class Server implements PeerInterface {
    private String serverID;
    private ControlChannel mc;
    private BackupChannel mdr;
    private DataChannel mdb;
    private Thread controlThread;
    private Thread dataThread;

    public static void main(String[] args) {

        try {
            Server server = new Server(args);
            PeerInterface stub = (PeerInterface) UnicastRemoteObject.exportObject(server,0);
            Registry registry= LocateRegistry.getRegistry();
            registry.rebind(server.getServerID(), stub);
            server.start();

        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public Server(String[] commands) {

        this.serverID = commands[0]; //temporario só para testar o RMI

        this.mc = new ControlChannel(this, commands[1], commands[2]);
        this.mdb = new DataChannel(this, commands[3], commands[4]);
        //this.mdr=new BackupChannel(commands[0],commands[1]);
        //this.mdb=new DataChannel(commands[0],commands[1]);

        Runtime.getRuntime().addShutdownHook(new Thread()
        {
            @Override
            public void run()
            {
                mc.getSocket().close();
                mc.shutdown();
                mdb.getSocket().close();
                mdb.shutdown();
                controlThread.interrupt();
                dataThread.interrupt();
            }
        });
    }

    public void start() {
        controlThread = new Thread(mc);
        dataThread = new Thread(mdb);

        controlThread.start();
        dataThread.start();

        try {
            BackupProtocol.sendFileChunks(mdb, "storage/amizade.jpg","1.0", serverID,"3");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeID(){
        System.out.println("My id: "+this.serverID);
    }

    public String getServerID() {
        return serverID;
    }

    public void sendStored(String fileID, int chunkNo) {
        new Timer().schedule(
                new TimerTask() {
                    @Override
                    public void run() {
                        System.out.println("Enviei");
                        BackupProtocol.sendStoredMessage(mc, fileID, chunkNo, serverID);
                    }
                },
                GoodGuy.sleepTime(0, 400)
        );
    }

    public void readFile() {

    }
}
