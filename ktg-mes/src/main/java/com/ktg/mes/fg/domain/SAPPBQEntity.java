package com.ktg.mes.fg.domain;

import java.util.Date;

public class SAPPBQEntity {
    private String ID;
    private String Plant;
    private String Location;
    private String Room;
    private String PN;
    private String Batch;
    private float Quantity;
    private String CreateUser;
    private Date CreateDatetime;

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getPlant() {
        return Plant;
    }

    public void setPlant(String plant) {
        Plant = plant;
    }

    public String getLocation() {
        return Location;
    }

    public void setLocation(String location) {
        Location = location;
    }

    public String getRoom() {
        return Room;
    }

    public void setRoom(String room) {
        Room = room;
    }

    public String getPN() {
        return PN;
    }

    public void setPN(String PN) {
        this.PN = PN;
    }

    public String getBatch() {
        return Batch;
    }

    public void setBatch(String batch) {
        Batch = batch;
    }

    public float getQuantity() {
        return Quantity;
    }

    public void setQuantity(float quantity) {
        Quantity = quantity;
    }

    public String getCreateUser() {
        return CreateUser;
    }

    public void setCreateUser(String createUser) {
        CreateUser = createUser;
    }

    public Date getCreateDatetime() {
        return CreateDatetime;
    }

    public void setCreateDatetime(Date createDatetime) {
        CreateDatetime = createDatetime;
    }

    @Override
    public String toString() {
        return "SAPPBQEntity{" +
                "ID='" + ID + '\'' +
                ", Plant='" + Plant + '\'' +
                ", Location='" + Location + '\'' +
                ", Room='" + Room + '\'' +
                ", PN='" + PN + '\'' +
                ", Batch='" + Batch + '\'' +
                ", Quantity=" + Quantity +
                ", CreateUser='" + CreateUser + '\'' +
                ", CreateDatetime=" + CreateDatetime +
                '}';
    }
}


