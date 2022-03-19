package server;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

//Clase que define los metodos de un chat.
public class ChatRoom {
    ReadWriteLock lock = new ReentrantReadWriteLock(); //Lock para la sincronización de procesos del thread.
    final String name; //Nombre del chat
    HashMap<String, User> users = new HashMap<String, User>(); //HashMap con los usuarios que pertenecen al chat.
    ArrayList<Message> messages = new ArrayList<Message>(); //ArrayList con los mensajes del chat.
    boolean expired = false; //Variable utilizada para saber cuando el tiempo de expiración ha terminado o cuando es eliminado el chat.
    Date expiry; //Fecha de expiración del chat.

    public ChatRoom(String name) { //Constructor de la clase. 
        this.name = name; //Inicializa el nombre
        updateExpiryTime(); //Inicializa la fecha de expiración.
    }
 
    public String getName() { //Devuelve el nombre de la sala.
        return name;
    }

    public void addMessage(User user, Message message) { //Añade un nuevo mensaje a la sala.
        lock.writeLock().lock(); //Bloquea el lock de escritura.
        if (!expired) { //Si todavía no ha expirado el chat.
            messages.add(message); //Añade un mensaje al ArrayList de mensajes.
            for (String s : users.keySet()) { //Añade el mensaje a todos los usuarios del chat mediante un for que recorre las keys del HashMap de usuarios.
                users.get(s).addMessage(message.toString());
            }
            updateExpiryTime(); //Actualiza la fecha de expiración.
        } else { //Si el chat ya ha expirado
            user.addMessage("Error: Chat room has been deleted."); //Se añade un mensaje de error.
        }
        lock.writeLock().unlock(); //Desbloquea el lock de escritura.
    }

    public ArrayList<Message> getMessages() { //Devuelve todos los mensajes del chat
        lock.readLock().lock(); //Bloquea el lock de lectura.
        ArrayList<Message> copy = new ArrayList<Message>(messages); //Crea una variable con todos los mensajes.
        lock.readLock().unlock(); //Desbloque el lock de lectura.
        return copy; //Devuelve el array de mensajes.
    }

    public void addUser(User user) { //Añade un nuevo usuario.
        lock.writeLock().lock(); //Bloque el lock de escritura.
        if (!expired) { //Si no ha expirado el chat
            users.put(user.getToken(), user); //Añade al HashMap de usuarios el usuario parado por parametro. La key es el token del usuario y el valor el objeto pasado por parametro.
            user.setChatRoom(this.name); //Añade a los chats del usuario el nombre del chat actual.
            for (Message message : messages) { //Añade todo los mensajes mandados antes de que entrara el usuario al chat.
                user.addMessage(message.toString());
            }
            updateExpiryTime(); //Actualiza la fecha de expiración.
        } else { //Si no 
            user.addMessage("Error: Chat room has been deleted."); //Manda un mensaje de error
        }
        lock.writeLock().unlock(); //Desbloquea el lock de escritura.
    }

    public void removeUser(User user) { //Elimina a un usuario del chat.
        lock.writeLock().lock(); //Bloquea el lock de escritura.
        users.remove(user.getToken()); //Elimina del HashMap el usuario con el token que se le pasa al metodo.
        user.setChatRoom(null); //Pone a null el chat en el que está el usuario.
        updateExpiryTime(); //Actualiza la fecha de expiración del chat
        lock.writeLock().unlock(); //Desbloquea el lock de escritura.
    }

    public void expire() { //Borra por completo el chat
        lock.writeLock().lock(); //Bloquea el lock de escritura.
        expired = true; //Pone la variable expired a true.
        for (String s : users.keySet()) { //Recorre todos los usuarios del HashMap
            User user = users.get(s); //Crea un objeto usuario.
            user.setChatRoom(null); //Pone a null el chat al que pertenece el usuario.
            user.addMessage("Notice: Your current chat room has been deleted"); //Manda un mensaje avisando de que el chat ha cerrado
        }
        lock.writeLock().unlock(); //Desbloquea el lock de escritura.
    }

    public Date getExpiryTime() { //Devuelve el tiempo de expiración del chat.
        lock.readLock().lock();
        Date date = expiry;
        lock.readLock().unlock();
        return date;
    }

    public void updateExpiryTime() { //Actualiza el tiempo de expiración del chay-
        lock.writeLock().lock();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.DAY_OF_YEAR, 7);
        expiry = calendar.getTime();
        lock.writeLock().unlock();
    }

    public ArrayList<String> getUserNames() {
        lock.readLock().lock();
        ArrayList<String> allUserNames = new ArrayList<String>();
        for(User user : users.values()){
            allUserNames.add(user.name);
        }
        lock.readLock().unlock();
        return allUserNames;
    }
}
