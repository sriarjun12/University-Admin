package com.abort.exploreradmin.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.abort.exploreradmin.Adapter.CategoryHomeAdapter;
import com.abort.exploreradmin.Common.Common;
import com.abort.exploreradmin.Common.SpacesItemDecoration;
import com.abort.exploreradmin.R;
import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.interfaces.ItemClickListener;
import com.denzcoskun.imageslider.models.SlideModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class HomeFragment extends Fragment {
    private HomeViewModel mViewModel;
    Unbinder unbinder;
    @BindView(R.id.image_sliders)
    ImageSlider mainSlider;
    @BindView(R.id.recycler_category_home)
    RecyclerView recycler_home_category;
    CategoryHomeAdapter categoryHomeAdapter;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        mViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        unbinder = ButterKnife.bind(this,root);

        final List<SlideModel> remoteImages = new ArrayList<>();
        FirebaseDatabase.getInstance().getReference().child(Common.BANNER_REF)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot data:snapshot.getChildren())
                        {
                            remoteImages.add(new SlideModel(data.child("image").getValue().toString(),"", ScaleTypes.CENTER_CROP));
                        }
                        mainSlider.setImageList(remoteImages,ScaleTypes.FIT);

                        mainSlider.setItemClickListener(new ItemClickListener() {
                            @Override
                            public void onItemSelected(int i) {

                            }
                        });
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        mViewModel.getCategoryListMutable().observe(getViewLifecycleOwner(),categoryModelList -> {
            categoryHomeAdapter = new CategoryHomeAdapter(getContext(),categoryModelList);
            recycler_home_category.setAdapter(categoryHomeAdapter);
        });
        initView();
        return root;
    }
    private void initView() {
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(),2);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        recycler_home_category.setLayoutManager(layoutManager);
        recycler_home_category.addItemDecoration(new SpacesItemDecoration(8));
        mViewModel.loadCategories();
    }
    @Override
    public void onStart() {
        super.onStart();
        mViewModel.loadCategories();
    }
    @Override
    public void onResume() {
        super.onResume();
        mViewModel.loadCategories();
    }
}