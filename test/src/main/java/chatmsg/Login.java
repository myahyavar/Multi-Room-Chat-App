/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatmsg;

import java.awt.Image;

import java.io.IOException;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import chatclient.Client;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.BevelBorder;


public class Login extends javax.swing.JFrame {

   
    public static Login ThisGame;
    
    //Yeni oda olusturma ekrani
    public CreateRoom cr;
    
    //Odaya girisi icin sifre girme paneli
    public PassInputScreen enterPass;
    
    //Kullanicinin dahil oldugu odalar
    public static ArrayList<Room> myChatRooms;
   
    public Thread tmr_slider;
   
    
   //Ozel mesaj atmak icin listeden anlik kisi
    public String selectedUserForPrvChat = "";
    
    // KullaniciAdi-Mesaj  ikilisi şeklinde, bir mapte, mesajlasilan kullanicilarla olan sohbet saklaniyor
    HashMap<String, String> myPrivateChats = new HashMap<String, String>();
    Random rand;

    //
    /**
     * Creates new form Game
     */
    @SuppressWarnings("empty-statement")
    public Login() {
        initComponents();
        ThisGame = this;
        rand = new Random();
        lbltest.setVisible(false);
        lbltest2.setVisible(false);
        myChatRooms = new ArrayList<>();
        roomListChoice.add("Test");
        
        tmr_slider = new Thread(() -> {
            //soket bağlıysa dönsün
            while (Client.socket.isConnected()) {
                try {
                    
                    Thread.sleep(100);
               
                    
                } catch (InterruptedException ex) {
                    Logger.getLogger(Login.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });

    }

    public void updateMyUserList(DefaultListModel dlm) {
        // Aktif kullanıcıların listelenmesi   
        String selected = list_all_users.getSelectedValue();
        list_all_users.setModel(dlm);
        boolean isChanged = true;
        if (selected != null) {
            for (int i = 0; i < dlm.size(); i++) {
                String uname = (String) dlm.get(i);

                if (selected.equals(uname)) {
                    list_all_users.setSelectedIndex(i);
                    //Liste guncellenirken, o anda sohbet edilen kisi cikmadiya, onu set et ve sohbet bolunmesin
                    isChanged = false;
                    break;
                }
            }
        }
        if (isChanged) {
            //Liste guncellenirken, o anda sohbet edilen kisi ciktiysa ozel sohbet icin ilk elemani set et
            list_all_users.setSelectedIndex(0);
        }
        
        createNewPrivateChat(dlm);
        // Asagidaki fonk cagirilir
    }

    public void createNewPrivateChat(DefaultListModel dlm) {
        // Listeyi guncellerken, yeni biri geldiyse, onunla yeni bir sohbet olustur, ici bos
        String username = (String) dlm.get(dlm.getSize() - 1);
        if (!myPrivateChats.containsKey(username)) {
            myPrivateChats.put(username, "");
        }

    }

    public void privateMsgReceived(PrivateMsg pmsg) {
        // Gelen ozel mesaj incelenir
        // o kisiyle olan sohbete mesaj eklenir
        // listede o an o kisi seciliyse yani onunla sohbet ediliyorsa dlm guncellenir
        // baska biriyle ediliyorsa, liste ustunde bildirim gosterir
        String oldMsgs = "";
        if (myPrivateChats.get(pmsg.getSender()) != null) {
            oldMsgs = myPrivateChats.get(pmsg.getSender());
        }

        myPrivateChats.put(pmsg.getSender(), oldMsgs + pmsg.getSender() + ": " + pmsg.getContent() + "\n");
        if (pmsg.getSender().equals(list_all_users.getSelectedValue())) {
            txt_private_chat.setText(myPrivateChats.get(pmsg.getSender()));
        } else {
            lbltest.setText(pmsg.getSender());
            lbltest.setVisible(true);
            lbltest2.setVisible(true);
        }

    }

    public void GetAllRooms(ArrayList<String> list) {
        // Server, yeni oda kurulunca, SendAllRooms ile tum oda adlarini string arraylistte gonderir
        // Tum kullanicilarin ekranindaki oda listesi guncellenir
        roomListChoice.removeAll();
        for (String listitem : list) {
            roomListChoice.add(listitem);
        }
    }

    public void CompleteCreateAndEnter(Message msg) {
        // Severdaki addnewroom'un 2. fonku
        // Odayi ilk acan kisi icin calisir
        
        HashMap<String, ArrayList<String>> mess = (HashMap<String, ArrayList<String>>) (msg.content);
        String key = mess.keySet().toArray()[0].toString();
        ArrayList<String> users = mess.get(key);
        Room newRoom = new Room();
        newRoom.roomName = key;
        newRoom.lbl_roomName.setText(key);
        newRoom.username = txt_myusername.getText();
        newRoom.updateParticipants(users);
        newRoom.updateRoomChat(txt_myusername.getText() + " congratulations! You created a new room!");
        newRoom.setVisible(true);
        myChatRooms.add(newRoom);

    }

    public void EnterRoom(Message msg) {
        // Password kabul mesajı ile tetiklenir
        // Mesaj olarak oda bilgileri gelir, bu bilgilerle yeni oda ekranı açılır
        ArrayList<String> elements = (ArrayList<String>) msg.content;
        Room newRoom = new Room();
        newRoom.roomName = elements.get(1);
        newRoom.username = txt_myusername.getText();
        newRoom.lbl_roomName.setText(elements.get(1));
        newRoom.setVisible(true);
        myChatRooms.add(newRoom);

    }

    public static void ChatRoomUserListAddNew(ArrayList<String> mess) {
        // Bir kullanici, odaya girmek icin servere join istegi atiyor
        // Server bu kullaniciyi kontrol ediyor, odada yoksa ve sifresi duzgunse onayliyor
        // Diger kullanicilara OdaAdi-YeniKullaniciAdi ikilisinden olusan arraylist geliyor
        // Her kullanici kendi odalari arasindan bu odayi kontrol ediyor, o oda adina sahip odada bulunanlar, guncelleme yapiyor
        for (Room r : myChatRooms) {
            if (r.roomName.equals(mess.get(0))) {
                r.dlmparticipants.addElement(mess.get(1));
                r.list_participants.setModel(r.dlmparticipants);
                break;
            }
        }

    }

    public void ChatRoomNewMsg(Message msg) {
        // Bir odadan atilan mesaj dogurultusunda Server bu mesajı alıyor odaadi-mesaj
        // bu odanin uye listesinde olan tum kullanicilara bu mesaj gidiyor
        // bu fonk ile mesaj ayristilirip ekranda gereken sekilde guncelleniyor
        String room = ((ArrayList<String>) msg.content).get(0);
        String text = ((ArrayList<String>) msg.content).get(1);

        for (Room r : myChatRooms) {
            if (r.roomName.equals(room)) {
                r.dlmChat.addElement(text);
                r.list_room_chat.setModel(r.dlmChat);

                break;
            }
        }

    }

    public void ChatSetRoomOldUsers(Message msg) {
        // İlk olarak yazilan update fonksiyonu: odadaki kullanicilar listesini sadece yeni gelen kullanici icin yapiyor
        // Odaya katilan kisi de ancak kendisini odada goruyordu, bu guncelleme tum uyeleri kapsayacak sekilde tekrar yazildi
        // Odaya girmek isteyen kullanici sifreyi dogru girdiyse, SendLastUserListToJoined ile tetikleniyor
        String room = (String) ((ArrayList) msg.content).get(0);
        ArrayList<String> userslist = (ArrayList<String>) ((ArrayList) msg.content).get(1);
        System.out.println("list: " + userslist);
        for (Room mcr : myChatRooms) {
            if (mcr.roomName.equals(room)) {
                for (String u : userslist) {
                    mcr.dlmparticipants.addElement(u);
                }
                mcr.list_participants.setModel(mcr.dlmparticipants);
                break;
            }
        }

    }

    public void ChatRoomParticipantLeft(Message msg) {
        // Bir uye ayrildiktan sonra
        // Serverden gelen mesajla uye listesi guncellenir
        // mesajda tum uye listesi vardir
        String room = (String) ((ArrayList) msg.content).get(0);
        ArrayList<String> users = (ArrayList<String>) ((ArrayList) msg.content).get(1);
        
        for (Room mcr : myChatRooms) {
            if (mcr.roomName.equals(room)) {
                mcr.dlmparticipants = new DefaultListModel();
                for (String u : users) {

                    mcr.dlmparticipants.addElement(u);
                }
                mcr.list_participants.setModel(mcr.dlmparticipants);

                break;
            }
        }

    }

    public void DellFromMyChatRooms(Message msg) {
        // Ayrildigi odayi, odalarindan siler
        String roomname = (String) msg.content;
        int index = 0;
        for (int i = 0; i < myChatRooms.size(); i++) {
            if (myChatRooms.get(i).roomName.equals(roomname)) {
                index = i;
                break;
            }
        }
        myChatRooms.remove(index);
    }

    public boolean roomExistanceControl(String roomName) {
        // daha sonra kullanmak icin boolean fonk
        boolean exist = false;
        for (Room mcr : myChatRooms) {
            if (mcr.roomName.equals(roomName)) {
                exist = true;
                break;
            }
        }
        return exist;
    }
    public static void getReceivedFile(Message msg) throws IOException{
        // Oda uzerinden gelen dosyanin kaydedilmesi
        ArrayList elements=(ArrayList)msg.content;
        for (Room mcr: myChatRooms) {
            if(mcr.roomName.equals(elements.get(0))){
                mcr.dlmChat.addElement("* "+elements.get(1)+" send a file: '"+elements.get(2)+"' *");
                    mcr.list_room_chat.setModel(mcr.dlmChat);
                if(!mcr.username.equals(elements.get(1))){
                    byte[] content=(byte[])elements.get(3);   
                    File fToDownload= new File("src/files/"+((String)elements.get(2)));
                    Files.write(fToDownload.toPath(),content); 
                break;
                }
                
            }
        }
    }

    public void Reset() {

    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        jScrollPane3 = new javax.swing.JScrollPane();
        jList1 = new javax.swing.JList<>();
        jMenu1 = new javax.swing.JMenu();
        jMenu2 = new javax.swing.JMenu();
        jPopupMenu1 = new javax.swing.JPopupMenu();
        jRadioButtonMenuItem1 = new javax.swing.JRadioButtonMenuItem();
        txt_myusername = new javax.swing.JTextField();
        btn_connect = new javax.swing.JButton();
        pnl_gamer1 = new javax.swing.JPanel();
        btn_send_message = new javax.swing.JButton();
        txt_global_chat = new java.awt.TextArea();
        txt_msg_global = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        list_all_users = new javax.swing.JList<>();
        jLabel1 = new javax.swing.JLabel();
        btn_dc = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        btn_send_private = new javax.swing.JButton();
        txt_msg_private = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        txt_private_chat = new java.awt.TextArea();
        btn_enterRoom = new javax.swing.JButton();
        lbltest = new java.awt.Label();
        lbltest2 = new java.awt.Label();
        roomListChoice = new java.awt.Choice();
        jLabel4 = new javax.swing.JLabel();
        btn_newRoom = new javax.swing.JButton();

        jList1.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        jScrollPane3.setViewportView(jList1);

        jMenu1.setText("jMenu1");

        jMenu2.setText("jMenu2");

        jRadioButtonMenuItem1.setSelected(true);
        jRadioButtonMenuItem1.setText("jRadioButtonMenuItem1");

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setMinimumSize(new java.awt.Dimension(1195, 450));
        setPreferredSize(new java.awt.Dimension(1195, 450));
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        txt_myusername.setText("Username");
        getContentPane().add(txt_myusername, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 30, 140, -1));

        btn_connect.setText("Connect");
        btn_connect.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_connectActionPerformed(evt);
            }
        });
        getContentPane().add(btn_connect, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 30, 90, -1));

        pnl_gamer1.setBackground(new java.awt.Color(255, 153, 153));
        pnl_gamer1.setForeground(new java.awt.Color(51, 255, 0));
        pnl_gamer1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
        getContentPane().add(pnl_gamer1, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 60, -1, 259));

        btn_send_message.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        btn_send_message.setForeground(new java.awt.Color(0, 204, 51));
        btn_send_message.setText(">");
        btn_send_message.setEnabled(false);
        btn_send_message.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_send_messageActionPerformed(evt);
            }
        });
        getContentPane().add(btn_send_message, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 340, 60, 30));

        txt_global_chat.setEditable(false);
        getContentPane().add(txt_global_chat, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 110, 370, 220));

        txt_msg_global.setText("Selam");
        getContentPane().add(txt_msg_global, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 340, 300, 30));

        list_all_users.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        list_all_users.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                list_all_usersValueChanged(evt);
            }
        });
        jScrollPane1.setViewportView(list_all_users);

        getContentPane().add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(440, 110, 210, 220));

        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel1.setText("GLOBAL CHAT");
        getContentPane().add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 80, -1, -1));

        btn_dc.setText("Disconnect");
        btn_dc.setEnabled(false);
        btn_dc.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_dcActionPerformed(evt);
            }
        });
        getContentPane().add(btn_dc, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 30, 110, -1));

        jLabel2.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel2.setText("Online Users");
        getContentPane().add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(490, 80, -1, -1));

        btn_send_private.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        btn_send_private.setForeground(new java.awt.Color(0, 204, 51));
        btn_send_private.setText(">");
        btn_send_private.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_send_privateActionPerformed(evt);
            }
        });
        getContentPane().add(btn_send_private, new org.netbeans.lib.awtextra.AbsoluteConstraints(890, 290, 50, 30));

        txt_msg_private.setText("Ozel Mesaj");
        getContentPane().add(txt_msg_private, new org.netbeans.lib.awtextra.AbsoluteConstraints(680, 290, 200, 30));

        jLabel3.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel3.setText("Private Chat");
        getContentPane().add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(840, 80, -1, -1));

        txt_private_chat.setEditable(false);
        getContentPane().add(txt_private_chat, new org.netbeans.lib.awtextra.AbsoluteConstraints(680, 110, 260, 170));

        btn_enterRoom.setText("Enter");
        btn_enterRoom.setEnabled(false);
        btn_enterRoom.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_enterRoomActionPerformed(evt);
            }
        });
        getContentPane().add(btn_enterRoom, new org.netbeans.lib.awtextra.AbsoluteConstraints(1060, 150, -1, -1));

        lbltest.setFont(new java.awt.Font("Calibri", 2, 14)); // NOI18N
        lbltest.setForeground(new java.awt.Color(255, 51, 51));
        lbltest.setText("Usename");
        getContentPane().add(lbltest, new org.netbeans.lib.awtextra.AbsoluteConstraints(680, 30, 260, -1));

        lbltest2.setFont(new java.awt.Font("Calibri", 2, 14)); // NOI18N
        lbltest2.setForeground(new java.awt.Color(255, 51, 51));
        lbltest2.setText("send you a private message.");
        getContentPane().add(lbltest2, new org.netbeans.lib.awtextra.AbsoluteConstraints(680, 50, 260, -1));
        getContentPane().add(roomListChoice, new org.netbeans.lib.awtextra.AbsoluteConstraints(1030, 110, 130, 30));

        jLabel4.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel4.setText("Rooms");
        getContentPane().add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(1060, 90, -1, -1));

        btn_newRoom.setText("New");
        btn_newRoom.setEnabled(false);
        btn_newRoom.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_newRoomActionPerformed(evt);
            }
        });
        getContentPane().add(btn_newRoom, new org.netbeans.lib.awtextra.AbsoluteConstraints(1060, 190, 60, -1));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btn_connectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_connectActionPerformed

        //Client.Start("127.0.0.1", 2000);
        Client.Start("3.142.197.115", 2000);
        
        btn_connect.setEnabled(false);
        txt_myusername.setEnabled(false);
        btn_send_message.setEnabled(true);
        btn_dc.setEnabled(true);
        btn_enterRoom.setEnabled(true);
        btn_newRoom.setEnabled(true);
    }//GEN-LAST:event_btn_connectActionPerformed

    private void btn_send_messageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_send_messageActionPerformed

        //Global metin mesajı gönderme işlemi
        Message msg = new Message(Message.Message_Type.Text);
        String x = txt_myusername.getText() + ": " + txt_msg_global.getText();
        msg.content = x;
        Client.Send(msg);
        txt_msg_global.setText("");

    }//GEN-LAST:event_btn_send_messageActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        // TODO add your handling code here:
        //Eğer bağlıysa, form X ile kapanırken de Disconnect butonunda yapılan işlemleri yapar
        if (Client.socket != null) {
            Message msg = new Message(Message.Message_Type.Disconnect);
            String x = txt_myusername.getText();
            msg.content = x;
            Client.Send(msg);

            Client.Stop();
        }

    }//GEN-LAST:event_formWindowClosing

    private void btn_dcActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_dcActionPerformed
        // TODO add your handling code here:
        // Disconnect butonuna basıldığında
        // Server'a ayrılacağı bildirisini yapar.
        // Server onu kullanıcı listesinden çıkartır ve threadlerini durdurur.
        if (myChatRooms.size() == 0) {
            Message msg = new Message(Message.Message_Type.Disconnect);
            String x = txt_myusername.getText();
            msg.content = x;
            Client.Send(msg);

            Client.Stop();
            btn_dc.setEnabled(false);
            btn_send_message.setEnabled(false);
            btn_connect.setEnabled(true);
            txt_myusername.setEnabled(true);
            btn_newRoom.setEnabled(false);
            btn_enterRoom.setEnabled(false);
        }


    }//GEN-LAST:event_btn_dcActionPerformed

    private void btn_send_privateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_send_privateActionPerformed
        // TODO add your handling code here:
        // Server uzerinden, listeden secilen kullaniciya, ozel mesaj gonderir
        // PrivateMsg nesnesi
        String senderUsername = txt_myusername.getText();
        String targetUsername = list_all_users.getSelectedValue();
        String content = txt_msg_private.getText();
        PrivateMsg pmsg = new PrivateMsg(senderUsername, targetUsername, content);
        String oldMsgs = "";
        if (myPrivateChats.get(pmsg.getTarget()) != null) {
            oldMsgs = myPrivateChats.get(targetUsername);
        }

        myPrivateChats.put(targetUsername, oldMsgs + senderUsername + ": " + content + "\n");
        txt_private_chat.setText(myPrivateChats.get(targetUsername));
        txt_msg_private.setText("");

        Message msg = new Message(Message.Message_Type.PrivateMsg);
        msg.content = pmsg;
        Client.Send(msg);


    }//GEN-LAST:event_btn_send_privateActionPerformed

    private void list_all_usersValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_list_all_usersValueChanged
        // TODO add your handling code here:

        selectedUserForPrvChat = list_all_users.getSelectedValue();
        if (selectedUserForPrvChat != null && selectedUserForPrvChat.equals(txt_myusername.getText())) {
            txt_private_chat.setText("");
            txt_private_chat.setEnabled(false);
            txt_msg_private.setEnabled(false);
            btn_send_private.setEnabled(false);
        } else {
            txt_private_chat.setText(myPrivateChats.get(selectedUserForPrvChat));
            txt_private_chat.setEnabled(true);
            txt_msg_private.setEnabled(true);
            btn_send_private.setEnabled(true);

            if (selectedUserForPrvChat != null && selectedUserForPrvChat.equals(lbltest.getText())) {

                lbltest.setVisible(false);
                lbltest2.setVisible(false);
            }
        }


    }//GEN-LAST:event_list_all_usersValueChanged

    private void btn_newRoomActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_newRoomActionPerformed
        // TODO add your handling code here:
        // Yeni oda olusturma ekranı, mevcut odaların isimleri gonderilerek aciliyor
        cr = new CreateRoom();
        ArrayList<String> roomnames = new ArrayList<String>();
        for (int i = 0; i < roomListChoice.getItemCount(); i++) {
            roomnames.add(roomListChoice.getItem(i));
        }

        cr.username = txt_myusername.getText();
        cr.rNames = roomnames;
        cr.setVisible(true);

        

    }//GEN-LAST:event_btn_newRoomActionPerformed

    private void btn_enterRoomActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_enterRoomActionPerformed
        // TODO add your handling code here:
        // Bir odaya girmek icin, once sifre girme ekrani aciliyor
        if (roomListChoice.getSelectedItem() != null && roomListChoice.getItemCount() > 0) {
            if (!roomExistanceControl(roomListChoice.getSelectedItem())) {
                String roomName = roomListChoice.getSelectedItem();
                enterPass = new PassInputScreen();
                enterPass.username = txt_myusername.getText();
                enterPass.lbl_roomname.setText(roomName);
                enterPass.setVisible(true);
            }
        }
    }//GEN-LAST:event_btn_enterRoomActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Login.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Login.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Login.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Login.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Login().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    public javax.swing.JButton btn_connect;
    private javax.swing.JButton btn_dc;
    private javax.swing.JButton btn_enterRoom;
    private javax.swing.JButton btn_newRoom;
    public javax.swing.JButton btn_send_message;
    private javax.swing.JButton btn_send_private;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JList<String> jList1;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JPopupMenu jPopupMenu1;
    private javax.swing.JRadioButtonMenuItem jRadioButtonMenuItem1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane3;
    private java.awt.Label lbltest;
    private java.awt.Label lbltest2;
    private javax.swing.JList<String> list_all_users;
    private javax.swing.JPanel pnl_gamer1;
    public java.awt.Choice roomListChoice;
    public java.awt.TextArea txt_global_chat;
    private javax.swing.JTextField txt_msg_global;
    private javax.swing.JTextField txt_msg_private;
    public javax.swing.JTextField txt_myusername;
    public java.awt.TextArea txt_private_chat;
    // End of variables declaration//GEN-END:variables
}
