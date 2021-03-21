package com.abort.exploreradmin.ui.places;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.abort.exploreradmin.CallBacks.IPlacesCallBacks;
import com.abort.exploreradmin.Common.Common;
import com.abort.exploreradmin.Model.PlacesModel;

import java.util.List;

public class PlacesViewModel extends ViewModel implements IPlacesCallBacks {

    private MutableLiveData<List<PlacesModel>> placesMutableList;
    private IPlacesCallBacks placesCallBacksLister;
    private MutableLiveData<String> messageError = new MutableLiveData<>();
    public PlacesViewModel() {
        placesCallBacksLister = this;
    }
    public MutableLiveData<String> getMessageError() {
        return messageError;
    }
    public MutableLiveData<List<PlacesModel>> getCategoryListMutable() {
        if (placesMutableList == null)
        {

            placesMutableList = new MutableLiveData<>();
            messageError = new MutableLiveData<>();
//            loadCategories();
        }

        return placesMutableList;
    }

    public void loadPlaces() {
        placesCallBacksLister.onCategoryLoadSuccess(Common.stateSelected.getPlaces());

    }

    @Override
    public void onCategoryLoadSuccess(List<PlacesModel> placesModelList) {
        placesMutableList.setValue(placesModelList);
    }

    @Override
    public void onCategoryLoadFailed(String message) {
    }



}