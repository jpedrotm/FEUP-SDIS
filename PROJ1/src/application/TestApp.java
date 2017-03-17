package application;

import server.PeerInterface;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class TestApp {

    private int peerAp; //peer access point (não sei bem para que usar)
    private String subProtocol; //sub protocol usado
    private String filePath; //path do ficheiro para faze backup
    private int nRep; //número de replicações para fazer do ficheiro (apenas em caso do sub protocolo backup)

    public static void main(String[] args){

        TestApp application=new TestApp(args);
        application.start();

    }

    public TestApp(String[] args){

        if(this.initializeVariables(args)==1){ //secalhar está confuso mas foi o que pareceu melhor porque com o switch já verifico o número de argumentos uma vez que é único para cada "comando"
            System.out.println("Número de argumentos inválido.");
            return;
        }

    }

    private int initializeVariables(String[] args){

        int n=args.length;

        switch(n){
            case 4:
                this.peerAp=Integer.parseInt(args[0]);
                this.subProtocol=args[1];
                this.filePath=args[2];
                this.nRep=Integer.parseInt(args[3]);
                break;
            case 3:
                this.peerAp=Integer.parseInt(args[0]);
                this.subProtocol=args[1];
                this.filePath=args[2];
                this.nRep=-1;
                break;
            case 2:
                this.peerAp=Integer.parseInt(args[0]);
                this.subProtocol=args[1];
                this.filePath=null;
                this.nRep=-1;
                break;
            default:
                return 1;
        }

        return 0;

    }

    public void start(){

        try {

            Registry registry= LocateRegistry.getRegistry("localhost");
            PeerInterface stub=(PeerInterface) registry.lookup(Integer.toString(peerAp));
            stub.writeID();

        } catch (RemoteException | NotBoundException e) {
            e.printStackTrace();
        }

    }
}
