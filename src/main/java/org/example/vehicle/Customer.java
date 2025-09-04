package org.example.vehicle;

public class Customer {
    private int cid;
    private String username;
    private String fullName;
    private String email;
    private String address;
    private String phone;
    private String picture;
    private String created_at;

    public Customer(int cid, String username, String fullName, String email, String address, String phone, String picture, String created_at) {
        this.cid = cid;
        this.username = username;
        this.fullName = fullName;
        this.email = email;
        this.address = address;
        this.phone = phone;
        this.picture = picture;
        this.created_at = created_at;
    }

    @Override
    public String toString() {
        return "Customer{" +
                "cid=" + cid +
                ", username='" + username + '\'' +
                ", fullName='" + fullName + '\'' +
                ", email='" + email + '\'' +
                ", address='" + address + '\'' +
                ", phone='" + phone + '\'' +
                ", picture='" + picture + '\'' +
                ", created_at='" + created_at + '\'' +
                '}';
    }

    public int getCid() {
        return cid;
    }

    public void setCid(int cid) {
        this.cid = cid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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
