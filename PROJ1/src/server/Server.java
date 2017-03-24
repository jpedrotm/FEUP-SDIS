package server;

import channels.*;
import protocols.*;

import java.awt.*;
import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import channels.DataChannel;
import protocols.BackupProtocol;
import utils.Message;

public class Server implements PeerInterface {
    private String serverID;
    private ControlChannel mc;
    private BackupChannel mdr;
    private DataChannel mdb;

    public static void main(String[] args) {

        try {
            Server server = new Server(args);
            System.out.println("ID: "+server.getServerID());
            PeerInterface stub = (PeerInterface) UnicastRemoteObject.exportObject(server,0);
            Registry registry= LocateRegistry.getRegistry();
            registry.rebind(server.getServerID(), stub);
            server.start();

            System.err.println("Server is ready.");

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
    }

    public void start() {
        //new Thread(mc).start();
        new Thread(mdb).start();

        try {
            BackupProtocol.sendFileChunks(mdb, "storage/a3_prototipo_da_interface_com_o_utilizador.pdf","1.0",serverID,"3");
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

    public void sendStored(String fileID,int chunkNo){
        BackupProtocol.sendStoredMessage(mc,fileID,chunkNo,serverID);
    }

    public void readFile() {

    }
}
