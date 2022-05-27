package server;

import message.Message;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClientInfo {

    int id;
    public String name = "default";
    Socket socket;
    ObjectOutputStream sOutput;
    ObjectInputStream sInput;
    Listen listenThread;

    public ClientInfo(Socket incSocket, int id) {
        this.socket = incSocket;
        this.id = id;
        try {
            this.sOutput = new ObjectOutputStream(this.socket.getOutputStream());
            this.sInput = new ObjectInputStream(this.socket.getInputStream());
        } catch (IOException ex) {
            Logger.getLogger(ClientInfo.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.listenThread = new Listen(this);
    }

    public void Send(Message message) {
        try {
            this.sOutput.writeObject(message);
        } catch (IOException ex) {
            Logger.getLogger(ClientInfo.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    class Listen extends Thread {

        ClientInfo TheClient;

        Listen(ClientInfo TheClient) {
            this.TheClient = TheClient;
        }

        public void run() {
            while (TheClient.socket.isConnected()) {
                try {
                    Message received = (Message) (TheClient.sInput.readObject());
                    switch (received.type) {
                        case Name:
                            String uname = received.content.toString();
                            uname = Server.checkName(TheClient, uname);
                            TheClient.name = uname;
                            System.out.println(TheClient.name + "is connected.");
                            Server.sendUpdateUList();
                            Server.SendAllRooms();
                            break;
                        case Disconnect:
                            Server.Clients.remove(TheClient);
                            Server.sendUpdateUList();
                            TheClient.listenThread.stop();
                            TheClient.socket.close();
                            break;
                        case Text:
                            Server.sendGlobal(received);
                            break;
                        case CreateNewRoom:
                            Server.addNewRoom(received);
                            break;
                        case RequestJoinRoom:
                            Server.ControlRoomJoin(received);
                            break;
                        case RoomMSG:
                            Server.SendRoomMSG(received);
                            break;
                        case RoomULeft:
                            Server.SendUserLeftRoom(received);
                            break;
                        case File:
                            Server.SendReceivedFile(received);
                            break;
                        case Fin:
                            break;

                    }

                } catch (EOFException ex) {
                    System.out.println("Error");
                } catch (IOException ex) {
                    Logger.getLogger(ClientInfo.class.getName()).log(Level.SEVERE, null, ex);
                    Server.Clients.remove(TheClient);

                } catch (ClassNotFoundException ex) {
                    Logger.getLogger(ClientInfo.class.getName()).log(Level.SEVERE, null, ex);
                    Server.Clients.remove(TheClient);
                }
            }

        }
    }

}
