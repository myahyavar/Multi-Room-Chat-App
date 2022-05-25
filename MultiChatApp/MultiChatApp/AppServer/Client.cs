using System;
using System.Collections.Generic;
using System.Linq;
using System.Net.Sockets;
using System.Text;
using System.Threading.Tasks;

namespace AppServer
{
    internal class Client
    {
        public int id = -1;
        public string nick = "";
        public TcpClient user_tcpclient;

        public Client(TcpClient tcpclient)
        {
            nick = "newclient";
            user_tcpclient = tcpclient;
        }

        override
        public string ToString()
        {
            return nick;
        }
    }
}
