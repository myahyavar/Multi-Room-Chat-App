using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace AppClient
{
    public class User
    {
        public string id { get; set; }
        public string nick { get; set; }
        public User(string kid, string kname)
        {
            id = kid;
            nick = kname;

        }
        override
      public String ToString()
        {
            return nick;
        }
    }
}
