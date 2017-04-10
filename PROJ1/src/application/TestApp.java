package application;

import server.PeerInterface;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class TestApp {

    private String peerAccessPoint;
    private String protocol; //sub protocol usado
    private String filePath; //path do ficheiro para faze backup
    private String nRep; //número de replicações para fazer do ficheiro (apenas em caso do sub protocolo backup)

    public static void main(String[] args){
        try {
            TestApp application = new TestApp(args);
            application.start();
        }
        catch (IllegalArgumentException e) {
            System.err.println("\nInicialização inválida: \n" +
                    "\t<AP> BACKUP <File Path> <Replication Degree> \n" +
                    "\t<AP> RESTORE <File Path> \n" +
                    "\t<AP> DELETE <File Path> \n" +
                    "\t<AP> STATE \n");
        }
    }

    public TestApp(String[] args){

        if (this.initializeVariables(args) == 1) {
            throw new IllegalArgumentException("Número de argumentos inválido.");
        }
    }

    private int initializeVariables(String[] args) {

        int n = args.length;

        switch(n) {
            case 4: //backup
                if (!args[1].equals("BACKUP")) {
                    return 1;
                }

                this.peerAccessPoint = args[0];
                this.protocol = args[1];
                this.filePath = args[2];
                this.nRep = args[3];
                break;
            case 3: //restore and delete
                if (!args[1].equals("RESTORE") && !args[0].equals("DELETE")) {
                    return 1;
                }

                this.peerAccessPoint = args[0];
                this.protocol=args[1];
                this.filePath=args[2];
                this.nRep=null;
                break;
            case 2: //state
                if (!args[1].equals("STATE")) {
                    return 1;
                }

                this.peerAccessPoint = args[0];
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
            PeerInterface stub=(PeerInterface) registry.lookup(peerAccessPoint);

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
