package message;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import client.Client;
import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.DefaultListModel;

public class MainRoom extends javax.swing.JFrame {

    public static MainRoom MRoom;
    public CreateRoom cr;
    public Room enter;
    public static ArrayList<Room> Rooms;
    public Thread tmr_slider;


    @SuppressWarnings("empty-statement")
    public MainRoom() {
        initComponents();
        MRoom = this;
        Rooms = new ArrayList<>();

        // run meanwhile connected to the server
        tmr_slider = new Thread(() -> {
            while (Client.socket.isConnected()) {
                try {

                    Thread.sleep(50);
                } catch (InterruptedException ex) {
                    Logger.getLogger(MainRoom.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });

    }

    // updates the user list
    public void updateUList(DefaultListModel dlm) {
        String selected = listUsers.getSelectedValue();
        listUsers.setModel(dlm);
        boolean isChanged = true;
        if (selected != null) {
            for (int i = 0; i < dlm.size(); i++) {
                String uname = (String) dlm.get(i);

                if (selected.equals(uname)) {
                    listUsers.setSelectedIndex(i);
                    isChanged = false;
                    break;
                }
            }
        }
        if (isChanged) {
            listUsers.setSelectedIndex(0);
        }

    }




    // updates room list
    public void GetAllRooms(ArrayList<String> list) {
        roomListChoice.removeAll();
        for (String listitem : list) {
            roomListChoice.add(listitem);
        }
    }

    // after creator creates the room , he enters it
    public void CreateAndEnterRoom(Message msg) {
        HashMap<String, ArrayList<String>> stuff = (HashMap<String, ArrayList<String>>) (msg.content);
        String key = stuff.keySet().toArray()[0].toString();
        ArrayList<String> users = stuff.get(key);
        Room newRoom = new Room();
        newRoom.roomName = key;
        newRoom.lblRoomName.setText(key);
        newRoom.username = txtUsername.getText();
        newRoom.updateFolk(users);
        newRoom.updateChat(txtUsername.getText() + "  created a new room");
        newRoom.setVisible(true);
        Rooms.add(newRoom);

    }

    // entering a created room
    public void EnterRoom(Message msg) {
        ArrayList<String> elements = (ArrayList<String>) msg.content;
        Room newRoom = new Room();
        newRoom.roomName = elements.get(1);
        newRoom.username = txtUsername.getText();
        newRoom.lblRoomName.setText(elements.get(1));
        newRoom.setVisible(true);
        Rooms.add(newRoom);

    }

    public static void RoomUListAdd(ArrayList<String> mess) {
        for (Room r : Rooms) {
            if (r.roomName.equals(mess.get(0))) {
                r.dlmFolk.addElement(mess.get(1));
                r.lFolk.setModel(r.dlmFolk);
                break;
            }
        }

    }

    public void NewMsg(Message msg) {
        String room = ((ArrayList<String>) msg.content).get(0);
        String text = ((ArrayList<String>) msg.content).get(1);

        for (Room r : Rooms) {
            if (r.roomName.equals(room)) {
                r.dlmChat.addElement(text);
                r.RoomChat.setModel(r.dlmChat);

                break;
            }
        }

    }

    public void OldUsers(Message msg) {
        String room = (String) ((ArrayList) msg.content).get(0);
        ArrayList<String> userslist = (ArrayList<String>) ((ArrayList) msg.content).get(1);
        System.out.println("list: " + userslist);
        for (Room mcr : Rooms) {
            if (mcr.roomName.equals(room)) {
                for (String u : userslist) {
                    mcr.dlmFolk.addElement(u);
                }
                mcr.lFolk.setModel(mcr.dlmFolk);
                break;
            }
        }

    }

    // after user left the room
    public void ChatRoomUserLeft(Message msg) {
        String room = (String) ((ArrayList) msg.content).get(0);
        ArrayList<String> users = (ArrayList<String>) ((ArrayList) msg.content).get(1);

        for (Room mcr : Rooms) {
            if (mcr.roomName.equals(room)) {
                mcr.dlmFolk = new DefaultListModel();
                for (String u : users) {

                    mcr.dlmFolk.addElement(u);
                }
                mcr.lFolk.setModel(mcr.dlmFolk);

                break;
            }
        }

    }

    // deleting the room
    public void delRoom(Message msg) {
        String roomname = (String) msg.content;
        int index = 0;
        for (int i = 0; i < Rooms.size(); i++) {
            if (Rooms.get(i).roomName.equals(roomname)) {
                index = i;
                break;
            }
        }
        Rooms.remove(index);
    }

    public boolean doRoomExists(String roomName) {
        boolean exist = false;
        for (Room mcr : Rooms) {
            if (mcr.roomName.equals(roomName)) {
                exist = true;
                break;
            }
        }
        return exist;
    }

    // file function
    public static void getFile(Message msg) throws IOException {
        ArrayList elements = (ArrayList) msg.content;
        for (Room mcr : Rooms) {
            if (mcr.roomName.equals(elements.get(0))) {
                mcr.dlmChat.addElement("* " + elements.get(1) + " sent: '" + elements.get(2) + "' *");
                mcr.RoomChat.setModel(mcr.dlmChat);
                if (!mcr.username.equals(elements.get(1))) {
                    byte[] content = (byte[]) elements.get(3);
                    File fToDownload = new File("src/files/" + ((String) elements.get(2)));
                    Files.write(fToDownload.toPath(), content);
                    break;
                }

            }
        }
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
        txtUsername = new javax.swing.JTextField();
        btnConnect = new javax.swing.JButton();
        pnl_gamer1 = new javax.swing.JPanel();
        btnSend = new javax.swing.JButton();
        txtMessageGeneral = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        listUsers = new javax.swing.JList<>();
        jLabel1 = new javax.swing.JLabel();
        btnDisconnect = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        btnEnter = new javax.swing.JButton();
        roomListChoice = new java.awt.Choice();
        jLabel4 = new javax.swing.JLabel();
        btnNewR = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JSeparator();
        jScrollPane2 = new javax.swing.JScrollPane();
        txtGeneral = new javax.swing.JTextArea();

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
        setFocusableWindowState(false);
        setMinimumSize(new java.awt.Dimension(560, 560));
        setPreferredSize(new java.awt.Dimension(600, 600));
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        txtUsername.setText("Nick");
        getContentPane().add(txtUsername, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 20, 140, -1));

        btnConnect.setText("Connect");
        btnConnect.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnConnectActionPerformed(evt);
            }
        });
        getContentPane().add(btnConnect, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 20, 90, -1));

        pnl_gamer1.setBackground(new java.awt.Color(255, 153, 153));
        pnl_gamer1.setForeground(new java.awt.Color(51, 255, 0));
        pnl_gamer1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
        getContentPane().add(pnl_gamer1, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 60, -1, 259));

        btnSend.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        btnSend.setForeground(new java.awt.Color(0, 204, 51));
        btnSend.setText(">");
        btnSend.setEnabled(false);
        btnSend.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSendActionPerformed(evt);
            }
        });
        getContentPane().add(btnSend, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 340, 60, 30));

        txtMessageGeneral.setText("hi");
        txtMessageGeneral.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtMessageGeneralActionPerformed(evt);
            }
        });
        getContentPane().add(txtMessageGeneral, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 340, 300, 30));

        listUsers.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { ".", ".", ".", ".", ".", ".", ".", ".", "." };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        jScrollPane1.setViewportView(listUsers);

        getContentPane().add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(440, 100, 100, 220));

        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel1.setText("General Chat");
        getContentPane().add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 70, -1, -1));

        btnDisconnect.setText("Disconnect");
        btnDisconnect.setEnabled(false);
        btnDisconnect.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDisconnectActionPerformed(evt);
            }
        });
        getContentPane().add(btnDisconnect, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 20, 110, -1));

        jLabel2.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel2.setText("Online Users");
        getContentPane().add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(440, 70, -1, -1));

        btnEnter.setText("Join");
        btnEnter.setEnabled(false);
        btnEnter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEnterActionPerformed(evt);
            }
        });
        getContentPane().add(btnEnter, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 460, -1, 20));
        getContentPane().add(roomListChoice, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 420, 130, 30));

        jLabel4.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel4.setText("Rooms");
        getContentPane().add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 400, -1, 20));

        btnNewR.setText("Create");
        btnNewR.setEnabled(false);
        btnNewR.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNewRActionPerformed(evt);
            }
        });
        getContentPane().add(btnNewR, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 460, 80, 20));
        getContentPane().add(jSeparator1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 50, 600, 20));

        txtGeneral.setEditable(false);
        txtGeneral.setColumns(20);
        txtGeneral.setRows(5);
        jScrollPane2.setViewportView(txtGeneral);

        getContentPane().add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 100, 370, 220));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnConnectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnConnectActionPerformed
        // aws expired :(
        //Client.Start("ec2-54-159-16-90.compute-1.amazonaws.com", 6000);
        Client.Start("127.0.0.1", 8888);

        btnConnect.setEnabled(false);
        txtUsername.setEnabled(false);
        btnSend.setEnabled(true);
        btnDisconnect.setEnabled(true);
        btnEnter.setEnabled(true);
        btnNewR.setEnabled(true);


    }//GEN-LAST:event_btnConnectActionPerformed

    private void btnSendActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSendActionPerformed

        Message msg = new Message(Message.Message_Type.Text);
        String x = txtUsername.getText() + ": " + txtMessageGeneral.getText();
        msg.content = x;
        Client.Send(msg);
        txtMessageGeneral.setText("");

    }//GEN-LAST:event_btnSendActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing

        if (Client.socket != null) {
            Message msg = new Message(Message.Message_Type.Disconnect);
            String x = txtUsername.getText();
            msg.content = x;
            Client.Send(msg);
            Client.Terminate();
        }

    }//GEN-LAST:event_formWindowClosing

    private void btnDisconnectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDisconnectActionPerformed

        if (Rooms.size() == 0) {
            Message msg = new Message(Message.Message_Type.Disconnect);
            String x = txtUsername.getText();
            msg.content = x;
            Client.Send(msg);

            Client.Terminate();
            btnDisconnect.setEnabled(false);
            btnSend.setEnabled(false);
            btnConnect.setEnabled(true);
            txtUsername.setEnabled(true);
            btnNewR.setEnabled(false);
            btnEnter.setEnabled(false);
        }


    }//GEN-LAST:event_btnDisconnectActionPerformed

    private void btnNewRActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNewRActionPerformed

        cr = new CreateRoom();
        ArrayList<String> roomnames = new ArrayList<String>();
        for (int i = 0; i < roomListChoice.getItemCount(); i++) {
            roomnames.add(roomListChoice.getItem(i));
        }

        cr.username = txtUsername.getText();
        cr.rNames = roomnames;
        cr.setVisible(true);


    }//GEN-LAST:event_btnNewRActionPerformed

    private void btnEnterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEnterActionPerformed

        if (roomListChoice.getSelectedItem() != null && roomListChoice.getItemCount() > 0) {
            if (!doRoomExists(roomListChoice.getSelectedItem())) {
                String roomName = roomListChoice.getSelectedItem();
                enter = new Room();
                enter.username = txtUsername.getText();
                enter.lblRoomName.setText(roomName);
                enter.setVisible(true);
            }
        }
    }//GEN-LAST:event_btnEnterActionPerformed

    private void txtMessageGeneralActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtMessageGeneralActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtMessageGeneralActionPerformed

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
            java.util.logging.Logger.getLogger(MainRoom.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MainRoom.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MainRoom.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MainRoom.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new MainRoom().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    public javax.swing.JButton btnConnect;
    private javax.swing.JButton btnDisconnect;
    private javax.swing.JButton btnEnter;
    private javax.swing.JButton btnNewR;
    public javax.swing.JButton btnSend;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JList<String> jList1;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JPopupMenu jPopupMenu1;
    private javax.swing.JRadioButtonMenuItem jRadioButtonMenuItem1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JList<String> listUsers;
    private javax.swing.JPanel pnl_gamer1;
    public java.awt.Choice roomListChoice;
    public javax.swing.JTextArea txtGeneral;
    private javax.swing.JTextField txtMessageGeneral;
    public javax.swing.JTextField txtUsername;
    // End of variables declaration//GEN-END:variables
}
