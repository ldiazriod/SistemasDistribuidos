import org.omg.CORBA.ORB;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;

import HelloApp.*;

public class Client {
    static IHelloWorld hImpl;
    public static void main(String args[]) {
        try{
            ORB orb = ORB.init(args, null);

            org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService");
            NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);

            //Pedimos el objeto, sacamos por pantalla el objeto que ha llegado y el valor de retorno del metodo "HelloWorld()" del objeto que llega.
            String name = "IHelloWorld";
            hImpl = IHelloWorldHelper.narrow(ncRef.resolve_str(name));
            System.out.println("Obtained a hadle on server object: " +  hImpl);
            System.out.println(hImpl.HelloWorld());
            hImpl.shutdown(); //Paramos el servidor.

        }catch(Exception e){
            System.out.println("ERROR: " + e);
            e.printStackTrace(System.out);
        }
    }
}
