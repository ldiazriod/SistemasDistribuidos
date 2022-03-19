package client;

import conn.Conn;
import conn.ConnHelper;
import org.omg.CORBA.ORB;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;

import java.util.Scanner;

public class ConnClient {
    //Define los comandos disponibles
    //!Comandos originales
    static final String CMD_PREFIX = "/";
    static final String CMD_CREATE = CMD_PREFIX + "create ";
    static final String CMD_LIST   = CMD_PREFIX + "list";
    static final String CMD_JOIN   = CMD_PREFIX + "join ";
    static final String CMD_LEAVE  = CMD_PREFIX + "leave";
    static final String CMD_HELP   = CMD_PREFIX + "help";
    static final String CMD_NAME   = CMD_PREFIX + "name ";
    static final String CMD_QUIT   = CMD_PREFIX + "quit";

    //? Comandos añadidos
    static final String CMD_LISTUSERS = CMD_PREFIX + "ChatUsers ";

    static Conn connImpl;
    static String token;

    //Utiliza la interfaz Runnable para trabajar con hilos.
    //Clase implementada mediante Runnable que lee los inputs del usuario.
    public static class Input implements Runnable {
        public void run() { //función que es llamada cuando la clase se usa en la declaración de un thread/hilo
            Scanner in = new Scanner(System.in); //Para que el usuario meta datos por pantalla utiliza Scanner del paquete utils de java
            while (true) { //Tecnica de polling, bucle infinito.
                String s = in.nextLine(); //Lee los datos metidos por pantalla.
                parse(s); //Lo pasa a la función "parse"
            }
        }

        void parse(String str) { //La función comprueba los comandos y llama a distintos metodos de la clase connImpl.
            if (str.startsWith(CMD_CREATE)) { // /create
                String name = str.substring(CMD_CREATE.length()); 
                connImpl.createChatRoom(token, name);  //Crea un nuevo chat
            } else if (str.startsWith(CMD_LIST)) { // /list
                connImpl.listChatRooms(token); //Devuelve los chats disponibles
            } else if (str.startsWith(CMD_JOIN)) { // /join
                String name = str.substring(CMD_JOIN.length());
                connImpl.joinChatRoom(token, name); //Te añade a un chat
            } else if (str.startsWith(CMD_LEAVE)) { // /leave
                connImpl.leaveChatRoom(token); //Te elimina de un chat
            } else if (str.startsWith(CMD_HELP)) { // /help
                help(); //Te muestra todos los comandos
            } else if (str.startsWith(CMD_NAME)) { // /name
                String name = str.substring(CMD_NAME.length());
                connImpl.changeName(token, name); //Cambia el nombre de usuario.
            } else if (str.startsWith(CMD_QUIT)) { // /quit
                System.exit(0); //Termina la ejecución del programa.
            } else if (str.startsWith(CMD_LISTUSERS)) { //? NEW COMMAND: Muestra todos los usuarios de un chat
                String name = str.substring(CMD_LISTUSERS.length());
                connImpl.listChatUsers(token, name);
            } else { //Si no es ningún comando
                connImpl.sendMessage(token, str); //Manda un nuevo mensaje.
            }
        }

        void help() { //Función que saca por pantalla los comandos que existen
            String str = "Commands:\n" +
                    CMD_CREATE + "\n" +
                    CMD_LIST + "\n" +
                    CMD_LISTUSERS + "\n" +
                    CMD_JOIN + "\n" +
                    CMD_LEAVE + "\n" +
                    CMD_HELP + "\n" +
                    CMD_NAME + "\n" +
                    CMD_QUIT;
            System.out.println(str);
        }
    }

    //Clase de output implementada mediante Runnable. 
    public static class Output implements Runnable {
        public void run() {
            while(true) {
                String message = connImpl.receiveMessage(token); //Recibe un mensaje del usuario (Lo saca de al cola de mensajes del usuario)
                if (!message.isEmpty()) { //Si el mensaje no es un string vacio.
                    System.out.println(message); //Saca por consola el mensaje
                } else { //Si el string está vacío.
                    try {
                        Thread.sleep(200); //El thread para la ejecución durante 200 milisegundos.
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public static void main(String args[])
    {
        try{
            // create and initialize the ORB
            ORB orb = ORB.init(args, null);

            // get the root naming context
            org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService");

            // Use NamingContextExt instead of NamingContext. This is
            // part of the Interoperable naming Service.
            NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);

            // resolve the Object Reference in Naming
            String name = "Conn";
            connImpl = ConnHelper.narrow(ncRef.resolve_str(name));

            System.out.println("Obtained a handle on server object: " + connImpl);
            token = connImpl.connect(); //Inicializa el token del usuario que devuelve el metodo connect.

            //Crea y ejecuta los Threads de entrada de mensaje y salida de mensajes.
            new Thread(new Input()).start();
            new Thread(new Output()).start();

        } catch (Exception e) {
            System.out.println("ERROR : " + e) ;
            e.printStackTrace(System.out);
        }
    }

}
