package server;

import channels.*;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Timer;
import java.util.TimerTask;

import channels.DataChannel;
import filesystem.FileManager;
import protocols.Backup;
import protocols.Delete;
import protocols.Restore;
import utils.GoodGuy;

public class Server implements PeerInterface {
    private String serverID;
    private Metadata metadata;
    private ControlChannel mc;
    private BackupChannel mdr;
    private DataChannel mdb;
    private Thread controlThread;
    private Thread dataThread;
    private Thread backupThread;

    public static void main(String[] args) {
        try {
            Server server = new Server(args);
            PeerInterface stub = (PeerInterface) UnicastRemoteObject.exportObject(server,0);
            Registry registry = LocateRegistry.getRegistry();
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
        this.mdr=new BackupChannel(this,commands[5],commands[6]);

        Runtime.getRuntime().addShutdownHook(new Thread()
        {
            @Override
            public void run()
            {
                mc.getSocket().close();
                mc.shutdown();
                mdb.getSocket().close();
                mdb.shutdown();
                mdr.getSocket().close();
                mdr.shutdown();
                controlThread.interrupt();
                dataThread.interrupt();
                backupThread.interrupt();

                try {
                    saveMetadata();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void start() {
        controlThread = new Thread(mc);
        dataThread = new Thread(mdb);
        backupThread=new Thread(mdr);

        controlThread.start();
        dataThread.start();
        backupThread.start();

        /*if(serverID.equals("1")){

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            System.out.println("GOOOOOOO");

            backup("storage/ola.pdf", "3");

            try {
                Thread.sleep(4000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            System.out.println(FileManager.instance());

            restore("storage/ola.pdf");
        }*/

        //delete("storage/asdas.pdf");

        /*try {
            Thread.sleep(6000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        System.out.println(FileManager.instance());*/

        try {
            Thread.sleep(8000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(metadata);
    }

    public String getServerID() {
        return serverID;
    }

    public BackupChannel getBackupChannel(){
        return mdr;
    }

    public Metadata getMetadata(){
        return metadata;
    }

    public void sendStored(String fileID, int chunkNo) {
        new Timer().schedule(
                new TimerTask() {
                    @Override
                    public void run() {
                        Backup.sendStoredMessage(mc, fileID, chunkNo, serverID);
                    }
                },
                GoodGuy.sleepTime(0, 400)
        );
    }

    public void backup(String path, String replicationDeg) {
        try {
            Backup.sendFileChunks(metadata, mdb, path,"1.0", serverID, replicationDeg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void delete(String path) {
        try {
            Delete.DeleteFile(metadata, mc, path, "1.0", serverID);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void restore(String path){
        try {
            Restore.receiveFileChunks(metadata,mc, path,"1.0",serverID);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void saveMetadata() throws IOException {
        String savePath = "storage/metadata/" + serverID;
        Path pathToFile = Paths.get(savePath);
        Files.createDirectories(pathToFile.getParent());

        FileOutputStream fout = new FileOutputStream(savePath);
        ObjectOutputStream oos = new ObjectOutputStream(fout);
        oos.writeObject(metadata);
    }
}
