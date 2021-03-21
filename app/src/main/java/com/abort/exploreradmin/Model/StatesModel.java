package com.abort.exploreradmin.Model;

import java.util.List;

public class StatesModel {
    private  String name,image,key;
    List<PlacesModel> places;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public StatesModel() {
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

    public List<PlacesModel> getPlaces() {
        return places;
    }

    public void setPlaces(List<PlacesModel> places) {
        this.places = places;
    }
}
