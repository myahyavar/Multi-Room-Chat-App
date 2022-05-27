package message;

public class Message implements java.io.Serializable {

    // message types used in threads
    public static enum Message_Type {
        Name,
        Start,
        Fin,
        Disconnect,
        Text,
        UpdateUList,
        Rename,
        CreateNewRoom,
        NewRoom,
        SendAllRooms,
        RoomNameTaken,
        CompleteCreation,
        RequestJoinRoom,
        RoomRequestAccepted,
        UpdateChatRoomUList,
        RoomMSG,
        GetOldRoomUsers,
        RoomULeft,
        RemoveFromMyRoomList,
        File
    }

    public Message_Type type;
    public Object content;

    public Message(Message_Type t) {
        this.type = t;
    }

}
