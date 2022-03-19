package server;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

//Define un usuario.
public class User {
    ReadWriteLock lock = new ReentrantReadWriteLock(); //Lock para la sincronización de procesos del thread.
    final String token; //Token del usuario.
    String name; //Nombre del usuario.
    String chatRoom; //Sala a la que pertenece
    Queue<String> messages = new LinkedList<String>(); //Lista enlazada simple que hace de cola de mensajes.

    public User(String token, String name) { //Contructor que inicializa el usuario.
        this.token = token;
        this.name = name;
    }

    public String getToken() { //Devuelve el token del usuario.
        return token;
    }

    public String getName() { //Devuelve el nombre del usuario.
        lock.readLock().lock();
        String name = this.name;
        lock.readLock().unlock();
        return name;
    }

    public void setName(String name) { //Cambia el nombre del usuario.
        lock.writeLock().lock();
        this.name = name;
        lock.writeLock().unlock();
    }

    public String getChatRoom() { //Deuvelve la sala al a que pertenece el usuario.
        lock.readLock().lock();
        String chatRoom = this.chatRoom;
        lock.readLock().unlock();
        return chatRoom;
    }

    public void setChatRoom(String chatRoom) { //Cambia la sala a la que pertenece el usuario.
        lock.writeLock().lock();
        this.chatRoom = chatRoom;
        lock.writeLock().unlock();
    }

    public void addMessage(String message) { //Añade un nuevo mensaje a la cola.
        lock.writeLock().lock();
        messages.add(message);
        lock.writeLock().unlock();
    }

    public String getMessage() { //Devuelve un mensaje, elimina el mensaje que esté el primero en la cola.
        lock.writeLock().lock();
        String message = messages.poll();
        lock.writeLock().lock();
        return message;
    }
}
