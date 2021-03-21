package com.abort.exploreradmin.ui.places;

import androidx.lifecycle.ViewModelProvider;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.abort.exploreradmin.Adapter.PlacesAdapter;
import com.abort.exploreradmin.Common.Common;
import com.abort.exploreradmin.Common.MySwipeHelper;
import com.abort.exploreradmin.Common.SpacesItemDecoration;
import com.abort.exploreradmin.Model.PlacesModel;
import com.abort.exploreradmin.Model.StatesModel;
import com.abort.exploreradmin.R;
import com.bumptech.glide.Glide;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import dmax.dialog.SpotsDialog;

public class PlacesFragment extends Fragment {

    private PlacesViewModel mViewModel;
    Unbinder unbinder;
    @BindView(R.id.recycler_category_home)
    RecyclerView recycler_home_category;
    PlacesAdapter placesAdapter;
    AlertDialog dialog;
    private Uri imageUrl=null;
    ImageView img_id;
    List<PlacesModel> placesModels;
    FirebaseStorage storage;
    StorageReference storageReference;


    private static final int PICK_IMAGE_REQUEST = 1234;

    public static PlacesFragment newInstance() {
        return new PlacesFragment();
    }


    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.home,menu);
    }



    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(item.getItemId() == R.id.action_create)
        {
            showAddDialog();
        }
        return super.onOptionsItemSelected(item);
    }



    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mViewModel =
                new ViewModelProvider(this).get(PlacesViewModel.class);
        View root= inflater.inflate(R.layout.places_fragment, container, false);
        unbinder = ButterKnife.bind(this,root);

        mViewModel.getCategoryListMutable().observe(getViewLifecycleOwner(),categoryModelList -> {
            placesAdapter = new PlacesAdapter(getContext(),categoryModelList);
            recycler_home_category.setAdapter(placesAdapter);
            placesModels=categoryModelList;
        });

        initView();
        return root;
    }

    private void initView() {
        storage=FirebaseStorage.getInstance();
        storageReference=storage.getReference();
        dialog = new SpotsDialog.Builder().setContext(getContext()).setCancelable(false).build();
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(),2);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        recycler_home_category.setLayoutManager(layoutManager);
        recycler_home_category.addItemDecoration(new SpacesItemDecoration(8));
        mViewModel.loadPlaces();
        MySwipeHelper swipeHelper=new MySwipeHelper(getContext(),recycler_home_category,150) {
            @Override
            public void instantiateMyButton(RecyclerView.ViewHolder viewHolder, List<MyButton> buf) {
                buf.add(new MyButton(getContext(),"Update",35,0, Color.parseColor("#88c057"),
                        pos ->{
                            placesModels.get(pos).setPosition(pos);
                            Common.placeSelected=placesModels.get(pos);
                            showDialog(pos,placesModels.get(pos));
                        }));
            }
        };

        setHasOptionsMenu(true);
    }

    private void showDialog(int pos, PlacesModel placesModel) {
        androidx.appcompat.app.AlertDialog.Builder builder=new androidx.appcompat.app.AlertDialog.Builder(getContext());
        builder.setTitle("Update Info");
        builder.setMessage("Please Fill information");
        View itemView =LayoutInflater.from(getContext()).inflate(R.layout.outside_update_layout,null);
        EditText name_id=(EditText) itemView.findViewById(R.id.name_id);
        img_id=(ImageView)itemView.findViewById(R.id.img_id);
        name_id.setText(placesModel.getName());
        //set data
        if(placesModel.getImage()!=null)
            Glide.with(getContext()).load(placesModel.getImage()).into(img_id);
        else
            Glide.with(getContext()).load(R.drawable.upload_img).into(img_id);
        //set event
        img_id.setOnClickListener(view->{
            Intent intent=new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent,"Select Picture"),PICK_IMAGE_REQUEST);
        });
        builder.setNegativeButton("CANCEL", (dialogInterface, i) -> dialog.dismiss());
        builder.setPositiveButton("UPDATE", (dialogInterface, i) -> {
            Map<String,Object> updateData=new HashMap<>();
            updateData.put("name",name_id.getText().toString());
            if(TextUtils.isEmpty(name_id.getText().toString())){

                return;
            }

            if(imageUrl!=null){
                dialog.setMessage("Uploading ...");
                dialog.show();
                String unique_name= UUID.randomUUID().toString();
                StorageReference imageFloder=storageReference.child("image/"+unique_name);
                Bitmap bmp = null;
                try {
                    bmp = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imageUrl);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bmp.compress(Bitmap.CompressFormat.JPEG, 60, baos);
                byte[] data = baos.toByteArray();

                imageFloder.putBytes(data)
                        .addOnFailureListener(e -> {
                            dialog.dismiss();
                            Toast.makeText(getContext(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }).addOnCompleteListener(task -> {
                    dialog.dismiss();
                    imageFloder.getDownloadUrl().addOnSuccessListener(uri -> {
                        updateData.put("image",uri.toString());
                        Updatecategory(pos,updateData);
                    });

                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                        double progress=(100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                        dialog.setMessage(new StringBuilder("Uploading : ").append(progress).append("%"));
                    }
                });
            }
            else{
                Updatecategory(pos,updateData);
            }
        });
        builder.setView(itemView);
        androidx.appcompat.app.AlertDialog dialog=builder.create();
        builder.show();
    }
    private void Updatecategory(int pos,Map<String, Object> updateData) {
        FirebaseDatabase.getInstance()
                .getReference(Common.STATES)
                .child(Common.stateSelected.getKey())
                .child("places")
                .child(String.valueOf(pos))
                .updateChildren(updateData)
                .addOnFailureListener(e -> Toast.makeText(getContext(), ""+e.getMessage(), Toast.LENGTH_SHORT).show())
                .addOnCompleteListener(task -> {
                    mViewModel.loadPlaces();
                });
    }
    private void showAddDialog(){
        androidx.appcompat.app.AlertDialog.Builder builder=new androidx.appcompat.app.AlertDialog.Builder(getContext());
        builder.setTitle("Create Place");
        builder.setMessage("Please Fill information");
        View itemView =LayoutInflater.from(getContext()).inflate(R.layout.outside_update_layout,null);
        EditText name_id=(EditText) itemView.findViewById(R.id.name_id);
        img_id=(ImageView)itemView.findViewById(R.id.img_id);

        //set data
        Glide.with(getContext()).load(R.drawable.upload_img).into(img_id);

        //set event
        img_id.setOnClickListener(view->{
            Intent intent=new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent,"Select Picture"),PICK_IMAGE_REQUEST);
        });
        builder.setNegativeButton("CANCEL", (dialogInterface, i) -> dialog.dismiss());
        builder.setPositiveButton("CREATE", (dialogInterface, i) -> {
            if(TextUtils.isEmpty(name_id.getText().toString())){

                return;
            }
            PlacesModel placesModel = new PlacesModel();
            placesModel.setName(name_id.getText().toString());

            // Create empty list for Food List


            if(imageUrl!=null){
                dialog.setMessage("Uploading ...");
                dialog.show();

                String unique_name= UUID.randomUUID().toString();
                StorageReference imageFloder=storageReference.child("image/"+unique_name);
                Bitmap bmp = null;
                try {
                    bmp = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imageUrl);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bmp.compress(Bitmap.CompressFormat.JPEG, 60, baos);
                byte[] data = baos.toByteArray();

                imageFloder.putBytes(data)
                        .addOnFailureListener(e -> {
                            dialog.dismiss();

                            Toast.makeText(getContext(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }).addOnCompleteListener(task -> {
                    dialog.dismiss();
                    imageFloder.getDownloadUrl().addOnSuccessListener(uri -> {
                        placesModel.setImage(uri.toString());
                        Common.stateSelected.setPlaces(new ArrayList<>());
                        Common.stateSelected.getPlaces().add(placesModel);
                        addState(Common.stateSelected.getPlaces());
                    });

                }).addOnProgressListener(taskSnapshot -> {
                    int progress=(int)(100*taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());

                    dialog.setMessage(new StringBuilder("Uploading : ").append(progress).append("%"));

                });
            }
            else{
                Toast.makeText(getContext(), "Select Image", Toast.LENGTH_SHORT).show();
                return;
                //addCategory(categoryModel);
            }
        });
        builder.setView(itemView);
        androidx.appcompat.app.AlertDialog dialog=builder.create();
        builder.show();
    }

    private void addState(List<PlacesModel> places) {
        Map<String,Object> updateData=new HashMap<>();
        updateData.put("places",places);
        FirebaseDatabase.getInstance()
                .getReference(Common.STATES)
                .child(Common.stateSelected.getKey())
                .updateChildren(updateData)
                .addOnFailureListener(e -> Toast.makeText(getContext(), ""+e.getMessage(), Toast.LENGTH_SHORT).show())
                .addOnCompleteListener(task -> {
                    mViewModel.loadPlaces();
                });
    }

//
    @Override
    public void onStart() {
        super.onStart();
        mViewModel.loadPlaces();
    }

    @Override
    public void onResume() {
        super.onResume();
        mViewModel.loadPlaces();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==PICK_IMAGE_REQUEST &&  resultCode== Activity.RESULT_OK){
            if (data != null && data.getData()!= null ){
                imageUrl=data.getData();
                img_id.setImageURI(imageUrl);
            }
        }
    }
}