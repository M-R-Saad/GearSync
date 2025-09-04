package org.example.vehicle;

public class Service {

    private int sid;
    private int cid;
    private int wid;
    private String title;
    private String description;
    private String vehicleType;
    private double payment_amount;
    private String status;
    private String picture;
    private String created_at;
    private String updated_at;

    public Service(int sid, int cid, int wid, String title, String description, String vehicleType, double payment_amount, String status, String picture, String created_at, String updated_at) {
        this.sid = sid;
        this.cid = cid;
        this.wid = wid;
        this.title = title;
        this.description = description;
        this.vehicleType = vehicleType;
        this.payment_amount = payment_amount;
        this.status = status;
        this.picture = picture;
        this.created_at = created_at;
        this.updated_at = updated_at;
    }

    public int getSid() {
        return sid;
    }

    public void setSid(int sid) {
        this.sid = sid;
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    public double getPayment_amount() {
        return payment_amount;
    }

    public void setPayment_amount(double payment_amount) {
        this.payment_amount = payment_amount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    @Override
    public String toString() {
        return "Service{" +
                "sid=" + sid +
                ", cid=" + cid +
                ", wid=" + wid +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", vehicleType='" + vehicleType + '\'' +
                ", payment_amount=" + payment_amount +
                ", status='" + status + '\'' +
                ", picture='" + picture + '\'' +
                ", created_at='" + created_at + '\'' +
                ", updated_at='" + updated_at + '\'' +
                '}';
    }
}
