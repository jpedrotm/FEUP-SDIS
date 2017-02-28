import java.awt.dnd.DragGestureEvent;
import java.io.IOException;
import java.net.*;

/**
 * Created by jpedrotm on 17-02-2017.
 */
public class Client{

    final public int BUF_SIZE=512;

    private InetAddress macAddress;
    private MulticastSocket macSocket;
    private int macPort;

    private DatagramPacket packet;
    private DatagramSocket socket;
    private InetAddress address;

    private String operation;
    private String plate;
    private String owner;
    private String port;

    public static void main(String[] args) throws IOException {

        /*if(args.length!=4 || args.length!=5)
        {
            System.out.println("Wrong number of arguments.");
        }*/

        try{

            Client client=new Client(args[0],args[1],args[2],args);
            client.initializePorts();
            client.start();

        }catch(IOException e){
            System.out.println("Error opening server.\n");
        }




    }

    public Client(String mcaAddress,String mcaPort,String opr,String[] args) throws IOException {

        System.out.println(mcaAddress+" "+mcaPort+" "+opr);

        this.macAddress=InetAddress.getByName(mcaAddress);

        this.macSocket=new MulticastSocket(Integer.parseInt(mcaPort));

        this.operation=opr;
        this.plate=args[3];
        this.macPort=Integer.parseInt(mcaPort);

        this.socket=new DatagramSocket();

        if(this.operation.equals("REGISTER")){
            this.owner=args[4];
        }else{
            this.owner=null;
        }

    }

    public void initializePorts() throws IOException {

        byte[] buf=new byte[BUF_SIZE];

        macSocket.joinGroup(macAddress);

        DatagramPacket tmpPacket=new DatagramPacket(buf,buf.length);
        macSocket.receive(tmpPacket);
        String info=new String(tmpPacket.getData(),0,tmpPacket.getLength());

        //inicializar variáveis para envio de mensagem do cliente para o servidor
        this.address=tmpPacket.getAddress();
        this.port=info;

        System.out.println("multicast: " +macAddress+" "+macPort+" : "+address+" "+port);
        macSocket.leaveGroup(macAddress);

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

}
