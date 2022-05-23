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
        try
         {
            int port = 8888;
            client = new TcpClient(server,port);

            NetworkStream stream = client.GetStream();

            int count = 0;
            while (count++ < 3)
            {

                Byte[] data = System.Text.Encoding.ASCII.GetBytes("connect");
                stream.Write(data, 0, data.Length);

                    // need to build a server - like handler for data streams
            }

        } 
        catch (Exception e) 
        {
                client = null;
        }
}
    }
}
