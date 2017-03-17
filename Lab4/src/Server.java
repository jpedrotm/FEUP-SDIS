import java.io.IOException;
import java.net.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class Server implements interfaceClient {

    public Server() {}

    public String sayHello() {
        return "Hello, world!";
    }

    public static void main(String args[]) {

        try {
            //Criar um objeto remoto
            Server obj = new Server();
            interfaceClient stub = (interfaceClient) UnicastRemoteObject.exportObject(obj, 0);

            // Bind the remote object's stub in the registry
            Registry registry = LocateRegistry.getRegistry();
            registry.bind("Hello", stub);

            System.err.println("Server ready");
        } catch (Exception e) {
            System.err.println("Server exception: " + e.toString());
            e.printStackTrace();
        }
    }
}

/*public class Server {

    final public int BUF_SIZE=512;
    final public int TYPE=0;
    final public int PLATE=1;
    final public int OWNER=2;

    private HashMap<String,String> database;
    private DatagramSocket socket;
    public static void main(String[] args) throws IOException {

        if(args.length!=1)
        {
            System.out.println("Usage: Wrong number of arguments.\n");
        }

        try{

            Server server=new Server(args[0]);
            server.start();

        }catch(SocketException e){
            System.out.println("Error opening server.\n");
        }

    }

    public Server(String port) throws SocketException{

        this.database=new HashMap<>();
        this.socket=new DatagramSocket(Integer.parseInt(port));


    }

    public void start() throws IOException {

        while(true){
            DatagramPacket packet=new DatagramPacket(new byte[BUF_SIZE],BUF_SIZE);

            socket.receive(packet); //só sai do receive quando recebe alguma informação
            String buffer=new String(packet.getData());
            String[] tokens=buffer.split("\\s");

            System.out.println("COMMAND: "+buffer);

            if(tokens[TYPE].equals("REGISTER") && tokens.length==3)
            {
                int num=this.register(tokens[PLATE],tokens[OWNER]);
                String result=Integer.toString(num);

                try{
                    this.sendAnswer(result,packet);
                }
                catch(SocketException e){
                    System.out.println("Server response failed.");
                }

            }else if(tokens[TYPE].equals("LOOKUP") && tokens.length==2)
            {
                System.out.println("ENTERED LOOKUP");
                String name=this.lookup(tokens[PLATE]);

                try{
                    this.sendAnswer(name,packet);
                }
                catch(SocketException e){
                    System.out.println("Server response failed.");
                }

            }
            else
            {
                System.out.println("Not a valid command.\n");
            }

        }

    }

    private void sendAnswer(String answer,DatagramPacket packet) throws IOException {

        int port=packet.getPort();
        InetAddress address=packet.getAddress();
        byte sbuf[]=answer.getBytes();
        packet=new DatagramPacket(sbuf,sbuf.length,address,port);
        socket.send(packet);

    }

    private int register(String plate,String owner){

        System.out.println("PLATE: "+plate+"\n"+"Owner:"+owner);

        if(database.get(plate)==null)
        {
            database.put(plate,owner);
            return database.size();
        }

        System.out.println("Plate already registed.\n");

        return -1;
    }

    private String lookup(String plate){

        System.out.println("PLATE: "+plate);

        String name=database.get(plate);

        if(name!=null)
        {
            return name;
        }
        else{
            return "NOT_FOUND";
        }
    }

}*/
