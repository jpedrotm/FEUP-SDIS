package server;

import channels.BackupChannel;
import channels.ControlChannel;
import channels.DataChannel;
import filesystem.Chunk;
import filesystem.FileChunk;
import filesystem.FileManager;
import metadata.FileMetadata;
import metadata.Metadata;
import protocols.Backup;
import protocols.Delete;
import protocols.LeaseProto;
import protocols.Restore;
import utils.FileChunkListener;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

public class Server implements PeerInterface, FileChunkListener {
    private String serverID;
    private String version;
    private String accessPoint;
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
            registry.rebind(server.getAccessPoint(), stub);
            server.start();
        } catch (RemoteException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    public Server(String[] commands) {
        

        this.version=commands[Arguments.ProtocolVersion];
        this.serverID=commands[Arguments.ServerID];
        this.accessPoint=commands[Arguments.AccessPoint];

        //----------------------------------------------------

        this.removedTuple = null;

        loadMetadata();
        loadFileManager();

        this.mc = new ControlChannel(this, commands[1], commands[2]);
        this.mdb = new DataChannel(this, commands[3], commands[4]);
        this.mdr = new BackupChannel(this,commands[5],commands[6]);

        handleTransactions();
        reviewFileManager();

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

        System.out.println("Server " + serverID + " is now online!");
    }

    public void start() {
        controlThread = new Thread(mc);
        dataThread = new Thread(mdb);
        backupThread=new Thread(mdr);

        controlThread.start();
        dataThread.start();
        backupThread.start();
    }

    private void handleTransactions() {

        // Interrupted backups
        HashMap<String, FileMetadata> fms = Metadata.instance().getFileInfoHashMap();
        for (FileMetadata fileMetadata : fms.values()) {
            if (fileMetadata.isOnTransaction()) {
                try {
                    Backup.sendFileChunks(mdb, fileMetadata.getPath(), "1.0", serverID, Integer.toString(fileMetadata.getRepDegree()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        // Interrupted putchunks (after removed)
        HashMap<String, FileChunk> fcs = FileManager.instance().getFiles();
        for (FileChunk fileChunk : fcs.values()) {
            if (fileChunk.isOnTransaction()) {
                ArrayList<Chunk> chunks = fileChunk.getChunks();
                for (Chunk chunk : chunks) {
                    Backup.sendPutchunkFromRemoved(mdb, serverID, fileChunk, chunk);
                }
            }
        }
    }

    public String getServerID() {
        return serverID;
    }

    public String getAccessPoint(){
        return accessPoint;
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

    public void sendStored(String fileID, int chunkNo,String version) {
        new Timer().schedule(
                new TimerTask() {
                    @Override
                    public void run() {
                        Backup.sendStoredMessage(mc, fileID, chunkNo, serverID,version);
                    }
                },
                GoodGuy.randomBetween(0, 400)
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
            Backup.sendFileChunks(mdb, path,version, serverID, replicationDeg);
        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }


    public void delete(String path) {
        try {
            Delete.DeleteFile(mc, path, version, serverID);
        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    public void restore(String path) {
        try {
            Restore.receiveFileChunks(mc, mdr, path, version, serverID);
        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    public void reclaim(String space){
        int newMaxContentSize=Integer.parseInt(space)*1000; //porque o tamanho é dado e Kb
        FileManager.instance().updateLimitContentSize(newMaxContentSize, mc, serverID, version);
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

    private void reviewFileManager(){
        FileManager.instance().refresh(this);
    }

    @Override
    public void notify(String fileId) {
        GoodGuy.sleepRandomTime(0, 400);
        FileManager.instance().addPendingLease(fileId);
        LeaseProto.sendGetLease(mc, "1.0", serverID, fileId);
    }

    private class Arguments{
        public final static int ProtocolVersion=0;
        public final static int ServerID=1;
        public final static int AccessPoint=2;
        public final static int MC_IPAddress=3;
        public final static int MC_Port=4;
        public final static int MDB_IPAddress=5;
        public final static int MDB_Port=6;
        public final static int MDR_IPAddress=7;
        public final static int MDR_Port=8;
    }


    public String getVersion() {
        return version;
    }
}
