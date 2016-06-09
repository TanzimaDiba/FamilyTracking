package com.example.diba.applicationparentchild;

public class ParentTable {

    String parent_ip, email, password;

    public ParentTable(String parent_ip, String email, String password) {
        super();
        this.parent_ip = parent_ip;
        this.email = email;
        this.password = password;
    }

    public ParentTable() {
        super();
        this.parent_ip = null;
        this.email = null;
        this.password = null;

    }

    public String getParentIp() {
        return parent_ip;
    }

    public void setParentIp(String parent_ip) {
        this.parent_ip = parent_ip;
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

}

