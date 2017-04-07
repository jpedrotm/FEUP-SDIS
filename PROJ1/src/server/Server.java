package server;

import channels.BackupChannel;
import channels.ControlChannel;
import channels.DataChannel;
import filesystem.FileManager;
import metadata.Metadata;
import protocols.Backup;
import protocols.Delete;
import protocols.Restore;
import utils.GoodGuy;
import utils.Tuplo3;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Timer;
import java.util.TimerTask;

public class Server implements PeerInterface {
    private String serverID;
    private ControlChannel mc;
    private BackupChannel mdr;
    private DataChannel mdb;
    private Thread controlThread;
    private Thread dataThread;
    private Thread backupThread;
    private Tuplo3 removedTuple;

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
        this.removedTuple = null;

        loadMetadata();
        loadFileManager();

        this.mc = new ControlChannel(this, commands[1], commands[2]);
        this.mdb = new DataChannel(this, commands[3], commands[4]);
        this.mdr = new BackupChannel(this,commands[5],commands[6]);

        Runtime.getRuntime().addShutdownHook(new Thread()
        {
            @Override
            public void run()
            {
                mc.shutdown();
                mdb.shutdown();
                mdr.shutdown();
                controlThread.interrupt();
                dataThread.interrupt();
                backupThread.interrupt();

                try {
                    saveMetadata();
                    System.out.println("Metadata stored!");
                } catch (IOException e) {
                    System.err.println("Error storing metadata... Please check for inconsistencies.");
                }

                try {
                    saveFileManager();
                    System.out.println("FileManager stored!");
                } catch (IOException e) {
                    System.err.println("Error storing file manager... Please check for inconsistencies.");
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
        */



        if (serverID.equals("1")) {
            try {
                Thread.sleep(4000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            backup("amizade.jpg", "2");

            try {
                Thread.sleep(4000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            restore("amizade.jpg");
        }

        try {
            Thread.sleep(16000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        System.out.println(FileManager.instance());
        System.out.println(Metadata.instance());
    }

    public String getServerID() {
        return serverID;
    }

    public BackupChannel getBackupChannel(){
        return mdr;
    }

    public ControlChannel getControlChannel(){
        return mc;
    }

    public DataChannel getDataChannel(){
        return mdb;
    }

    public Tuplo3 getRemovedTuple(){
        return removedTuple;
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

    public void newRemovedTuple(Tuplo3 tuple){
        this.removedTuple=tuple;
    }

    public void resetRemovedTuple(){
        this.removedTuple=null;
    }

    public void updateRemovedTuple(String fileID,int chunkNo){
        if(removedTuple!=null){
            removedTuple.verifyEquality(fileID,chunkNo);
        }
    }

    public void backup(String path, String replicationDeg) {
        try {
            Backup.sendFileChunks(mdb, path,"1.0", serverID, replicationDeg);
        } catch (IOException e) {
            System.out.println("File not found.");
        }
    }


    public void delete(String path) {
        try {
            Delete.DeleteFile(mc, path, "1.0", serverID);
        } catch (IOException e) {
            System.out.println("File not found.");
        }
    }

    public void restore(String path) {
        try {
            Restore.receiveFileChunks(mc, mdr, path,"1.0",serverID);
        } catch (IOException e) {
            System.out.println("Error restoring chunks.");
        }
    }

    public void reclaim(String space){
        int newMaxContentSize=Integer.parseInt(space)*1000; //porque o tamanho é dado e Kb
        System.out.println("new content size: "+newMaxContentSize);
        FileManager.instance().updateLimitContentSize(newMaxContentSize,mc,serverID);
    }

    public String state(){


        String stateInfo = "Informação da metadata: \n" + Metadata.instance().toString() + "\nInformação do FileManager: \n" + FileManager.instance().toString();
        return stateInfo;
    }


    private void saveFileManager() throws IOException {
        String savePath = "storage/filemanager/" + serverID;
        Path pathToFile = Paths.get(savePath);
        Files.createDirectories(pathToFile.getParent());

        FileOutputStream fout = new FileOutputStream(savePath);
        ObjectOutputStream oos = new ObjectOutputStream(fout);
        oos.writeObject(FileManager.instance());
    }

    private void loadFileManager() {
        String loadPath = "storage/filemanager/" + serverID;
        try {
            FileInputStream stream = new FileInputStream(loadPath);
            ObjectInputStream ois = new ObjectInputStream(stream);
            FileManager.load((FileManager) ois.readObject());
        } catch (Exception e) {}
    }

    private void saveMetadata() throws IOException {
        String savePath = "storage/metadata/" + serverID;
        Path pathToFile = Paths.get(savePath);
        Files.createDirectories(pathToFile.getParent());

        FileOutputStream fout = new FileOutputStream(savePath);
        ObjectOutputStream oos = new ObjectOutputStream(fout);
        oos.writeObject(Metadata.instance());
    }

    private void loadMetadata() {
        String loadPath = "storage/metadata/" + serverID;
        try {
            FileInputStream stream = new FileInputStream(loadPath);
            ObjectInputStream ois = new ObjectInputStream(stream);
            Metadata.load((Metadata) ois.readObject());
        } catch (Exception e) {}
    }
}
