import java.rmi.Remote;
import java.rmi.RemoteException;

public interface interfaceClient extends Remote{
    String sayHello() throws RemoteException;

}
