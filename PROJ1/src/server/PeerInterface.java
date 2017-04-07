package server;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface PeerInterface extends Remote { //todas as funções do Server são colocadas aqui
    void backup(String path,String replicationDeg) throws RemoteException;
    void delete(String path) throws RemoteException;
    void restore(String path) throws RemoteException;
    void reclaim(String space) throws RemoteException;
    String state() throws  RemoteException;
}