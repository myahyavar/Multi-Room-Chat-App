using System;
using System.Collections.Generic;
using System.Linq;
using System.Net;
using System.Net.Sockets;
using System.Text;
using System.Threading.Tasks;

namespace AppServer
{
    internal class Server
    {
        public TcpListener server = null;
        public List<Client> clientLists = new List<Client>();

        public Server(int port)
        {
            string IP = "127.0.0.1";

            IPAddress localAddr = IPAddress.Parse(IP);
            server = new TcpListener(localAddr, port);

            server.Start();

        }
    }
}
