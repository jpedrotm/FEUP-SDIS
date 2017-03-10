package channels;

import java.io.IOException;
import java.net.InetAddress;
import java.net.MulticastSocket;

abstract class Channel {

    protected MulticastSocket socket;
    protected InetAddress address;
    protected int port;

    public Channel(String addressStr,String portVar){

        try {
            this.address = InetAddress.getByName(addressStr);
            this.port = Integer.parseInt(portVar);

            socket = new MulticastSocket(port);
            socket.joinGroup(address); //provavelmente o join não é feito aqui depois julgo eu
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    abstract void start();

    abstract void handler();

}
