package com.abort.exploreradmin.ui.gallery;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.abort.exploreradmin.CallBacks.IStatesCallBackListener;
import com.abort.exploreradmin.Common.Common;
import com.abort.exploreradmin.Model.StatesModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class GalleryViewModel extends ViewModel implements IStatesCallBackListener {
    private  MutableLiveData<List<StatesModel>> stateMutableList;
    private IStatesCallBackListener statesCallBackListener;
    private MutableLiveData<String> messageError = new MutableLiveData<>();
    public GalleryViewModel() {
        statesCallBackListener = this;
    }
    public MutableLiveData<String> getMessageError() {
        return messageError;
    }
    public MutableLiveData<List<StatesModel>> getCategoryListMutable() {
        if (stateMutableList == null)
        {
            stateMutableList = new MutableLiveData<>();
            messageError = new MutableLiveData<>();
//            loadCategories();
        }
        return stateMutableList;
    }

    public void loadCategories() {

        List<StatesModel> tempList = new ArrayList<>();
        DatabaseReference categoryRef = FirebaseDatabase.getInstance()
                .getReference(Common.STATES);
        categoryRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot itemSnapShot:dataSnapshot.getChildren())
                {
                    StatesModel statesModel = itemSnapShot.getValue(StatesModel.class);
                    statesModel.setKey(itemSnapShot.getKey());
                    tempList.add(statesModel);
                }
                statesCallBackListener.onCategoryLoadSuccess(tempList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                statesCallBackListener.onCategoryLoadFailed(error.getMessage());
            }
        });
    }

    @Override
    public void onCategoryLoadSuccess(List<StatesModel> statesModelList) {
        stateMutableList.setValue(statesModelList);
    }
    @Override
    public void onCategoryLoadFailed(String message) {
    }
}