package server;

import conn.Conn;
import conn.ConnHelper;
import conn.ConnPOA;
import org.omg.CORBA.ORB;
import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;

import java.util.ArrayList;

//Crea la clase ConnImpl que es una implementaión de la interfaz Conn definida en Conn.idl.
class ConnImpl extends ConnPOA {
    private ORB orb; //Declaramos una variable de tipo ORB. ORB es una clase que nos permite trabajar con las APIs de CORBA
    Server server = new Server(); //Declaramos un nuevo Server

    public void setORB(ORB orb_val) {  //Setter del ORB
        orb = orb_val;
    }

    public String connect() { //Metodo connect que añade al servidor un usuario y devuelve su token.
        return server.addUser();
    }

    public void sendMessage(String token, String message) { //Manda un nuevo mensaje.
        User user = server.getUser(token); //Inicializamos un usuario utilizando el token que le pasamos por parametro.
        if (user != null) { //Si el usuario existe.
            String chatName = user.getChatRoom(); //Guarda el nombre del chat al que pertenece.
            ChatRoom chatRoom = server.getChatRoom(chatName); //Inicializa una variable de tipo ChatRoom con el nombre del chat al que pertenece el usuario.
            if (chatRoom != null) { //Si existe el chat
                Message chatMessage = new Message(user.getName(), message); //Crea un nuevo mensaje
                chatRoom.addMessage(user, chatMessage); //Añade el mensaje al chat.
            } else { //Si no existe el chat manda un mensaje de error al usuario.
                user.addMessage("Error: You cannot send a message within the lobby.");
            }
        } else { //Si no existe un usuario con ese token, devuelve por consola un error.
            System.out.println("Invalid token given for sending message.");
        }
    }

    public String receiveMessage(String token) { //Recibe un mensaje (lo devuelve)
        User user = server.getUser(token); //Busca a el usuario por token.
        if (user != null) { //Si el usuario existe
            String message = user.getMessage(); //Saca el primer mensaje que haya en la cola de mensajes del usuario.
            if (message != null) { //Si hay un mensaje en la cola de mensajes del usuario.
                return message; //Devuelve el mensaje.
            } else { //Si no
                return ""; //Devuelve un string vacio.
            }
        } else { //Si no existe el usuario.
            System.out.println("Invalid token given for receiving message."); //Manda un mensaje de error por consola
            return ""; //Devuelve un string vacio.
        }
    }

    public void createChatRoom(String token, String name) { //Crea un nuevo chat.
        User user = server.getUser(token); //Busca al usuario que tenga el token que le llega por parametro a la función.
        if (user != null) { //Si el usuario existe
            server.addChatRoom(name); //Añade un nuevo chat con el nombre que le llega por parametro a la función.
            user.addMessage("Notice: Created " + name + "."); //Manda un mensaje al usuario confirmando la creación del chat.
        } else {
            System.out.println("Invalid token given for creating chat room."); //Si no existe el usuario saca un error por consola.
        }
    }

    public void listChatRooms(String token) { //Saca por pantalla todos los chats disponibles.
        User user = server.getUser(token); //Busca al usuario por token
        if (user != null) { //Si existe
            ArrayList<String> chatNames = server.getChatRooms(); //Declara e inicializa un ArrayLisy con todos los chats del servidor.
            String str = "Chat Rooms:\n"; //Declara e inicializa un string donde se van a almacenar todos los nombres de los chats del servidor.
            for (String chatName : chatNames) { //Con un for recorre todos los nombres almacenados en el ArrayList
                str += chatName + "\n"; //Concatena el nombre del chat con un salto de linea con el string que se imprime por pantalla.
            }
            user.addMessage(str.substring(0, str.length()-1)); //Añade un mensaje al usuario con todos los chats disponibles tras el for.
        } else {
            System.out.println("Invalid token given for listing chat rooms."); //Si el usuario no existe saca un error por consola.
        }
    }

    public void joinChatRoom(String token, String name) { //Añade un usuario a una sala que especifique por nombre.
        User user = server.getUser(token); //Busca al usuario por token
        if (user != null) { //Si existe
            ChatRoom chatRoom = server.getChatRoom(name); //Busca si existe un chat con ese nombre
            if (chatRoom != null) { //Si existe
                user.addMessage("Notice: Joined " + name +  "."); //Manda un mensaje al usuario diciendo que se ha unido correctamente al chat
                chatRoom.addUser(user); //Añade al chat el usuario.
            } else { //Si no existe el chat
                user.addMessage("Error: The specified channel does not exist."); //Manda un error al usuario.
            }
        } else { //Si no existe el usuario saca un error por consola.
            System.out.println("Invalid token given for joining chat room.");
        }
    }

    public void leaveChatRoom(String token) { //Saca a un usuario de un chat.
        User user = server.getUser(token); //Busca el usuario por token.
        if (user != null) { //Si existe
            String chatName = user.getChatRoom(); //Busca el nombre de la sala mediante el usuario.
            ChatRoom chatRoom = server.getChatRoom(chatName); //Busca la sala mediante nombre.
            chatRoom.removeUser(user); //Saca al usuario del chat
            user.addMessage("Notice: Left " + chatName +  "."); //Manda un mensaje al usuario diciendo que ha salido de la sala.
        } else {
            System.out.println("Invalid token given for leaving chat room.");
        }
    }

    public void changeName(String token, String name) { //cambia el nombre de usario.
        User user = server.getUser(token); //Busca al usuario por token.
        if (user != null) { //Si existe.
            user.setName(name); //Cambia el nombre del usuario.
            user.addMessage("Notice: Changed name to " + name +  "."); //Manda un mensaje al usuario confirmando el cambio de nombre
        } else { 
            System.out.println("Invalid token given for changing name."); //Si no existe el usuario saca un error por consola.
        }
    }

    //? NUEVAS FUNCIONES
    public void listChatUsers(String token, String name) {
        User user = server.getUser(token);
        if (user != null) { 
            ChatRoom chatRoom = server.getChatRoom(name);
            if(chatRoom != null){
                int anonCounter = 0;
                String str = "Users in chat room:\n";
                for (String userToken : chatRoom.users.keySet()) {
                    User userInChat = server.getUser(userToken);
                    if(userInChat.name != "Anonymous"){
                        str += userInChat.name + "\n";
                    }else{
                        anonCounter++;
                    }
                }
                str += "Anonymous: " + anonCounter; 
                user.addMessage(str.substring(0, str.length()-1));
            }else{ 
                System.out.println("Invalid Name: Chat Room not found.");
            }
        } else {
            System.out.println("Invalid token given for listing chat rooms.");
        }
    }
}

/**
 * Created by joshheinrichs on 15-11-24.
 */
public class ConnServer { //Main del servidor, explicado en el ejercicio1. 
    public static void main(String args[]) {
        try{
            // create and initialize the ORB
            ORB orb = ORB.init(args, null);

            // get reference to rootpoa & activate the POAManager
            POA rootpoa = POAHelper.narrow(orb.resolve_initial_references("RootPOA"));
            rootpoa.the_POAManager().activate();

            // create servant and register it with the ORB
            ConnImpl connImpl = new ConnImpl();
            connImpl.setORB(orb);

            // get object reference from the servant
            org.omg.CORBA.Object ref = rootpoa.servant_to_reference(connImpl);
            Conn href = ConnHelper.narrow(ref);

            // get the root naming context
            // NameService invokes the name service
            org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService");
            // Use NamingContextExt which is part of the Interoperable
            // Naming Service (INS) specification.
            NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);

            // bind the Object Reference in Naming
            String name = "Conn";
            NameComponent path[] = ncRef.to_name( name );
            ncRef.rebind(path, href);

            System.out.println("server.ConnServer ready and waiting ...");

            // wait for invocations from clients
            orb.run();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("server.ConnServer Exiting ...");
    }
}
