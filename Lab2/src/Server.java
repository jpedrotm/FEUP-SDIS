/**
 * Created by jpedrotm on 17-02-2017.
 */
import java.io.IOException;
import java.net.*;
import java.util.HashMap;

public class Server {

    final public int BUF_SIZE=512;
    final public int TYPE=0;
    final public int PLATE=1;
    final public int OWNER=2;

    private int port;
    private String macAddressStr;
    private int macPort;

    private InetAddress address1;
    private MulticastThread thread;
    private MulticastSocket socket1;

    private HashMap<String,String> database;
    private DatagramSocket socket;

    public static void main(String[] args) throws IOException {

        if(args.length!=3)
        {
            System.out.println("Usage: Wrong number of arguments.\n");
        }

        try{

            Server server=new Server(args[0],args[1],args[2]);
            server.start();

        }catch(SocketException e){
            System.out.println("Error opening server.\n");
        }

    }

    public Server(String port,String macAddressStr,String macPort) throws IOException {

        //Guardar inputs
        this.port=Integer.parseInt(port);
        this.macAddressStr=macAddressStr;
        this.macPort=Integer.parseInt(macPort);


        try {
            address1 = InetAddress.getByName(macAddressStr);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        socket1 = new MulticastSocket();

        this.database=new HashMap<>();
        this.socket=new DatagramSocket(Integer.parseInt(port));

        this.thread=new MulticastThread();
        thread.start();


    }

    public void start() throws IOException {

        while(true){
            DatagramPacket packet=new DatagramPacket(new byte[BUF_SIZE],BUF_SIZE);
            socket.receive(packet); //só sai do receive quando recebe alguma informação
            String buffer=new String(packet.getData(),0,packet.getLength());
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

    private class MulticastThread extends Thread{

        public void run(){

            while(true){

                try  {

                    String msg = Integer.toString(port);

                    System.out.println("VOU enviar");

                    DatagramPacket packet1 = new DatagramPacket(msg.getBytes(), msg.getBytes().length, address1, macPort);
                    socket1.send(packet1);

                    System.out.println("enviei");

                    Thread.sleep(1000);

                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }

            }

        }

    }

}
