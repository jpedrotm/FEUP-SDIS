import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.*;
import java.util.HashMap;

/**
 * Created by jpedrotm on 03-03-2017.
 */
public class Server {

    final public int TYPE=0;
    final public int PLATE=1;
    final public int OWNER=2;

    private HashMap<String,String> database;
    private ServerSocket requestSocket;

    public static void main(String[] args) throws IOException {

        if(args.length!=1)
        {
            System.out.println("Usage: Wrong number of arguments.\n");
        }

        try{

            Server server=new Server(args[0]);
            server.start();

        }catch(SocketException e){
            System.out.println("Error opening server.\n");
        }

    }

    public Server(String port) throws IOException {

        this.database=new HashMap<>();
        this.requestSocket=new ServerSocket(Integer.parseInt(port));


    }

    public void start() throws IOException {


        while(true){

            Socket echoSocket=requestSocket.accept();
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
