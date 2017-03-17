package server;


import java.rmi.Remote;
import java.rmi.RemoteException;

public interface PeerInterface extends Remote { //todas as funções do Server são colocadas aqui

    void start() throws RemoteException;
    String getServerID() throws RemoteException;
    void writeID() throws RemoteException;
}
