package com.abort.exploreradmin.ui.slideshow;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.abort.exploreradmin.Common.Common;
import com.abort.exploreradmin.Eventbus.SummaryClick;
import com.abort.exploreradmin.Model.SummaryModel;
import com.abort.exploreradmin.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.greenrobot.eventbus.EventBus;

public class SlideshowFragment extends Fragment {
    EditText silverDis,goldDis,platinumDis;
    EditText silverPrice,goldPrice,platinumPrice;
    EditText silverSpots,goldSpots,platinumSpots;
    Button silverBut,goldBut,platinumBut;
    private SlideshowViewModel slideshowViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        slideshowViewModel =
                new ViewModelProvider(this).get(SlideshowViewModel.class);
        View root = inflater.inflate(R.layout.fragment_slideshow, container, false);
        silverDis=root.findViewById(R.id.package_discribtion1);
        goldDis=root.findViewById(R.id.package_discribtion2);
        platinumDis=root.findViewById(R.id.package_discribtion3);

        silverPrice=root.findViewById(R.id.price_silver);
        goldPrice=root.findViewById(R.id.price_gold);
        platinumPrice=root.findViewById(R.id.price_platinum);

        silverSpots=root.findViewById(R.id.spots_silver);
        goldSpots=root.findViewById(R.id.spots_gold);
        platinumSpots=root.findViewById(R.id.spots_platinum);

        silverBut=root.findViewById(R.id.btnsilver);
        goldBut=root.findViewById(R.id.btngold);
        platinumBut=root.findViewById(R.id.btnplatinum);

        FirebaseDatabase.getInstance().getReference(Common.PACKAGE)
                .child("silver").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                SummaryModel summaryModel =snapshot.getValue(SummaryModel.class);
                silverDis.setText(summaryModel.getDiscription());
                silverPrice.setText(String.valueOf(summaryModel.getPrice()));
                silverSpots.setText(summaryModel.getSpotscover());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        FirebaseDatabase.getInstance().getReference(Common.PACKAGE)
                .child("gold").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                SummaryModel summaryModel =snapshot.getValue(SummaryModel.class);
                goldDis.setText(summaryModel.getDiscription());
                goldPrice.setText(String.valueOf(summaryModel.getPrice()));
                goldSpots.setText(summaryModel.getSpotscover());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        FirebaseDatabase.getInstance().getReference(Common.PACKAGE)
                .child("platinum").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                SummaryModel summaryModel =snapshot.getValue(SummaryModel.class);
                platinumDis.setText(summaryModel.getDiscription());
                platinumPrice.setText(String.valueOf(summaryModel.getPrice()));
                platinumSpots.setText(summaryModel.getSpotscover());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        silverBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SummaryModel summaryModel=new SummaryModel();
                summaryModel.setDiscription(silverDis.getText().toString());
                summaryModel.setPrice(Long.parseLong(silverPrice.getText().toString()));
                summaryModel.setSpotscover(silverSpots.getText().toString());
                summaryModel.setName("Silver Package");
                FirebaseDatabase.getInstance().getReference(Common.PACKAGE)
                        .child("silver").setValue(summaryModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(getContext(), "Update Success", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        goldBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SummaryModel summaryModel=new SummaryModel();
                summaryModel.setDiscription(goldDis.getText().toString());
                summaryModel.setPrice(Long.parseLong(goldPrice.getText().toString()));
                summaryModel.setSpotscover(goldSpots.getText().toString());
                summaryModel.setName("Gold Package");
                FirebaseDatabase.getInstance().getReference(Common.PACKAGE)
                        .child("gold").setValue(summaryModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(getContext(), "Update Success", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        platinumBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SummaryModel summaryModel=new SummaryModel();
                summaryModel.setDiscription(platinumDis.getText().toString());
                summaryModel.setPrice(Long.parseLong(platinumPrice.getText().toString()));
                summaryModel.setSpotscover(platinumSpots.getText().toString());
                summaryModel.setName("Platinum Package");
                FirebaseDatabase.getInstance().getReference(Common.PACKAGE)
                        .child("platinum").setValue(summaryModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(getContext(), "Update Success", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        return root;
    }

}