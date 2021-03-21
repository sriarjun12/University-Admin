package com.abort.exploreradmin.Eventbus;

import com.abort.exploreradmin.Model.PlacesModel;
import com.abort.exploreradmin.Model.SummaryModel;

public class SummaryClick {

    private  boolean success;
    private SummaryModel summaryModel;

    public SummaryClick(boolean success, SummaryModel summaryModel) {
        this.success = success;
        this.summaryModel = summaryModel;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public SummaryModel getSummaryModel() {
        return summaryModel;
    }

    public void setSummaryModel(SummaryModel summaryModel) {
        this.summaryModel = summaryModel;
    }
}
