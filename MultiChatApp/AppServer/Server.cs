using System;
using System.Collections.Generic;
using System.Linq;
using System.Net;
using System.Net.Sockets;
using System.Text;
using System.Threading;
using System.Threading.Tasks;
using System.Windows;

namespace AppServer
{
    internal class Server
    {
        public TcpListener server = null;
        public List<Client> clients = new List<Client>();
        public List<Room> rooms = new List<Room>();
        MainWindow myWindow = Application.Current.MainWindow as MainWindow;
        public Server(int port)
        {
            string IP = "127.0.0.1";

            IPAddress localAddr = IPAddress.Parse(IP);
            server = new TcpListener(localAddr, port);

            server.Start();

        }
        public void StartListener()
        {
            try
            {
                while (true)
                {
                    TcpClient client = server.AcceptTcpClient();
                    Client newUser = new Client(client);
                    Message("ConnOK<", newUser, false);
                    clients.Add(newUser);
                    Thread t = new Thread(new ParameterizedThreadStart(Handle));
                    t.Start(newUser);
                }
            }
            catch (SocketException Ex)
            {
            }
        }

        // tcp server examle
        //https://codinginfinite.com/multi-threaded-tcp-server-core-example-csharp/
        public void Handle(Object obj)
        {

            TcpClient client = ((Client)obj).user_tcpclient;
            var stream = client.GetStream();
            string imei = String.Empty;

            string data = null;
            Byte[] bytes = new Byte[256];
            int i;
            try
            {
                while ((i = stream.Read(bytes, 0, bytes.Length)) != 0)
                {

                    string hex = BitConverter.ToString(bytes);
                    data = Encoding.UTF32.GetString(bytes, 0, i);

                    if (data.Contains("NewNick"))
                    {
                        string nick = data.Split('<')[1];
                        foreach (Client user in clients)
                        {
                            if (user == ((Client)obj))
                            {
                                user.id = new Random().Next(1, 999999999);
                                user.nick = nick;
                            }

                            addClientToList(user);
                            Message("" + user.id + "~" + user.nick + "~" + connectingClient(user), user, false);
                            Message("NewUser=" + user.id + "<" + user.nick, user, true); 
                        }

                    }

                }

            }
            catch (Exception e)
            {

                client.Close();

            }
        }

        //main function, the soine of the app
        public void Message(string str, Client client, bool broadcast)
        {
            try
            {

                foreach (Client cl in clients)
                {
                    if (cl != client)
                    {
                        var stream = cl.user_tcpclient.GetStream();

                        Byte[] reply = Encoding.UTF32.GetBytes(str);
                        stream.Write(reply, 0, reply.Length);
                    }
                }

            }
            catch (Exception Ex)
            {

            }
        }

        public string connectingClient(Client me) 
        {
            string users = "newConnections{";
            foreach (Client client in myWindow.lblClients.Items)
            {
                if (me != client) users += "[" + client.id + "<" + client.nick + ""; 
            }
            users += "}";

            string rooms = "{";
            foreach (Room room in this.rooms)
            {
                if (room.id == 0) continue;
                rooms += "[" + room.id + "<" + room.name + ""; 
            }
            rooms += "}";

            return users + rooms;
        }

        public void addClientToList(Client client)
        {
            Application.Current.Dispatcher.Invoke(delegate
            {
                myWindow.lblClients.Items.Add(client);
            });
        }
    }
}


