import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import java.io.*;
import java.net.SocketException;
import java.util.HashMap;



public class SSLServer {

    final public int TYPE=0;
    final public int PLATE=1;
    final public int OWNER=2;

    private HashMap<String,String> database;
    private SSLServerSocket requestSocket;

    public static void main(String[] args) throws IOException {

        if(args.length<2)
        {
            System.out.println("Usage: Wrong number of arguments.\n");
            return;
        }

        try{

            SSLServer server=new SSLServer(args);
            server.start();

        }catch(SocketException e){
            System.out.println("Error opening server.\n");
        }

    }

    public SSLServer(String args[]) throws IOException {

        this.database=new HashMap<>();

        String[] cypherSuites;
        int numCyphers=args.length-1;
        cypherSuites=new String[numCyphers];
        for(int i=0;i<numCyphers;i++){
            int j=i+1;
            cypherSuites[i]=args[j];
        }

        try {
            SSLServerSocketFactory ssf = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault(); //apenas para ajudar a criar objetos da SSLServerSocket
            requestSocket = (SSLServerSocket) ssf.createServerSocket(Integer.parseInt(args[0]));
            requestSocket.setNeedClientAuth(true); //para indicar que é preciso autentificação da parte do utilizador

            if(cypherSuites.length==0){
                requestSocket.setEnabledCipherSuites(ssf.getDefaultCipherSuites());
            }
            else{
                requestSocket.setEnabledCipherSuites(cypherSuites);
            }
        }
        catch( IOException e) {
            System.out.println("Server - Failed to create SSLServerSocket");
            e.getMessage();
        }
    }

    public void start() throws IOException {


        while(true){
            SSLSocket echoSocket=(SSLSocket) requestSocket.accept();
            PrintWriter out=new PrintWriter(echoSocket.getOutputStream(),true);
            BufferedReader in=new BufferedReader(new InputStreamReader(echoSocket.getInputStream()));

            String buffer=in.readLine();

            String[] tokens=buffer.split("\\s");

            System.out.println("COMMAND: "+buffer);

            if(tokens[TYPE].equals("REGISTER") && tokens.length==3)
            {
                int num=this.register(tokens[PLATE],tokens[OWNER]);
                String result=Integer.toString(num);
                out.println(result);

            }else if(tokens[TYPE].equals("LOOKUP") && tokens.length==2)
            {
                System.out.println("ENTERED LOOKUP");
                String name=this.lookup(tokens[PLATE]);
                out.println(name);

            }
            else
            {
                System.out.println("Not a valid command.\n");
            }

        }

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
}
