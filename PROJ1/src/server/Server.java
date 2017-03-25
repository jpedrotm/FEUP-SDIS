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
import filesystem.FileManager;
import protocols.BackupProtocol;
import utils.GoodGuy;

public class Server implements PeerInterface {
    private String serverID;
    private Metadata metadata;
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
        this.metadata = new Metadata();

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

        backup("storage/amizade.jpg", "3");

        try {
            Thread.sleep(6000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println(FileManager.instance());
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
                        BackupProtocol.sendStoredMessage(mc, fileID, chunkNo, serverID);
                    }
                },
                GoodGuy.sleepTime(0, 400)
        );
    }

    public void readFile() {

    }


    public boolean hasMetadata(String key) {
        return (metadata.contains(Metadata.InfoRequest.FILENAME, key) || metadata.contains(Metadata.InfoRequest.HASH, key));
    }

    public void backup(String path, String replicationDeg) {
        try {
            BackupProtocol.sendFileChunks(metadata, mdb, path,"1.0", serverID, replicationDeg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
