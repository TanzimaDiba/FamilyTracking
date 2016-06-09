using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Configuration;
using System.Data.SqlClient;

namespace WebApplication
{
    public class DBConnect
    {

        private static SqlConnection NewCon;
        private static string conStr = ConfigurationManager.ConnectionStrings["WebConnectionString"].ConnectionString;

        public static SqlConnection getConnection()
        {
            NewCon = new SqlConnection(conStr);
            return NewCon;

        }
        public DBConnect()
        {

        }

    }
}