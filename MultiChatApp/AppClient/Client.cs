using System;
using System.Collections.Generic;
using System.Linq;
using System.Net.Sockets;
using System.Text;
using System.Threading.Tasks;

namespace AppClient
{
    internal class Client
    {
        public TcpClient client = null;
        JoinWindow jWindow = null;

       public Client( JoinWindow joinWindow)
        {
           jWindow = joinWindow;
        }

        public void Connect(String server)
        {
            int port = 8888;
            client = new TcpClient(server,port);
        }
    }
}
