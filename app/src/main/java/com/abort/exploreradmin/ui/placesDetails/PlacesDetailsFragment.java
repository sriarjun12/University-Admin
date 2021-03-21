package com.abort.exploreradmin.ui.placesDetails;

import androidx.lifecycle.ViewModelProvider;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.abort.exploreradmin.Common.Common;
import com.abort.exploreradmin.Eventbus.StatesClick;
import com.abort.exploreradmin.Eventbus.SummaryClick;
import com.abort.exploreradmin.Model.PlacesModel;
import com.abort.exploreradmin.Model.SpotsModel;
import com.abort.exploreradmin.Model.SummaryModel;
import com.abort.exploreradmin.R;
import com.bumptech.glide.Glide;
import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
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

public class PlacesDetailsFragment extends Fragment {

    private PlacesDetailsViewModel mViewModel;
    Unbinder unbinder;
    @BindView(R.id.image_sliders)
    ImageSlider mainSlider;
    @BindView(R.id.placename)
    EditText placename;
    @BindView(R.id.discription)
    EditText discription;
    @BindView(R.id.temperature)
    EditText temperature;
    @BindView(R.id.placescover)
    EditText placescover;
    @BindView(R.id.packageprice)
    EditText packageprice;
    @BindView(R.id.bookbtn)
    Button bookbtn;
    ImageView image_sliders;
    AlertDialog dialog;
    private Uri imageUrl = null;
    final List<SlideModel> remoteImages = new ArrayList<>();

    private static final int PICK_IMAGE_REQUEST = 1234;
    FirebaseStorage storage;
    StorageReference storageReference;

    public static PlacesDetailsFragment newInstance() {
        return new PlacesDetailsFragment();
    }

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

    private void showAddDialog() {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(getContext());
        builder.setTitle("Update Info");
        builder.setMessage("Please Fill information");
        View itemView = LayoutInflater.from(getContext()).inflate(R.layout.add_slider_image, null);

        image_sliders = (ImageView) itemView.findViewById(R.id.img_id);

        //set data
        Glide.with(getContext()).load(R.drawable.upload_img).into(image_sliders);

        //set event
        image_sliders.setOnClickListener(view -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
        });
        builder.setNegativeButton("CANCEL", (dialogInterface, i) -> dialog.dismiss());
        builder.setPositiveButton("UPDATE", (dialogInterface, i) -> {
            Map<String, Object> updateData = new HashMap<>();
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
                        SpotsModel spotsModel = new SpotsModel();
                        spotsModel.setImage(uri.toString());
                        Common.placeSelected.getSpots().add(spotsModel);
                        Updatecategory();
                    });

                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                        double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                        dialog.setMessage(new StringBuilder("Uploading : ").append(progress).append("%"));
                    }
                });
            }
        });
        builder.setView(itemView);
        androidx.appcompat.app.AlertDialog dialog = builder.create();
        builder.show();
    }

    private void Updatecategory() {
        Map<String, Object> updateData = new HashMap<>();
        updateData.put("spots", Common.placeSelected.getSpots());
        FirebaseDatabase.getInstance()
                .getReference(Common.STATES)
                .child(Common.stateSelected.getKey())
                .child("places")
                .child(String.valueOf(Common.placeSelected.getPosition()))
                .updateChildren(updateData)
                .addOnFailureListener(e -> Toast.makeText(getContext(), "" + e.getMessage(), Toast.LENGTH_SHORT).show())
                .addOnCompleteListener(task -> {
                    mainSlider.setImageList(remoteImages);
                });
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.places_details_fragment, container, false);

        unbinder = ButterKnife.bind(this, root);
        setHasOptionsMenu(true);
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        dialog = new SpotsDialog.Builder().setContext(getContext()).setCancelable(false).build();

        if (Common.placeSelected.getSpots() != null) {
            Log.d("hello","check");
            for (SpotsModel spotsModel : Common.placeSelected.getSpots()) {
                remoteImages.add(new SlideModel(spotsModel.getImage(), "", ScaleTypes.CENTER_CROP));
            }
            mainSlider.setImageList(remoteImages);
        }

        else{
            Common.placeSelected.setSpots(new ArrayList<>());
        }
        if (Common.placeSelected.getName() != null)
            placename.setText(Common.placeSelected.getName());
        if (Common.placeSelected.getDescription() != null)
            discription.setText(Common.placeSelected.getDescription());
        if (String.valueOf(Common.placeSelected.getTempurature()) != null)
            temperature.setText(String.valueOf(Common.placeSelected.getTempurature()));
        if (Common.placeSelected.getSpotscover() != null)
            placescover.setText(Common.placeSelected.getSpotscover());
        if (String.valueOf(Common.placeSelected.getPrice()) != null)
            packageprice.setText(String.valueOf(Common.placeSelected.getPrice()));

        bookbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PlacesModel placesModel = new PlacesModel();
                placesModel.setName(placename.getText().toString());
                placesModel.setDescription(discription.getText().toString());
                placesModel.setPrice(Integer.parseInt(packageprice.getText().toString()));
                placesModel.setTempurature(Integer.parseInt(temperature.getText().toString()));
                placesModel.setSpots(Common.placeSelected.getSpots());
                placesModel.setImage(Common.placeSelected.getImage());
                placesModel.setPosition(Common.placeSelected.getPosition());
                placesModel.setSpotscover(placescover.getText().toString());
                FirebaseDatabase.getInstance().getReference(Common.STATES)
                        .child(Common.stateSelected.getKey())
                        .child("places")
                        .child(String.valueOf(Common.placeSelected.getPosition()))
                        .setValue(placesModel);
//                EventBus.getDefault().postSticky(new SummaryClick(true, Common.currentSymaryModel));
            }
        });
        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(PlacesDetailsViewModel.class);
        // TODO: Use the ViewModel
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            if (data != null && data.getData() != null) {
                imageUrl = data.getData();
                remoteImages.add(new SlideModel(imageUrl.toString(), "", ScaleTypes.CENTER_CROP));
                mainSlider.setImageList(remoteImages);
                image_sliders.setImageURI(imageUrl);
            }
        }
    }

}