package com.abort.exploreradmin.Eventbus;

import com.abort.exploreradmin.Model.StatesModel;

public class StatesClick {

    private  boolean success;
    private StatesModel statesModel;

    public StatesClick(boolean success, StatesModel categoryModel) {
        this.success = success;
        this.statesModel = categoryModel;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public StatesModel getStatesModel() {
        return statesModel;
    }

    public void setStatesModel(StatesModel statesModel) {
        this.statesModel = statesModel;
    }
}
