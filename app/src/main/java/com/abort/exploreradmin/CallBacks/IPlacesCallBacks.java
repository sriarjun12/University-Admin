package com.abort.exploreradmin.CallBacks;

import com.abort.exploreradmin.Model.PlacesModel;
import com.abort.exploreradmin.Model.StatesModel;

import java.util.List;

public interface IPlacesCallBacks {
    void onCategoryLoadSuccess(List<PlacesModel> placesModelList);
    void onCategoryLoadFailed(String message);
}
