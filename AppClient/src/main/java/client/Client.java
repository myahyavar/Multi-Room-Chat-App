package client;

import message.Message;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import message.MainRoom;

public class Client {

    public static Socket socket;
    public static ObjectInputStream sInput;
    public static ObjectOutputStream sOutput;
    public static Listen listener;

    // starts connection with server
    public static void Start(String ip, int port) {
        try {
            Client.socket = new Socket(ip, port);
            Client.ConsoleSend("Connected to the Server");

            Client.sInput = new ObjectInputStream(Client.socket.getInputStream());
            Client.sOutput = new ObjectOutputStream(Client.socket.getOutputStream());
            Client.listener = new Listen();
            Client.listener.start();

            Message msg = new Message(Message.Message_Type.Name);
            msg.content = MainRoom.MRoom.Username.getText();
            Client.Send(msg);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null, "Socket Error.", "Error", JOptionPane.ERROR_MESSAGE);
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    // terminates connection with server
    public static void Terminate() {
        try {
            if (Client.socket != null) {
                Client.listener.stop();
                Client.socket.close();
                Client.sOutput.flush();
                Client.sOutput.close();
                Client.sInput.close();
            }
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    // sends thread message
    public static void Send(Message msg) {
        try {
            Client.sOutput.writeObject(msg);
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    //extra checks on console
    public static void ConsoleSend(String msg) {

        System.out.println(msg);

    }

}
