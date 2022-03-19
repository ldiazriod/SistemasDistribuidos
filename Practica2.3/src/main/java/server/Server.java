package server;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

//Clase que define el comportamiento del servidor.
public class Server {
    static final String DEFAULT_NAME = "Anonymous"; //Nombre del usuario anonimo por defecto.

    ReadWriteLock lock = new ReentrantReadWriteLock(); //Lock para la sincronización de procesos del thread.
    HashMap<String, User> users = new HashMap<String, User>(); //HashMap donde se guardan los usuarios
    HashMap<String, ChatRoom> chatRooms = new HashMap<String, ChatRoom>(); //HashMap donde se guardan las salas de chat
    SecureRandom random = new SecureRandom(); //Genera un número aleatorio encriptado.

    public void addChatRoom(final String name) { //Crea una sala.
        lock.writeLock().lock(); //Bloquea el lock de escritura.
        ChatRoom chatRoom = new ChatRoom(name); //Crea la nueva sala
        chatRooms.put(chatRoom.getName(), chatRoom); //Añade la sala al HashMap
        lock.writeLock().unlock(); //Desbloquea el lock de escritura.

        //Crea un nuevo thread. El target lo crea directamente.
        new Thread(new Runnable() {
            //Target
            public void run() { //función que es llamada cuando la clase se usa en la declaración de un thread/hilo
                ChatRoom chatRoom = getChatRoom(name); //Declara un nuevo ChatRoom
                while(true) { //Polling
                    Date expiryTime = chatRoom.getExpiryTime(); //Tiempo que queda hasta que el chat se borre solo
                    Date currentTime = new Date(); //Tiempo actual para comparar
                    if (currentTime.after(expiryTime)) { //Si la hora/fecha actual es mayor que el valor definido en la sala para que sea borrado.
                        removeChatRoom(name); //Elimina del HashMap la sala
                        break; //Sale del bucle infinito.
                    } else { //Si no
                        //Calcula el tiempo que queda hasta que haya que borrar el chat y para la ejecución del thread temporalmente (Hasta qye haya que borrar el chat)
                        System.out.println("Checking for deletion");
                        long waitTime = expiryTime.getTime() - currentTime.getTime();
                        try {
                            Thread.sleep(waitTime);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }).start(); //Ejecuta el thread.
    }

    public ChatRoom getChatRoom(String name) { //Busca y devuelve un chat por nombre.
        lock.readLock().lock(); //Bloquea el lock de lectura.
        ChatRoom chatRoom = chatRooms.get(name); //Inicializa un nuevo ChatRoom usando el metodo .get de la clase HashMap y busca la key que sea igual al nombre que le llega como parametro.
        lock.readLock().unlock(); //Desbloquea el lock de lectura.
        return chatRoom; //Devuelve el ChatRoom.
    }

    public void removeChatRoom(String name) { //Elimina una chat por nombre.
        lock.writeLock().lock(); //Bloquea el lock de lectura
        ChatRoom chatRoom = chatRooms.remove(name); //Elimina el chat del HashMap que tenga el nombre que le pasa por parametro.
        chatRoom.expire(); //Llama al metodo expire de la clase chatRoom que elimina el chat del usuario y manda un mensaje avisando de que el chat ha sido eliminado.
        lock.writeLock().unlock(); //Desbloquea el lock de escritura.
    }

    public ArrayList<String> getChatRooms() { //Devuelve todos los chats disponibles.
        lock.readLock().lock();  //Bloquea el lock de lectura.
        ArrayList<String> names = new ArrayList<String>(chatRooms.keySet()); //Crea un nuevo ArrayList con las keys del HashMap de chats.
        lock.readLock().unlock(); //Desbloqeua el lock de lectura.
        return names; //Devuelve el ArrayList con todos los chats.
    }

    public String addUser() { //Añade un usuario y devuelve su token.
        lock.writeLock().lock(); //Bloqeua el lock de escritura.
        String token = randomString(); //LLama a la función randomString (está más abajo) para utilizarlo como toquen.
        User user = new User(token, DEFAULT_NAME); //Crea un nuevo usuario con el Token creado y el nombre por defecto (Anonymous)
        users.put(user.getToken(), user); //Añade al HashMap el usuario, utilizando el token como key y el objeto como value 
        lock.writeLock().unlock(); //Desbloquea el lock de escritura.
        return token; //Devuelve el token del usuario.
    }

    public User getUser(String token) { //Busca y devuelve un usuario por token.
        lock.readLock().lock(); //Bloquea el lock de lectura.
        User user = users.get(token); //Declara un nuevo usuario e inicializa con el usuario que tenga como key el token añadido por parametro.
        lock.readLock().unlock(); //Desbloquea el lock de lectura.
        return user; //Devuelve al usuario.
    }

    String randomString() { //Crea un string aleatorio.
        //Genera un entero con una distrubición de 0 a 260-1 con una uniformidad de la destribución aleatoria y lo convierte en un string. 
        return new BigInteger(130, random).toString(32);
    }
}
