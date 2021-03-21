package com.abort.exploreradmin.Model;

public class SummaryModel {
    private String name;
    private String discription;
    private String spotscover;
    private String userid;
    private String phone;
    private String date;
    private long price;
    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDiscription() {
        return discription;
    }

    public void setDiscription(String discription) {
        this.discription = discription;
    }

    public String getSpotscover() {
        return spotscover;
    }

    public void setSpotscover(String spotscover) {
        this.spotscover = spotscover;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public long getPrice() {
        return price;
    }

    public void setPrice(long price) {
        this.price = price;
    }
}
