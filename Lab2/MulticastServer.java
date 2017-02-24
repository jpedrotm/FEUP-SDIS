import java.io.IOException;
import java.net.*;

/**
 * Created by joaobarbosa on 24-02-2017.
 */
public class MulticastServer {

    final static String INET_ADDR = "239.0.0.10";
    final static int PORT = 8888;

    private InetAddress address;
    private DatagramSocket socket;


    public static void main(String[] args) throws IOException, InterruptedException {
        MulticastServer server = new MulticastServer();
        server.start();
    }


    MulticastServer() throws UnknownHostException, SocketException {
        address = InetAddress.getByName(INET_ADDR);
        socket = new DatagramSocket();
    }

    public void start() throws InterruptedException, IOException {
        for (int i = 0; i < 5; i++) {
            String msg = "Sent message no " + i;

            DatagramPacket packet = new DatagramPacket(msg.getBytes(), msg.getBytes().length, address, PORT);
            socket.send(packet);
            System.out.println("Server sent packet with msg: " + msg);
            Thread.sleep(1000);
        }
    }
}
