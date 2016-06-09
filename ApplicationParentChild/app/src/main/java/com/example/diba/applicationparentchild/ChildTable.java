package com.example.diba.applicationparentchild;

public class ChildTable {

    String child_ip, email, password, child_name, problem, on_off;
    int age;
    double current_lat, current_lon;

    public ChildTable(String child_ip, String email, String password, String child_name, int age, String problem, double current_lat, double current_lon, String on_off) {
        super();
        this.child_ip = child_ip;
        this.email = email;
        this.password = password;
        this.child_name = child_name;
        this.age = age;
        this.problem = problem;
        this.current_lat = current_lat;
        this.current_lon = current_lon;
        this.on_off = on_off;
    }

    public ChildTable() {
        super();
        this.child_ip = null;
        this.email = null;
        this.password = null;
        this.child_name = null;
        this.age = 0;
        this.problem = null;
        this.current_lat = 0;
        this.current_lon = 0;
        this.on_off = null;

    }

    public String getChildIp() {
        return child_ip;
    }

    public void setChildIp(String child_ip) {
        this.child_ip = child_ip;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getChildName() {return child_name;}

    public void setChildName(String child_name) {
        this.child_name = child_name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getProblem() {
        return problem;
    }

    public void setProblem(String problem) {
        this.problem = problem;
    }

    public Double getCurrentLat() {
        return current_lat;
    }

    public void setCurrentLat(Double current_lat) {
        this.current_lat = current_lat;
    }

    public Double getCurrentLon() {
        return current_lon;
    }

    public void setCurrentLon(Double current_lon) {
        this.current_lon = current_lon;
    }

    public String getOnOff() {
        return on_off;
    }

    public void setOnOff(String on_off) {
        this.on_off = on_off;
    }

}


