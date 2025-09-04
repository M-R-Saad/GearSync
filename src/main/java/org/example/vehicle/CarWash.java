// File: CarWash.java
package org.example.vehicle;

public class CarWash {
    private int rid;
    private int cid;
    private int wid;
    private int cwid;
    private String planName;
    private String carLocation;
    private String carModel;
    private String dateTime;
    private String picture;
    private String createdAt;

    public CarWash(int rid, int cid, int wid, int cwid, String planName, String carLocation, String carModel, String dateTime, String picture, String createdAt) {
        this.rid = rid;
        this.cid = cid;
        this.wid = wid;
        this.cwid = cwid;
        this.planName = planName;
        this.carLocation = carLocation;
        this.carModel = carModel;
        this.dateTime = dateTime;
        this.picture = picture;
        this.createdAt = createdAt;
    }

    public CarWash(int rid, int cid, int wid, int cwid, String carLocation, String carModel, String dateTime, String picture, String createdAt) {
        this.rid = rid;
        this.cid = cid;
        this.wid = wid;
        this.cwid = cwid;
        this.carLocation = carLocation;
        this.carModel = carModel;
        this.dateTime = dateTime;
        this.picture = picture;
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "CarWash{" +
                "rid=" + rid +
                ", cid=" + cid +
                ", wid=" + wid +
                ", cwid=" + cwid +
                ", planName='" + planName + '\'' +
                ", carLocation='" + carLocation + '\'' +
                ", carModel='" + carModel + '\'' +
                ", dateTime='" + dateTime + '\'' +
                ", picture='" + picture + '\'' +
                ", createdAt='" + createdAt + '\'' +
                '}';
    }

    public int getRid() {
        return rid;
    }

    public void setRid(int rid) {
        this.rid = rid;
    }

    public int getCid() {
        return cid;
    }

    public void setCid(int cid) {
        this.cid = cid;
    }

    public int getWid() {
        return wid;
    }

    public void setWid(int wid) {
        this.wid = wid;
    }

    public int getCwid() {
        return cwid;
    }

    public void setCwid(int cwid) {
        this.cwid = cwid;
    }

    public String getPlanName() {
        return planName;
    }

    public void setPlanName(String planName) {
        this.planName = planName;
    }

    public String getCarLocation() {
        return carLocation;
    }

    public void setCarLocation(String carLocation) {
        this.carLocation = carLocation;
    }

    public String getCarModel() {
        return carModel;
    }

    public void setCarModel(String carModel) {
        this.carModel = carModel;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}
