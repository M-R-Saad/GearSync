package org.example.vehicle;

public class Worker {
    private int wid;
    private String username;
    private String fullname;
    private String email;
    private String status;
    private String phone;
    private String picture;
    private String created_at;

    public Worker(int wid, String username, String fullname, String email, String status, String phone, String picture, String created_at) {
        this.wid = wid;
        this.username = username;
        this.fullname = fullname;
        this.email = email;
        this.status = status;
        this.phone = phone;
        this.picture = picture;
        this.created_at = created_at;
    }

    @Override
    public String toString() {
        return "Worker{" +
                "wid=" + wid +
                ", username='" + username + '\'' +
                ", fullName='" + fullname + '\'' +
                ", email='" + email + '\'' +
                ", status='" + status + '\'' +
                ", phone='" + phone + '\'' +
                ", picture='" + picture + '\'' +
                ", created_at='" + created_at + '\'' +
                '}';
    }

    public int getWid() {
        return wid;
    }

    public void setWid(int wid) {
        this.wid = wid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullName) {
        this.fullname = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
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
}
