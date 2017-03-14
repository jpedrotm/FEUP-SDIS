package channels;

import server.Server;

import java.io.IOException;
import java.net.DatagramPacket;

public class DataChannel extends Channel{

    public DataChannel(Server server, String addressStr, String portVar){
        super(server, addressStr,portVar);
    }

    /*
    @Override
    void start() {

        new Thread(() -> handler()).start();

        String msg = "ola hehe";
        DatagramPacket packet = new DatagramPacket(msg.getBytes(), msg.length(), address, port);
        try {
            socket.send(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
    */

    @Override
    void handler() {

        DatagramPacket packet = new DatagramPacket(new byte[256], 256);

        try {
            socket.receive(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println(new String(packet.getData(), 0, packet.getLength()));

    }

    @Override
    public void run() {

    }
}
