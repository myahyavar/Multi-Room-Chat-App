using System;
using System.Collections.Generic;
using System.Linq;
using System.Net.Sockets;
using System.Text;
using System.Threading;
using System.Threading.Tasks;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Data;
using System.Windows.Documents;
using System.Windows.Input;
using System.Windows.Media;
using System.Windows.Media.Imaging;
using System.Windows.Navigation;
using System.Windows.Shapes;

namespace AppServer
{
    /// <summary>
    /// MainWindow.xaml etkileşim mantığı
    /// </summary>
    public partial class MainWindow : Window
    {
        Server server = null;
        public MainWindow()
        {
            InitializeComponent();
        }

        private void Button_Click(object sender, RoutedEventArgs e)
        {
            if (btnServer.Content.ToString() != "Stop Server")
            {
                try
                {
                    server = new Server(8888);

                    Thread t = new Thread(delegate ()
                    {
                        server.StartListener();

                    });
                    t.Start();
                    txtCheck.Text = ("Server Started...");
                    //Console.WriteLine("Server Started...");
                    btnServer.Content = "Stop Server";
                }
                catch (SocketException err)
                {
                    //Console.WriteLine("SocketException: {0}", err);
                    txtCheck.Text = (""+err);


                }
            }
            else
            {
                btnServer.Content = "Start Server";
                server.server.Stop();
                server = null;

            }
        }

        private void Window_Closing(object sender, System.ComponentModel.CancelEventArgs e)
        {

            if (server != null)
            {
                server.server.Stop();
            }
        }
    }
}
