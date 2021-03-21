package com.abort.exploreradmin.ui.gallery;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.abort.exploreradmin.Adapter.StatesAdapter;
import com.abort.exploreradmin.Common.Common;
import com.abort.exploreradmin.Common.MySwipeHelper;
import com.abort.exploreradmin.Common.SpacesItemDecoration;
import com.abort.exploreradmin.Model.StatesModel;
import com.abort.exploreradmin.R;
import com.bumptech.glide.Glide;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.greenrobot.eventbus.EventBus;

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

public class GalleryFragment extends Fragment {

    private GalleryViewModel mViewModel;
    Unbinder unbinder;
    @BindView(R.id.recycler_category_home)
    RecyclerView recycler_home_category;
    StatesAdapter statesAdapter;
    List<StatesModel> statesModels;
    AlertDialog dialog;
    private Uri imageUrl = null;
    ImageView img_id;

    FirebaseStorage storage;
    StorageReference storageReference;


    private static final int PICK_IMAGE_REQUEST = 1234;

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.home, menu);
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.action_create) {
            showAddDialog();
        }
        return super.onOptionsItemSelected(item);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        mViewModel =
                new ViewModelProvider(this).get(GalleryViewModel.class);
        View root = inflater.inflate(R.layout.fragment_gallery, container, false);
        unbinder = ButterKnife.bind(this, root);
        mViewModel.getCategoryListMutable().observe(getViewLifecycleOwner(), categoryModelList -> {
            statesAdapter = new StatesAdapter(getContext(), categoryModelList);
            statesModels = categoryModelList;
            recycler_home_category.setAdapter(statesAdapter);
        });
        initView();
        return root;
    }

    private void initView() {
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        dialog = new SpotsDialog.Builder().setContext(getContext()).setCancelable(false).build();
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 2);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        recycler_home_category.setLayoutManager(layoutManager);
        recycler_home_category.addItemDecoration(new SpacesItemDecoration(8));

        mViewModel.loadCategories();
        MySwipeHelper swipeHelper = new MySwipeHelper(getContext(), recycler_home_category, 150) {
            @Override
            public void instantiateMyButton(RecyclerView.ViewHolder viewHolder, List<MyButton> buf) {


                buf.add(new MyButton(getContext(), "Update", 35, 0, Color.parseColor("#88c057"),
                        pos -> {
                            Common.stateSelected = statesModels.get(pos);
                            showDialog(pos, statesModels.get(pos));
                        }));
            }
        };
        setHasOptionsMenu(true);
    }

    private void showAddDialog() {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(getContext());
        builder.setTitle("Create State");
        builder.setMessage("Please Fill information");
        View itemView = LayoutInflater.from(getContext()).inflate(R.layout.outside_update_layout, null);
        EditText name_id = (EditText) itemView.findViewById(R.id.name_id);
        img_id = (ImageView) itemView.findViewById(R.id.img_id);

        //set data
        Glide.with(getContext()).load(R.drawable.upload_img).into(img_id);

        //set event
        img_id.setOnClickListener(view -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
        });
        builder.setNegativeButton("CANCEL", (dialogInterface, i) -> dialog.dismiss());
        builder.setPositiveButton("CREATE", (dialogInterface, i) -> {
            if (TextUtils.isEmpty(name_id.getText().toString())) {

                return;
            }
            StatesModel statesModel = new StatesModel();
            statesModel.setName(name_id.getText().toString());
            statesModel.setKey(name_id.getText().toString().replace(" ", "").toLowerCase());
            statesModel.setPlaces(new ArrayList<>());// Create empty list for Food List


            if (imageUrl != null) {
                dialog.setMessage("Uploading ...");
                dialog.show();

                String unique_name = UUID.randomUUID().toString();
                StorageReference imageFloder = storageReference.child("image/" + unique_name);
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

                            Toast.makeText(getContext(), "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }).addOnCompleteListener(task -> {
                    dialog.dismiss();
                    imageFloder.getDownloadUrl().addOnSuccessListener(uri -> {
                        statesModel.setImage(uri.toString());
                        addState(statesModel);
                    });

                }).addOnProgressListener(taskSnapshot -> {
                    int progress = (int) (100 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());

                    dialog.setMessage(new StringBuilder("Uploading : ").append(progress).append("%"));

                });
            } else {
                Toast.makeText(getContext(), "Select Image", Toast.LENGTH_SHORT).show();
                return;
                //addCategory(categoryModel);
            }
        });
        builder.setView(itemView);
        androidx.appcompat.app.AlertDialog dialog = builder.create();
        builder.show();
    }

    private void addState(StatesModel statesModel) {
        FirebaseDatabase.getInstance()
                .getReference(Common.STATES)
                .child(statesModel.getKey())
                .setValue(statesModel)
                .addOnFailureListener(e -> Toast.makeText(getContext(), "" + e.getMessage(), Toast.LENGTH_SHORT).show())
                .addOnCompleteListener(task -> {
                    mViewModel.loadCategories();
                });
    }

    private void showDialog(int position, StatesModel statesModel) {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(getContext());
        builder.setTitle("Update Info");
        builder.setMessage("Please Fill information");
        View itemView = LayoutInflater.from(getContext()).inflate(R.layout.outside_update_layout, null);
        EditText name_id = (EditText) itemView.findViewById(R.id.name_id);
        img_id = (ImageView) itemView.findViewById(R.id.img_id);
        name_id.setText(statesModel.getName());
        //set data
        Glide.with(getContext()).load(statesModel.getImage()).into(img_id);

        //set event
        img_id.setOnClickListener(view -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
        });
        builder.setNegativeButton("CANCEL", (dialogInterface, i) -> dialog.dismiss());
        builder.setPositiveButton("UPDATE", (dialogInterface, i) -> {
            Map<String, Object> updateData = new HashMap<>();
            updateData.put("name", name_id.getText().toString());
            if (TextUtils.isEmpty(name_id.getText().toString())) {

                return;
            }

            if (imageUrl != null) {
                dialog.setMessage("Uploading ...");
                dialog.show();
                String unique_name = UUID.randomUUID().toString();
                StorageReference imageFloder = storageReference.child("image/" + unique_name);
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
                            Toast.makeText(getContext(), "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }).addOnCompleteListener(task -> {
                    dialog.dismiss();
                    imageFloder.getDownloadUrl().addOnSuccessListener(uri -> {
                        updateData.put("image", uri.toString());
                        Updatecategory(updateData);
                    });

                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                        double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                        dialog.setMessage(new StringBuilder("Uploading : ").append(progress).append("%"));
                    }
                });
            } else {
                Updatecategory(updateData);
            }
        });
        builder.setView(itemView);
        androidx.appcompat.app.AlertDialog dialog = builder.create();
        builder.show();
    }

    private void Updatecategory(Map<String, Object> updateData) {
        FirebaseDatabase.getInstance()
                .getReference(Common.STATES)
                .child(Common.stateSelected.getKey())
                .updateChildren(updateData)
                .addOnFailureListener(e -> Toast.makeText(getContext(), "" + e.getMessage(), Toast.LENGTH_SHORT).show())
                .addOnCompleteListener(task -> {
                    mViewModel.loadCategories();
                });
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            if (data != null && data.getData() != null) {
                imageUrl = data.getData();
                img_id.setImageURI(imageUrl);
            }
        }
    }
}