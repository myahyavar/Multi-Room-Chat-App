using System;
using System.Collections.Generic;
using System.Linq;
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

namespace AppClient
{
    /// <summary>
    /// MainWindow.xaml etkileşim mantığı
    /// </summary>
    public partial class JoinWindow : Window
    {
        GeneralWindow myWindow = Application.Current.MainWindow as GeneralWindow;

        public JoinWindow()
        {
            InitializeComponent();
        }

        private void Connect_Click(object sender, RoutedEventArgs e)
        {
            btnConnect.Content = "Connecting...";
            //btnConnect.IsEnabled=false;
            string ip = "127.0.0.1";
            if (txtServer.Text != "")
            {
                ip = txtServer.Text;
            }
            myWindow.myClient = new Client(this);
            new Thread(() =>
            {
                Thread.CurrentThread.IsBackground = true;
                myWindow.myClient.Connect(ip);
            }).Start();

        }


        private void Join_Click(object sender, RoutedEventArgs e)
        {

        }


    }
}
