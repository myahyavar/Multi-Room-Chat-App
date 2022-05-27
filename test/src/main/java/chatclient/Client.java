/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatclient;

import chatmsg.Message;
import chatmsg.PrivateMsg;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import static chatclient.Client.sInput;
import chatmsg.Login;
import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.DefaultListModel;

/**
 *
 * 
 */
// serverdan gelecek mesajları dinleyen thread
class Listen extends Thread {

    public void run() {
        //soket bağlı olduğu sürece dön
        while (Client.socket.isConnected()) {
            try {
                //mesaj gelmesini dinyelen komut
                Message received = (Message) (sInput.readObject());
                
                switch (received.type) {
                    case Name:
                        break;
                    case Disconnect:
                        break;
                    case Text:
                        // Global mesajdır. Gelen metinle chati günceller
                        Login.ThisGame.txt_global_chat.setText(Login.ThisGame.txt_global_chat.getText() + "\n" + received.content.toString());
                        break;
                    case UpdateUserList:
                        // Gelen dlm ile mevcut user listesini günceller
                        Login.ThisGame.updateMyUserList((DefaultListModel) received.content);
                        break;
                    case PrivateMsg:
                        Login.ThisGame. privateMsgReceived((PrivateMsg)received.content);
                        break;
                    case SendAllRooms:                        
                        Login.ThisGame.GetAllRooms((ArrayList<String>)received.content);                       
                        break;
                    case RoomNameExist:
                        //Yeni room açmaya çalışılan sırada, eger o isimde bir oda daha varsa
                        Login.ThisGame.cr.lbl_Hata.setText((String)received.content);
                        Login.ThisGame.cr.lbl_Hata.setVisible(true);
                        break;
                    case CompleteCreation:
                        //Serverden gelen: "sorunsuz bir şekilde odayı oluştur" mesajı
                        Login.ThisGame.cr.dispose();
                        Login.ThisGame.CompleteCreateAndEnter(received);
                        break;
                    case PasswordRejected:
                        Login.ThisGame.enterPass.lbl_wrongpass.setVisible(true);
                        break;
                    case PasswordAccepted:
                        // Dogru girilen mesaj sonrasi, odaya giris
                        Login.ThisGame.EnterRoom(received);
                        Login.ThisGame.enterPass.dispose();
                        break;
                    case UpdateChatRoomUserList:
                        //Login.ThisGame.ChatRoomUserListAddNew((HashMap<String,ArrayList<String>>)received.content);
                        Login.ThisGame.ChatRoomUserListAddNew((ArrayList<String>)received.content);
                        break;
                    case RoomMSG:
                        Login.ThisGame.ChatRoomNewMsg(received);
                        break;
                    case GetOldRoomUsers:
                        Login.ThisGame.ChatSetRoomOldUsers(received);
                        break;
                    case RoomUserLeft:
                        Login.ThisGame.ChatRoomParticipantLeft(received);
                        break;
                    case RemoveFromMyRoomList:
                        Login.ThisGame.DellFromMyChatRooms(received);
                        break;
                    case Rename:
                        // Girdiği isim bulunduysa, kendisine yeni ismi gönderilir, o da username kısmını günceller
                        Login.ThisGame.txt_myusername.setText((String) received.content);
                        break;
                    case FileTransfer:
                        Login.ThisGame.getReceivedFile(received);
                        break;
                    case Bitis:
                        break;

                }

            } catch (IOException ex) {

                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
                Client.Stop();
                break;
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
                Client.Stop();
                break;
            }
        }

    }
}

public class Client {

    //her clientın bir soketi olmalı
    public static Socket socket;

    
    public static ObjectInputStream sInput;
   
    public static ObjectOutputStream sOutput;
    //serverı dinleme thredi 
    public static Listen listenMe;

    public static void Start(String ip, int port) {
        try {
            // Client Soket nesnesi
            Client.socket = new Socket(ip, port);
            Client.Display("Servera bağlandı");
            
            Client.sInput = new ObjectInputStream(Client.socket.getInputStream());
          
            Client.sOutput = new ObjectOutputStream(Client.socket.getOutputStream());
            Client.listenMe = new Listen();
            Client.listenMe.start();

            //ilk mesaj
            Message msg = new Message(Message.Message_Type.Name);
            msg.content = Login.ThisGame.txt_myusername.getText();
            Client.Send(msg);
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    //client durdurma fonksiyonu
    public static void Stop() {
        try {
            if (Client.socket != null) {
                Client.listenMe.stop();
                Client.socket.close();
                Client.sOutput.flush();
                Client.sOutput.close();

                Client.sInput.close();
            }
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public static void Display(String msg) {

        System.out.println(msg);

    }

    //mesaj gönderme fonksiyonu
    public static void Send(Message msg) {
        try {
            Client.sOutput.writeObject(msg);
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}
