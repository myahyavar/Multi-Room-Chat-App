using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace AppServer
{
    internal class Room
    {
        public int id = (new Random()).Next(1, 9999999);
        public string name;


        public Room(string name, int id)
        {
            this.id = id;
            this.name = name;

        }

        override
        public string ToString()
        {
            return this.name;
        }

    }
}

