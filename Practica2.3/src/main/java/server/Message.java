package server;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

//Clase que define los mensajes.
public class Message {
    Date date; //Fecha del mensaje
    String username; //Usuario que lo mandó.
    String text; //El texto del mensaje.

    public Message(String username, String text) { //Constructor que inicializa un nuevo mensaje.
        this.date = new Date();
        this.username = username;
        this.text = text;
    }

    public String toString() { //Devuelve el mensaje en formato fecha - nombre de usuario: Texto.
        DateFormat dateFormat = new SimpleDateFormat("HH:mm");
        return dateFormat.format(date) + " - " + username + ": " + text;
    }
}
