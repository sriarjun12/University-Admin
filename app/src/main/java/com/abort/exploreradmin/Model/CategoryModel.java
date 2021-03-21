package com.abort.exploreradmin.Model;

public class CategoryModel {
    private String name,image,type;

    public CategoryModel(String name, String image, String type) {
        this.name = name;
        this.image = image;
        this.type = type;
    }

    public CategoryModel() {
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
