package com.example.diba.applicationparentchild;

public class RecordTable {

    String record_date, email, child_name, location_name ;
    double lat, lon;

    public RecordTable(String record_date,String email,String child_name,String location_name,double lat,double lon) {
        super();
        this.record_date = record_date;
        this.email = email;
        this.child_name = child_name;
        this.location_name = location_name;
        this.lat = lat;
        this.lon = lon;
    }

    public RecordTable() {
        super();
        this.record_date = null;
        this.email = null;
        this.child_name = null;
        this.location_name = null;
        this.lat = 0;
        this.lon = 0;
    }

    public String getRecordDate() {
        return record_date;
    }

    public void setRecordDate(String record_date) {
        this.record_date = record_date;
    }

    public String getRecordEmail() {
        return email;
    }

    public void setRecordEmail(String email) {
        this.email = email;
    }

    public String getRecordChildName() {return child_name;}

    public void setRecordChildName(String child_name) {
        this.child_name = child_name;
    }

    public String getLocationName() {
        return location_name;
    }

    public void setocationName(String location_name) {
        this.location_name = location_name;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLon() {
        return lon;
    }

    public void setLon(Double lon) {
        this.lon = lon;
    }

}


