using System;
using System.Collections.Generic;
using System.Linq;
using System.Net;
using System.Net.Sockets;
using System.Text;
using System.Threading;
using System.Threading.Tasks;

namespace AppServer
{
    internal class Server
    {
        public TcpListener server = null;
        public List<Client> clients = new List<Client>();

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
                    //Console.WriteLine("Connecting...");
                    TcpClient client = server.AcceptTcpClient();
                    Client newUser = new Client(client);

                    //Console.WriteLine("Connected");
                    clients.Add(newUser);
                    Thread t = new Thread(new ParameterizedThreadStart(Handle));
                    t.Start(newUser);
                }
            }
            catch (SocketException Ex)
            {
                //Console.WriteLine("" + Ex.ToString());
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

                }

            }
            catch (Exception e)
            {

                //Console.WriteLine("Exception: {0}", e.ToString());
                client.Close();

            }
        }


    }
}
    

