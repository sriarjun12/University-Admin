package com.abort.exploreradmin.Model;

import java.util.List;

public class PlacesModel {
    private  String name,image,type,description,spotscover;
    List<SpotsModel> spots;
    private int position;
    private long price,tempurature;

    public List<SpotsModel> getSpots() {
        return spots;
    }

    public void setSpots(List<SpotsModel> spots) {
        this.spots = spots;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public long getPrice() {
        return price;
    }

    public void setPrice(long price) {
        this.price = price;
    }

    public long getTempurature() {
        return tempurature;
    }

    public void setTempurature(long tempurature) {
        this.tempurature = tempurature;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    public String getSpotscover() {
        return spotscover;
    }

    public void setSpotscover(String spotscover) {
        this.spotscover = spotscover;
    }





    public PlacesModel() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
