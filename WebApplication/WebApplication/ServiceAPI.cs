using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Data;
using System.Data.SqlClient;

namespace WebApplication
{
    public class ServiceAPI : IServiceAPI
    {
        SqlConnection dbConnection;

        public ServiceAPI()
        {
            dbConnection = DBConnect.getConnection();
        }

        public int CreateNewAccount(string parent_ip, string email, string password)
        {
            int msg = 0;

            if (dbConnection.State.ToString() == "Closed")
            {
                dbConnection.Open();
            }

            SqlCommand command = new SqlCommand("SELECT * FROM Parent WHERE parent_ip = @parent_ip", dbConnection);
            SqlDataAdapter da = new SqlDataAdapter(command);
            command.Parameters.AddWithValue("@parent_ip", parent_ip);
            DataSet ds = new DataSet();
            da.Fill(ds);
            if (ds.Tables[0].Rows.Count > 0)
            {
                msg = 1;
                dbConnection.Close();
            }
            else
            {
                SqlCommand command1 = new SqlCommand("SELECT * FROM Parent WHERE email = @email", dbConnection);
                SqlDataAdapter da1 = new SqlDataAdapter(command1);
                command1.Parameters.AddWithValue("@email", email);
                DataSet ds1 = new DataSet();
                da1.Fill(ds1);
                if (ds1.Tables[0].Rows.Count > 0)
                {
                    msg = 2;
                    dbConnection.Close();
                }
                else
                {
                    SqlCommand command2 = new SqlCommand("SELECT * FROM Child WHERE child_ip = @child_ip", dbConnection);
                    SqlDataAdapter da2 = new SqlDataAdapter(command2);
                    command2.Parameters.AddWithValue("@child_ip", parent_ip);
                    DataSet ds2 = new DataSet();
                    da2.Fill(ds2);
                    if (ds2.Tables[0].Rows.Count > 0)
                    {
                        msg = 3;
                        dbConnection.Close();
                    }
                    else
                    {
                        SqlCommand cmd = new SqlCommand("INSERT INTO Parent (parent_ip, email, password) VALUES(@parent_ip, @email, @password)", dbConnection);
                        cmd.Parameters.AddWithValue("@parent_ip", parent_ip);
                        cmd.Parameters.AddWithValue("@email", email);
                        cmd.Parameters.AddWithValue("@password", password);
                        cmd.ExecuteNonQuery();
                        dbConnection.Close();

                        msg = 4;
                    }
                }
            }

            return msg;
        }

        public DataTable ParentDetails()
        {

            DataTable parentTable = new DataTable();
            parentTable.Columns.Add("parent_ip", typeof(String));
            parentTable.Columns.Add("email", typeof(String));
            parentTable.Columns.Add("password", typeof(String));

            if (dbConnection.State.ToString() == "Closed")
            {
                dbConnection.Open();
            }

            string query = "SELECT parent_ip, email, password FROM Parent;";
            SqlCommand command = new SqlCommand(query, dbConnection);
            SqlDataReader reader = command.ExecuteReader();

            if (reader.HasRows)
            {
                while (reader.Read())
                {
                    parentTable.Rows.Add(reader["parent_ip"], reader["email"], reader["password"]);
                }
            }

            reader.Close();
            dbConnection.Close();
            return parentTable;
        }

        public string UserAuthentication(string ip, string email, string password)
        {
            string auth = "invalid";

            if (dbConnection.State.ToString() == "Closed")
            {
                dbConnection.Open();
            }

            SqlCommand command = new SqlCommand("SELECT id FROM Parent WHERE parent_ip = @parent_ip AND email = @email AND password = @password", dbConnection);
            command.Parameters.AddWithValue("@parent_ip", ip);
            command.Parameters.AddWithValue("@email", email);
            command.Parameters.AddWithValue("@password", password);
            SqlDataReader reader = command.ExecuteReader();

            if (reader.HasRows)
            {
                auth = "parent";
                reader.Close();
            }
            else
            {
                reader.Close();
                SqlCommand command1 = new SqlCommand("SELECT id FROM Child WHERE child_ip = @child_ip AND email = @email AND password = @password", dbConnection);
                command1.Parameters.AddWithValue("@child_ip", ip);
                command1.Parameters.AddWithValue("@email", email);
                command1.Parameters.AddWithValue("@password", password);
                SqlDataReader reader1 = command1.ExecuteReader();

                if (reader1.HasRows)
                {
                    auth = "child";
                    reader1.Close();
                }
                else
                {
                    reader1.Close();
                    SqlCommand command2 = new SqlCommand("SELECT id FROM Parent WHERE email = @email AND password = @password", dbConnection);
                    command2.Parameters.AddWithValue("@email", email);
                    command2.Parameters.AddWithValue("@password", password);
                    SqlDataReader reader2 = command2.ExecuteReader();
                    if (reader2.HasRows)
                    {
                        auth = "set";
                    }
                    else
                    {
                        auth = "not";
                    }
                    reader2.Close();
                }
            }
            dbConnection.Close();
            return auth;
        }

        public void CreateChildAccount(string child_ip, string email, string password, string child_name, int age, string problem, double current_lat, double current_lon, string on_off)
        {

            if (dbConnection.State.ToString() == "Closed")
            {
                dbConnection.Open();
            }
            SqlCommand cmd = new SqlCommand("INSERT INTO Child (child_ip, email, password, child_name, age, problem, current_lat, current_lon, on_off) VALUES(@child_ip, @email, @password, @child_name, @age, @problem, @current_lat, @current_lon, @on_off)", dbConnection);
            cmd.Parameters.AddWithValue("@child_ip", child_ip);
            cmd.Parameters.AddWithValue("@email", email);
            cmd.Parameters.AddWithValue("@password", password);
            cmd.Parameters.AddWithValue("@child_name", child_name);
            cmd.Parameters.AddWithValue("@age", age);
            cmd.Parameters.AddWithValue("@problem", problem);
            cmd.Parameters.AddWithValue("@current_lat", current_lat);
            cmd.Parameters.AddWithValue("@current_lon", current_lon);
            cmd.Parameters.AddWithValue("@on_off", on_off);
            cmd.ExecuteNonQuery();
            dbConnection.Close();
        }

        public DataTable ChildDetails()
        {

            DataTable childTable = new DataTable();
            childTable.Columns.Add("child_ip", typeof(String));
            childTable.Columns.Add("email", typeof(String));
            childTable.Columns.Add("password", typeof(String));
            childTable.Columns.Add("child_name", typeof(String));
            childTable.Columns.Add("age", typeof(int));
            childTable.Columns.Add("problem", typeof(String));
            childTable.Columns.Add("current_lat", typeof(Double));
            childTable.Columns.Add("current_lon", typeof(Double));
            childTable.Columns.Add("on_off", typeof(String));

            if (dbConnection.State.ToString() == "Closed")
            {
                dbConnection.Open();
            }

            string query = "SELECT child_ip, email, password, child_name, age, problem, current_lat, current_lon, on_off FROM Child;";
            SqlCommand command = new SqlCommand(query, dbConnection);
            SqlDataReader reader = command.ExecuteReader();

            if (reader.HasRows)
            {
                while (reader.Read())
                {
                    childTable.Rows.Add(reader["child_ip"], reader["email"], reader["password"],
                                        reader["child_name"], reader["age"], reader["problem"],
                                        reader["current_lat"], reader["current_lon"], reader["on_off"]);
                }
            }

            reader.Close();
            dbConnection.Close();
            return childTable;
        }

        public void DeleteChild(string email, string child_name)
        {

            if (dbConnection.State.ToString() == "Closed")
            {
                dbConnection.Open();
            }

            SqlCommand cmd = new SqlCommand("DELETE FROM Child WHERE email = @email AND child_name = @child_name", dbConnection);
            cmd.Parameters.AddWithValue("@email", email);
            cmd.Parameters.AddWithValue("@child_name", child_name);
            cmd.ExecuteNonQuery();
            dbConnection.Close();
        }

        public void EditChild(string email, string old_child, string new_child, int age, string problem)
        {

            if (dbConnection.State.ToString() == "Closed")
            {
                dbConnection.Open();
            }

            SqlCommand cmd = new SqlCommand("UPDATE Child SET child_name = @new_child, age = @age, problem = @problem WHERE email = @email AND child_name = @old_child", dbConnection);
            cmd.Parameters.AddWithValue("@email", email);
            cmd.Parameters.AddWithValue("@old_child", old_child);
            cmd.Parameters.AddWithValue("@new_child", new_child);
            cmd.Parameters.AddWithValue("@age", age);
            cmd.Parameters.AddWithValue("@problem", problem);
            cmd.ExecuteNonQuery();
            dbConnection.Close();
        }

        public void SelectChild(string child_ip, string email, string child_name)
        {

            if (dbConnection.State.ToString() == "Closed")
            {
                dbConnection.Open();
            }

            SqlCommand cmd = new SqlCommand("UPDATE Child SET child_ip = @child_ip WHERE email = @email AND child_name = @child_name", dbConnection);
            cmd.Parameters.AddWithValue("@child_ip", child_ip);
            cmd.Parameters.AddWithValue("@email", email);
            cmd.Parameters.AddWithValue("@child_name", child_name);
            cmd.ExecuteNonQuery();
            dbConnection.Close();
        }

         public void ChildOnOff(string email, string child_name, string on_off)
        {

            if (dbConnection.State.ToString() == "Closed")
            {
                dbConnection.Open();
            }

            SqlCommand cmd = new SqlCommand("UPDATE Child SET on_off = @on_off WHERE email = @email AND child_name = @child_name", dbConnection);
            cmd.Parameters.AddWithValue("@email", email);
            cmd.Parameters.AddWithValue("@child_name", child_name);
            cmd.Parameters.AddWithValue("@on_off", on_off);
            cmd.ExecuteNonQuery();
            dbConnection.Close();
        }

         public void ChildLocation(string email, string child_name, double current_lat, double current_lon)
         {

             if (dbConnection.State.ToString() == "Closed")
             {
                 dbConnection.Open();
             }

             SqlCommand cmd = new SqlCommand("UPDATE Child SET current_lat = @current_lat, current_lon = @current_lon WHERE email = @email AND child_name = @child_name", dbConnection);
             cmd.Parameters.AddWithValue("@email", email);
             cmd.Parameters.AddWithValue("@child_name", child_name);
             cmd.Parameters.AddWithValue("@current_lat", current_lat);
             cmd.Parameters.AddWithValue("@current_lon", current_lon);
             cmd.ExecuteNonQuery();
             dbConnection.Close();
         }

         public void CreateRecord(string record_date, string email, string child_name, string location_name, double lat, double lon)
         {

             if (dbConnection.State.ToString() == "Closed")
             {
                 dbConnection.Open();
             }
             SqlCommand cmd = new SqlCommand("INSERT INTO Record (record_date, email, child_name, location_name, lat, lon) VALUES(@record_date, @email, @child_name, @location_name, @lat, @lon)", dbConnection);
             cmd.Parameters.AddWithValue("@record_date", record_date);
             cmd.Parameters.AddWithValue("@email", email);
             cmd.Parameters.AddWithValue("@child_name", child_name);
             cmd.Parameters.AddWithValue("@location_name", location_name);
             cmd.Parameters.AddWithValue("@lat", lat);
             cmd.Parameters.AddWithValue("@lon", lon);
             cmd.ExecuteNonQuery();
             dbConnection.Close();
         }

         public void DeleteRecord(string record_date, string email, string child_name, string location_name)
         {

             if (dbConnection.State.ToString() == "Closed")
             {
                 dbConnection.Open();
             }
             SqlCommand cmd = new SqlCommand("DELETE FROM Record WHERE record_date = @record_date AND email = @email AND child_name = @child_name AND location_name = @location_name", dbConnection);
             cmd.Parameters.AddWithValue("@record_date", record_date);
             cmd.Parameters.AddWithValue("@email", email);
             cmd.Parameters.AddWithValue("@child_name", child_name);
             cmd.Parameters.AddWithValue("@location_name", location_name);
             cmd.ExecuteNonQuery();
             dbConnection.Close();
         }

         public DataTable RecordDetails()
         {

             DataTable recordTable = new DataTable();
             recordTable.Columns.Add("record_date", typeof(String));
             recordTable.Columns.Add("email", typeof(String));
             recordTable.Columns.Add("child_name", typeof(String));
             recordTable.Columns.Add("location_name", typeof(String));
             recordTable.Columns.Add("lat", typeof(Double));
             recordTable.Columns.Add("lon", typeof(Double));

             if (dbConnection.State.ToString() == "Closed")
             {
                 dbConnection.Open();
             }

             string query = "SELECT record_date, email, child_name, location_name, lat, lon FROM Record;";
             SqlCommand command = new SqlCommand(query, dbConnection);
             SqlDataReader reader = command.ExecuteReader();

             if (reader.HasRows)
             {
                 while (reader.Read())
                 {
                     recordTable.Rows.Add(reader["record_date"], reader["email"],reader["child_name"], reader["location_name"],reader["lat"], reader["lon"]);
                 }
             }

             reader.Close();
             dbConnection.Close();
             return recordTable;
         }
    }
}