
import HelloApp.*;
import org.omg.CosNaming.*;
import org.omg.CORBA.*;
import org.omg.PortableServer.*;
import org.omg.PortableServer.POA;

/*
    https://docs.oracle.com/javase/7/docs/technotes/guides/idl/jidlExample.html
    https://www.ooportal.com/corba-programming/module3/corba-helper-classes.php
    http://grasia.fdi.ucm.es/jpavon/docencia/dso/corba05servidoresjava.pdf
    https://docs.oracle.com/javase/7/docs/technotes/guides/idl/POA.html
    https://docs.oracle.com/javase/7/docs/technotes/guides/idl/tutorial/GSserver.html
*/

//Creamos la clase HelloImpl que es una implementaión de la interfaz IHelloWorld definida en HelloWorld.idl.
class HelloImpl extends IHelloWorldPOA {
    private ORB orb; //Declaramos una variable de tipo ORB. ORB es una clase que nos permite trabajar con las APIs de CORBA

    public void setORB(ORB _orb){ //Clase setter
        orb = _orb;
    }
    public String HelloWorld() { //Implementamos el metodo "HelloWorld" que nos devuelve "Hello World!"
        return "Hello World!";
    }
    public void shutdown() { //Implementamos el metodo "shutdown" que, mediante el metodo "shutdown" de la clase ORB, cierra y destruye todos los adaptadores de los objetos.
        orb.shutdown(false); //Le entra como opción un parametro booleano, este parametro (wait_for_completion) al estar en false no espera a que todos los procesos terminen.
    }
}

public class Server { //Clase del servidor.
    public static void main(String args[]){
        try{
            ORB orb = ORB.init(args, null); //Crea e inicializa un objeto ORB.

            //Declaramos un POA (Portable Object Adapter) y lo inicializamos al POA raíz.
            POA rootpoa = POAHelper.narrow(orb.resolve_initial_references("RootPOA"));
            rootpoa.the_POAManager().activate(); //Habilitamos la entrada de request

            HelloImpl hImpl = new HelloImpl(); //Declaramos un nuevo objeto HelloImpl 
            hImpl.setORB(orb); //Le pasamos el objeto ORB al objeto

            org.omg.CORBA.Object ref = rootpoa.servant_to_reference(hImpl); //Guardamos la referencia al objeto hImpl
            IHelloWorld href = IHelloWorldHelper.narrow(ref); //Convertimos la referencia anterior al tipo correcto para la interfaz.

            //Guardamos la referencia de objeto al nombre del servidor.
            org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService"); 
            NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);

            String name = "IHelloWorld"; //Guardamos el nombre
            NameComponent path[] = ncRef.to_name(name); //Creamos un path con el nombre al objeto
            ncRef.rebind(path, href); //Juntamos el path con el objeto servant 

            System.out.println("Servidor listo y escuchando ...");
            orb.run(); //Iniciamos el servidor. Espera hasta que el cliente lanza una petición.

        }catch(Exception e){
            System.err.println("ERROR: " + e);
            e.printStackTrace(System.out);
        }
        System.out.println("Cerrando Server ...");
    }
}