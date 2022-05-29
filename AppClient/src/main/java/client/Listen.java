
package client;

import static client.Client.sInput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListModel;
import message.MainRoom;
import message.Message;

// main listen thread, app behaves according to received message
class Listen extends Thread {
       public void run() {
        while (Client.socket.isConnected()) {
            try {
                Message received = (Message) (sInput.readObject());
                
                switch (received.type) {
                    case Name:
                        break;
                    case Disconnect:
                        break;
                    case Text:
                        MainRoom.MRoom.txtGeneral.setText(MainRoom.MRoom.txtGeneral.getText() + "\n" + received.content.toString());
                        break;
                    case UpdateUList:
                        MainRoom.MRoom.updateUList((DefaultListModel) received.content);
                        break;
                    case SendAllRooms:                        
                        MainRoom.MRoom.GetAllRooms((ArrayList<String>)received.content);                       
                        break;
                    case RoomNameTaken:
                        MainRoom.MRoom.cr.lbl_Hata.setText((String)received.content);
                        MainRoom.MRoom.cr.lbl_Hata.setVisible(true);
                        break;
                    case CompleteCreation:
                        MainRoom.MRoom.cr.dispose();
                        MainRoom.MRoom.CreateAndEnterRoom(received);
                        break;
                    case UpdateChatRoomUList:
                        MainRoom.MRoom.RoomUListAdd((ArrayList<String>)received.content);
                        break;
                    case RoomMSG:
                        MainRoom.MRoom.NewMsg(received);
                        break;
                    case GetOldRoomUsers:
                        MainRoom.MRoom.OldUsers(received);
                        break;
                    case RoomULeft:
                        MainRoom.MRoom.ChatRoomUserLeft(received);
                        break;
                    case RemoveFromMyRoomList:
                        MainRoom.MRoom.delRoom(received);
                        break;
                    case Rename:
                        MainRoom.MRoom.Username.setText((String) received.content);
                        break;
                    case File:
                        MainRoom.MRoom.getFile(received);
                        break;
                    case Fin:
                        break;

                }

                // if somwthing goes wrong stop the client
            } catch (IOException ex) {

                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
                Client.Terminate();
                break;
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
                Client.Terminate();
                break;
            }
        }

    }
}
