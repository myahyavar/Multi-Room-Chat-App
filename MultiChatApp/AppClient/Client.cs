using System;
using System.Collections.Generic;
using System.Linq;
using System.Net.Sockets;
using System.Text;
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
    }
}
