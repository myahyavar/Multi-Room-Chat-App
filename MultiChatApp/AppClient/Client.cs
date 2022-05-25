using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Net.Sockets;
using System.Text;
using System.Threading;
using System.Threading.Tasks;
using System.Windows;

namespace AppClient
{
    public class Client
    {
        public TcpClient client = null;
        NetworkStream stream = null;
        JoinWindow jWindow = null;
        GeneralWindow myWindow = Application.Current.MainWindow as GeneralWindow;


        public Client(JoinWindow joinWindow)
        {
            jWindow = joinWindow;
        }

        public void Connect(String server)
        {
            try
            {
                int port = 8888;
                client = new TcpClient(server, port);

                NetworkStream stream = client.GetStream();

                int count = 0;
                while (count++ < 3)
                {

                    Byte[] data = System.Text.Encoding.ASCII.GetBytes("connect");
                    stream.Write(data, 0, data.Length);
                    // invokes are required 
                    Application.Current.Dispatcher.Invoke(delegate
                    {
                        jWindow.txtTest.Content = "Connected";

                    });

                    Thread t = new Thread(new ParameterizedThreadStart(Handle));
                    t.Start(stream);

                    // need to build a server - like handler for data streams
                }

            }
            catch (Exception e)
            {
                Application.Current.Dispatcher.Invoke(delegate
                {
                    jWindow.txtTest.Content ="you fucked up";
                });
                client = null;
            }
        }

        public void sendMessage(string message)
        {
            Byte[] data = Encoding.UTF32.GetBytes(message);
            stream.Write(data, 0, data.Length);
        }

        public void Handle(Object obj)
        {
            var stream = (NetworkStream)obj;
            string data = null;
            Byte[] bytes = new Byte[1048576];
            int i;
            try
            {
                while ((i = stream.Read(bytes, 0, bytes.Length)) != 0)
                {
                    data = Encoding.UTF32.GetString(bytes, 0, i); 

                    
                    if (data.Contains("ConnOK"))
                    {
                        Application.Current.Dispatcher.Invoke(delegate
                        {
                            myWindow.joinWindow.btnJoin.IsEnabled = true;
                            myWindow.joinWindow.txtNickname.IsEnabled = true;

                            myWindow.joinWindow.txtServer.IsEnabled = false;
                            myWindow.joinWindow.btnConnect.IsEnabled = false;

                        });
                    }
                    else if (data.Contains("newConnections"))
                    {
                        string[] inc = data.Split('~');
                        Application.Current.Dispatcher.Invoke(delegate
                        {
                            myWindow.myId = inc[0];
                            myWindow.myNickName = inc[1];

                            myWindow.txtId.Text = myWindow.myId;
                            myWindow.Title = "Nick: " + myWindow.myNickName;
                            myWindow.btnCreateGroup.IsEnabled = true;
                            myWindow.btnJoinGroup.IsEnabled = true;
                            myWindow.joinWindow.Close();

                            // exit button would be cool


                            myWindow.txtMessage.IsEnabled = true;
                            myWindow.btnSend.IsEnabled = true;
                        });
                        newbie(inc[2]);
                        //sendMessage("joinRoom<0");

                    }
                    else if (data.Contains("newUser="))
                    {
                        if (myWindow.myId != null)
                        {
                            string inc = data.Remove(0, 8);
                            string[] user_info = inc.Split('<');
                            User userToAdd = new User(user_info[0], user_info[1]);
                            myWindow.users.Add(userToAdd);


                            Application.Current.Dispatcher.Invoke(delegate
                            {
                                bool addCheck = false;
                                if (myWindow.lblClients.Items.Count > 0)
                                {
                                    foreach (User uye in myWindow.lblClients.Items)
                                    {
                                        if (uye.id != userToAdd.id)
                                            addCheck = true;
                                    }
                                    if (addCheck) myWindow.lblClients.Items.Add(userToAdd);
                                }
                                else
                                {
                                    myWindow.lblClients.Items.Add(userToAdd);
                                }

                            });
                        }
                    }

                    // exiting user and rooms next...
                }

            }
            catch (IOException e)
            {

            }



        }


        public void newbie(string data)
        {
            try
            {
                data = data.Remove(0, 15);
                string[] datas = data.Split('}');

                for (int i = 0; i < datas.Length; i++)
                {

                        if (datas[i].Length > 1)
                        {
                            string inc = datas[i].Remove(0, 1);
                            if (i == 0)
                            {
                                string[] users = inc.Split('[');
                                foreach (string input in users)
                                {
                                    if (input.Contains("<"))
                                    {
                                        string[] user_info = input.Split('<');
                                        User userToAdd = new User(user_info[0], user_info[1]);
                                        Application.Current.Dispatcher.Invoke(delegate {
                                            bool addCheck = false;
                                            if (myWindow.lblClients.Items.Count > 0)
                                            {
                                                foreach (User user in myWindow.lblClients.Items)
                                                {
                                                    if (user.id != userToAdd.id)
                                                        addCheck = true;
                                                }
                                                if (addCheck) myWindow.lblClients.Items.Add(userToAdd);
                                            }
                                            else
                                            {
                                                myWindow.lblClients.Items.Add(userToAdd);
                                            }
                                        });
                                    }
                                }
                            }
                        }
                    
                }

            }
            catch (IOException e)
            {
            }
        }
    }
}
