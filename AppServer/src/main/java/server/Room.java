package server;

import java.util.ArrayList;
import javax.swing.DefaultListModel;

public class Room {

    String name;
    ArrayList<String> users = new ArrayList<>();
    DefaultListModel<String> messagesListModel = new DefaultListModel<>();

    public Room(String name, String firstUser) {
        this.name = name;
        users.add(firstUser);
    }
}
