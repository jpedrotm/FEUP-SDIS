import java.io.IOException;
import java.net.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Client {

    private Client() {}

    public static void main(String[] args) {

        String host = (args.length < 1) ? null : args[0];
        try {
            Registry registry = LocateRegistry.getRegistry(host);
            interfaceClient stub = (interfaceClient) registry.lookup("Hello");
            String response = stub.sayHello();
            System.out.println("response: " + response);
        } catch (Exception e) {
            System.err.println("Client exception: " + e.toString());
            e.printStackTrace();
        }
    }
}


/*public class Client{

    final public int BUF_SIZE=512;

    private DatagramPacket packet;
    private DatagramSocket socket;
    private InetAddress address;
    private String operation;
    private String plate;
    private String owner;
    private String port;

    public static void main(String[] args) throws IOException {

        if(args.length!=4 || args.length!=5)
        {
            System.out.println("Wrong number of arguments.");
        }

        try{

            Client client=new Client(args[0],args[1],args[2],args);
            client.start();

        }catch(SocketException e){
            System.out.println("Error opening server.\n");
        }




    }

    public Client(String hostName,String port,String opr,String[] args) throws SocketException, UnknownHostException {

        System.out.println("PORT: "+port);

        this.socket=new DatagramSocket();
        this.address=InetAddress.getByName(hostName);
        this.operation=opr;
        this.plate=args[3];
        this.port=port;

        if(this.operation.equals("REGISTER")){
            this.owner=args[4];
        }else{
            this.owner=null;
        }

    }

    public void start() throws IOException {


        String message;
        byte[] sbuf;

        if(this.operation.equals("REGISTER")){

            message=this.operation+" "+this.plate+" "+this.owner;
            sbuf=message.getBytes();
            packet=new DatagramPacket(sbuf,sbuf.length,this.address,Integer.parseInt(this.port));
            this.socket.send(packet);


        }else if(this.operation.equals("LOOKUP")){

            message=this.operation+" "+this.plate;
            sbuf=message.getBytes();
            packet=new DatagramPacket(sbuf,sbuf.length,this.address,Integer.parseInt(this.port));
            this.socket.send(packet);

        }else{
            System.out.println("Operation not available.");
        }

        try {
            this.waitAnswer();
        }
        catch(IOException e){
            System.out.println("Server answer not received.");
        }

    }

    private void waitAnswer() throws IOException {

        packet=new DatagramPacket(new byte[BUF_SIZE],BUF_SIZE);
        socket.receive(packet);
        String messageReceived=new String(packet.getData());
        System.out.println("Server message: "+messageReceived);

    }

}*/