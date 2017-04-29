import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;


public class SSLClient {
    private InetAddress address;
    private String operation;
    private String plate;
    private String owner;
    private String port;
    private String[] cypherSuites;

    public static void main(String[] args) throws IOException {

        if(args.length<6){
            System.out.println("Usage: Wrong number of arguments.\n");
            return;
        }

        SSLClient client=new SSLClient(args[0],args[1],args[2],args);
        client.start();


    }

    public SSLClient(String hostName,String port,String opr,String[] args) throws IOException {

        this.address=InetAddress.getByName(hostName);
        this.port=port;
        this.operation=opr;
        this.plate=args[3];

        if(this.operation.equals("REGISTER")){
            this.owner=args[4];
            int numCyphers=args.length-5;
            cypherSuites=new String[numCyphers];
            for(int i=0;i<numCyphers;i++){
                int j=i+5;
                cypherSuites[i]=args[j];
            }
        }else{
            this.owner=null;
            int numCyphers=args.length-4;
            cypherSuites=new String[numCyphers];
            for(int i=0;i<numCyphers;i++){
                int j=i+4;
                cypherSuites[i]=args[j];
            }
        }
    }

    public void start() throws IOException {
        SSLSocket echoSocket=(SSLSocket) SSLSocketFactory.getDefault().createSocket(address,Integer.parseInt(port));

        if(cypherSuites.length==0){
            SSLServerSocketFactory ssf = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
            echoSocket.setEnabledCipherSuites(ssf.getDefaultCipherSuites());
        }
        else{
            echoSocket.setEnabledCipherSuites(cypherSuites);
        }

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

    private void closeConnections(BufferedReader in, PrintWriter out) throws IOException {
        in.close();
        out.close();
    }
}
