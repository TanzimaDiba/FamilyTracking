using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Data;

namespace WebApplication
{
    public interface IServiceAPI
    {
        int CreateNewAccount(string parent_ip, string email, string password);
        DataTable ParentDetails();
        string UserAuthentication(string ip, string email, string password);
        void CreateChildAccount(string child_ip, string email, string password, string child_name, int age, string problem, double current_lat, double current_lon, string on_off);
        DataTable ChildDetails();
        void DeleteChild(string email, string child_name);
        void EditChild(string email, string old_child, string new_child, int age, string problem);
        void SelectChild(string child_ip, string email, string child_name);
        void ChildOnOff(string email, string child_name, string on_off);
        void ChildLocation(string email, string child_name, double current_lat, double current_lon);
        void CreateRecord(string record_date, string email, string child_name, string location_name, double lat, double lon);
        void DeleteRecord(string record_date, string email, string child_name, string location_name);
        DataTable RecordDetails();

    }
}