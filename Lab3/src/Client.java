import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.*;

/**
 * Created by jpedrotm on 03-03-2017.
 */
public class Client{

    private InetAddress address;
    private String operation;
    private String plate;
    private String owner;
    private String port;

    public static void main(String[] args) throws IOException {

        Client client=new Client(args[0],args[1],args[2],args);
        client.start();


    }

    public Client(String hostName,String port,String opr,String[] args) throws IOException {

        System.out.println("PORT: "+port);

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

        Socket echoSocket=new Socket(address,Integer.parseInt(port));

        PrintWriter out=new PrintWriter(echoSocket.getOutputStream(),true);
        BufferedReader in=new BufferedReader(new InputStreamReader(echoSocket.getInputStream()));
        String message;

        if(this.operation.equals("REGISTER")){

            message=this.operation+" "+this.plate+" "+this.owner;
            out.println(message);


        }else if(this.operation.equals("LOOKUP")){

            message=this.operation+" "+this.plate;
            out.println(message);

        }else{
            System.out.println("Operation not available.");
        }

        this.waitAnswer(in);
        this.closeConnections(in,out);

    }

    private void waitAnswer(BufferedReader in) throws IOException {

        String messageReceived=in.readLine();
        System.out.println("Server message: "+messageReceived);

    }

    private void closeConnections(BufferedReader in,PrintWriter out) throws IOException {

        in.close();
        out.close();


    }

}
