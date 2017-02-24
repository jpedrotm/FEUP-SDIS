import java.io.IOException;
import java.net.*;

/**
 * Created by joaobarbosa on 24-02-2017.
 */
public class MulticastClient {
    final static String INET_ADDR = "239.0.0.10";
    final static int PORT = 8888;

    private InetAddress address;
    private MulticastSocket socket;

    public static void main(String[] args) throws IOException, InterruptedException {
        MulticastClient client = new MulticastClient();
        client.start();
    }

    MulticastClient() throws IOException {
        address = InetAddress.getByName(INET_ADDR);
        socket = new MulticastSocket(PORT);
        socket.joinGroup(address);
    }

    public void start() throws IOException {
        DatagramPacket packet = new DatagramPacket(new byte[256], 256);
        socket.receive(packet);

        String msg = new String(packet.getData(), 0, packet.getLength());
        System.out.println(msg);
    }
}
