import java.io.IOException;
import java.net.*;

/**
 * Created by joaobarbosa on 10-03-2017.
 */
public class Server {
    private MulticastSocket mcSocket;
    private InetAddress mcAddr;
    private int mcPort;

    private MulticastSocket mdbSocket;
    private InetAddress mdbAddr;
    private int mdbPort;

    private MulticastSocket mdrSocket;
    private InetAddress mdrAddr;
    private int mdrPort;

    public static void main(String[] args) {
        Server server = new Server(args);
        server.start();
    }

    public Server(String[] commands) {
        try {
            mcAddr = InetAddress.getByName(commands[0]);
            mcPort = Integer.parseInt(commands[1]);

            mcSocket = new MulticastSocket(mcPort);
            mcSocket.joinGroup(mcAddr);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        /*
        try {
            mdbAddr = InetAddress.getByName(commands[2]);
            mdbPort = Integer.parseInt(commands[3]);

            mdbSocket = new MulticastSocket(mdbPort);
            mdbSocket.joinGroup(mdbAddr);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        try {
            mdrAddr = InetAddress.getByName(commands[2]);
            mdrPort = Integer.parseInt(commands[3]);

            mdrSocket = new MulticastSocket(mdrPort);
            mdrSocket.joinGroup(mdrAddr);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        */
    }


    public void start() {
        new Thread(() -> handleControlChannel()).start();

        String msg = "ola hehe";
        DatagramPacket packet = new DatagramPacket(msg.getBytes(), msg.length(), mcAddr, mcPort);
        try {
            mcSocket.send(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }



    public void handleControlChannel() {
        DatagramPacket packet = new DatagramPacket(new byte[256], 256);

        try {
            mcSocket.receive(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println(new String(packet.getData(), 0, packet.getLength()));
    }


    
    public void handleDataChannel() {

    }
}
