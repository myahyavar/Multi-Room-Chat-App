package server;

import message.Message;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListModel;

//main server thread
class ServerThread extends Thread {

    public void run() {
        //listen till server terminates
        while (!Server.serverSocket.isClosed()) {
            try {
                Socket clientSocket = Server.serverSocket.accept();
                Server.ConsoleSend("Client Connected");
                ClientInfo nclient = new ClientInfo(clientSocket, Server.IdClient);

                Server.IdClient++;
                Server.Clients.add(nclient);
                nclient.listenThread.start();

            } catch (IOException ex) {
                Logger.getLogger(ServerThread.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}

public class Server {

    public static ServerSocket serverSocket;
    public static int IdClient = 0;
    public static int port = 0;
    public static ServerThread runThread;
    public static ArrayList<ClientInfo> Clients = new ArrayList<>();
    public static ArrayList<Room> Rooms = new ArrayList<>();

    public static void Start(int port) {
        try {
            Server.port = port;
            Server.serverSocket = new ServerSocket(Server.port);
            Server.runThread = new ServerThread();
            Server.runThread.start();
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void ConsoleSend(String msg) {

        System.out.println(msg);

    }

    // message for updating the user list
    public static void sendUpdateUList() {
        DefaultListModel model = new DefaultListModel();
        for (ClientInfo c : Clients) {
            model.addElement(c.name);
        }
        Message msg = new Message(Message.Message_Type.UpdateUList);
        msg.content = model;
        for (ClientInfo c : Clients) {
            Server.Send(c, msg);
        }

    }

    // for preventing the multiple same name occurences
    public static String checkName(ClientInfo c, String name) {
        boolean sendRename = false;
        for (int i = 0; i < Clients.size(); i++) {
            String cname = Clients.get(i).name;
            if (cname.toLowerCase().equals(name.toLowerCase())) {
                name += "(1)";
                i = 0;
                sendRename = true;
            }
        }

        if (sendRename) {
            Message msg2 = new Message(Message.Message_Type.Rename);
            msg2.content = name;
            Server.Send(c, msg2);
        }
        return name;
    }

    // send text to all connected
    public static void sendGlobal(Message msg) {
        for (ClientInfo c : Clients) {
            Server.Send(c, msg);
        }
    }

    public static void addNewRoom(Message msg) {
        String roomname = ((ArrayList<String>) msg.content).get(0);
        String firstUser = ((ArrayList<String>) msg.content).get(1);

        boolean containControl = false;
        for (Room c : Rooms) {
            if (c.name.equals(roomname)) {
                containControl = true;
                break;
            }
        }

        if (!containControl) {
            Room r = new Room(roomname, firstUser);

            Rooms.add(r);
            SendAllRooms();
            SendCompleteMsg(firstUser, r);

        } else {
            Message newmsg = new Message(Message.Message_Type.RoomNameTaken);
            String text = "Room name taken";
            newmsg.content = text;

            for (ClientInfo c : Clients) {
                if (c.name.equals(firstUser)) {
                    Server.Send(c, newmsg);
                    break;
                }
            }

        }
    }

    // after creating room
    public static void SendCompleteMsg(String name, Room r) {
        Message newmsg = new Message(Message.Message_Type.CompleteCreation);
        HashMap<String, ArrayList<String>> cnt = new HashMap<String, ArrayList<String>>();

        cnt.put(r.name, r.users);
        newmsg.content = cnt;
        for (ClientInfo c : Clients) {
            if (c.name.equals(name)) {
                Server.Send(c, newmsg);
                break;
            }

        }
    }

    public static void SendAllRooms() {
        ArrayList<String> allroomnames = new ArrayList<>();

        for (Room c : Rooms) {
            allroomnames.add(c.name);
        }

        Message newmsg = new Message(Message.Message_Type.SendAllRooms);
        newmsg.content = allroomnames;
        for (ClientInfo c : Clients) {
            Server.Send(c, newmsg);
        }
    }

    public static void ControlRoomJoin(Message msg) {
        ArrayList<String> info = (ArrayList<String>) msg.content;
        String room = info.get(0);
        String user = info.get(1);

        int roomIndex = findRoom(room);

        Server.SendAcceptRoomJoin(user, Rooms.get(roomIndex).name);
        Server.SendLastUserListToJoined(user, Rooms.get(roomIndex).name, Rooms.get(roomIndex).users);
        Rooms.get(roomIndex).users.add(user);
        Server.SendUpdateRoomFolk(Rooms.get(roomIndex), user);

    }

    public static int findRoom(String roomName) {
        int index = -1;

        for (int i = 0; i < Rooms.size(); i++) {
            if (Rooms.get(i).name.equals(roomName)) {
                index = i;
                break;
            }
        }

        return index;
    }

    public static void SendAcceptRoomJoin(String user, String roomname) {

        for (ClientInfo c : Clients) {
            if (c.name.equals(user)) {
                Message newmsg = new Message(Message.Message_Type.RoomRequestAccepted);
                ArrayList<String> items = new ArrayList<String>();
                items.add(user);
                items.add(roomname);
                newmsg.content = items;
                Server.Send(c, newmsg);
                break;
            }
        }

    }

    public static void SendUpdateRoomFolk(Room cr, String newUser) {
        for (ClientInfo c : Clients) {
            if (cr.users.contains(c.name)) {
                Message newmsg = new Message(Message.Message_Type.UpdateChatRoomUList);
                ArrayList<String> elements = new ArrayList<>();
                elements.add(cr.name);
                elements.add(newUser);
                newmsg.content = elements;
                Server.Send(c, newmsg);
            }
        }
    }

    public static void SendRoomMSG(Message msg) {
        ArrayList<String> elements = (ArrayList<String>) msg.content;
        String room = elements.get(0);
        String text = elements.get(1);

        for (Room cr : Rooms) {
            if (cr.name.equals(room)) {

                for (ClientInfo c : Clients) {

                    if (cr.users.contains(c.name)) {
                        Message newmsg = new Message(Message.Message_Type.RoomMSG);
                        newmsg.content = elements;
                        Server.Send(c, newmsg);
                    }
                }

                break;
            }

        }

    }

    public static void SendLastUserListToJoined(String user, String roomname, ArrayList<String> userList) {
        for (ClientInfo c : Clients) {
            if (c.name.equals(user)) {
                Message newmsg = new Message(Message.Message_Type.GetOldRoomUsers);
                ArrayList elements = new ArrayList();
                elements.add(roomname);
                ArrayList<String> userListCopy = new ArrayList<String>();
                for (String u : userList) {
                    userListCopy.add(u);
                }
                elements.add(userListCopy);
                newmsg.content = elements;
                Server.Send(c, newmsg);
                break;
            }
        }
    }

    public static void SendUserLeftRoom(Message msg) {
        String uname = ((ArrayList<String>) msg.content).get(0);
        String room = ((ArrayList<String>) msg.content).get(1);
        int index = findRoom(room);
        Rooms.get(index).users.remove(uname);
        ArrayList<String> arr = Rooms.get(index).users;
        ArrayList<String> arr2 = new ArrayList<String>();
        for (String el : arr) {
            arr2.add(el);
        }
        Message newmsg = new Message(Message.Message_Type.RoomULeft);
        ArrayList elements = new ArrayList<>();
        elements.add(room);
        elements.add(arr2);
        newmsg.content = elements;
        for (ClientInfo c : Clients) {
            if (Rooms.get(index).users.contains(c.name)) {
                Server.Send(c, newmsg);
            }
        }

        for (ClientInfo c : Clients) {
            if (c.name.equals(uname)) {
                Message newmsg2 = new Message(Message.Message_Type.RemoveFromMyRoomList);
                newmsg2.content = room;
                Server.Send(c, newmsg2);
            }
        }

    }

    public static void SendReceivedFile(Message msg) {
        ArrayList elements = (ArrayList) msg.content;
        int roomindex = findRoom(elements.get(0).toString());
        Message newmsg2 = new Message(Message.Message_Type.File);
        newmsg2.content = elements;
        for (ClientInfo clt : Clients) {
            if (Rooms.get(roomindex).users.contains(clt.name)) {
                Server.Send(clt, newmsg2);
            }
        }

    }

    public static void Send(ClientInfo cl, Message msg) {

        try {
            cl.sOutput.writeObject(msg);
        } catch (IOException ex) {
            Logger.getLogger(ClientInfo.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}
