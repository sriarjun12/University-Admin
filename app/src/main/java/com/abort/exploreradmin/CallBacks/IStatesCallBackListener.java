package com.abort.exploreradmin.CallBacks;

import com.abort.exploreradmin.Model.StatesModel;

import java.util.List;

public interface IStatesCallBackListener {
    void onCategoryLoadSuccess(List<StatesModel> statesModelList);
    void onCategoryLoadFailed(String message);
}
