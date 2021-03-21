package com.abort.exploreradmin.Eventbus;

import com.abort.exploreradmin.Model.CategoryModel;

public class CategoryHomeClick {

    private  boolean success;
    private CategoryModel categoryModel;

    public CategoryHomeClick(boolean success, CategoryModel categoryModel) {
        this.success = success;
        this.categoryModel = categoryModel;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public CategoryModel getCategoryModel() {
        return categoryModel;
    }

    public void setCategoryModel(CategoryModel categoryModel) {
        this.categoryModel = categoryModel;
    }
}