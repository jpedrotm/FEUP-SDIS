package application;

import server.PeerInterface;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class TestApp {

    private int peerAccessPoint; //peer access point (não sei bem para que usar)
    private String protocol; //sub protocol usado
    private String filePath; //path do ficheiro para faze backup
    private String nRep; //número de replicações para fazer do ficheiro (apenas em caso do sub protocolo backup)

    public static void main(String[] args){

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("VAMOOOOOS");

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
            case 4: //backup
                this.peerAccessPoint=Integer.parseInt(args[0]);
                this.protocol=args[1];
                this.filePath=args[2];
                this.nRep=args[3];
                break;
            case 3: //restore
                this.peerAccessPoint=Integer.parseInt(args[0]);
                this.protocol=args[1];
                this.filePath=args[2];
                this.nRep=null;
                break;
            case 2: //state and delete
                this.peerAccessPoint=Integer.parseInt(args[0]);
                this.protocol=args[1];
                this.filePath=null;
                this.nRep=null;
                break;
            default:
                return 1;
        }

        return 0;

    }

    public void start(){
        try {
            Registry registry= LocateRegistry.getRegistry("localhost");
            PeerInterface stub=(PeerInterface) registry.lookup(Integer.toString(peerAccessPoint));

            switch(protocol)
            {
                case CommandType.Backup:
                    stub.backup(filePath,nRep);
                    break;
                case CommandType.Delete:
                    stub.delete(filePath);
                    break;
                case CommandType.Restore:
                    stub.restore(filePath);
                    break;
                case CommandType.Reclaim:
                    stub.reclaim(filePath); //filePath neste caso é o espaço novo a ser indicado ao peer
                    break;
                case CommandType.State:
                    System.out.println(stub.state());
                default:
                    break;
            }

        } catch (RemoteException | NotBoundException e) {
            e.printStackTrace();
        }

    }

    private static class CommandType{
        public static final String Backup="BACKUP";
        public static final String Delete="DELETE";
        public static final String Restore="RESTORE";
        public static final String Reclaim="RECLAIM";
        public static final String State="STATE";
    }
}
