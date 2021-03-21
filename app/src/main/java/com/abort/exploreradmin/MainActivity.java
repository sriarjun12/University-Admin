package com.abort.exploreradmin;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.abort.exploreradmin.Common.Common;
import com.abort.exploreradmin.Model.UserModel;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Arrays;
import java.util.List;

import dmax.dialog.SpotsDialog;

public class MainActivity extends AppCompatActivity {
    private static int APP_REQUEST_CODE=7171;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener listener;

    private DatabaseReference userRef;

    private FirebaseStorage storage;
    private StorageReference storageReference;
    private List<AuthUI.IdpConfig> providers;

    android.app.AlertDialog dialog;

    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(listener);
    }
    @Override
    protected void onStop() {
        if(listener!=null)
            firebaseAuth.removeAuthStateListener(listener);
        super.onStop();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }
    private void init() {
        storage=FirebaseStorage.getInstance();
        storageReference=storage.getReference();
        dialog=new SpotsDialog.Builder().setContext(this).setCancelable(false).build();
        providers = Arrays.asList(new AuthUI.IdpConfig.PhoneBuilder().build());
        userRef= FirebaseDatabase.getInstance().getReference(Common.ADMINREF);
        firebaseAuth=FirebaseAuth.getInstance();
        listener=firebaseAuthLocal ->{
            FirebaseUser user=firebaseAuthLocal.getCurrentUser();
            if(user !=null){
                checkUserFromFirebase(user);
            }
            else{
                PhoneLogin();
            }
        };
    }
    private void checkUserFromFirebase(FirebaseUser user){
        dialog.show();
        userRef.child(user.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            UserModel userModel = snapshot.getValue(UserModel.class);
                            goToHomeActivity(userModel);
                        }
                        else{
                            dialog.dismiss();
                            showRegisterDialog(user);
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        dialog.dismiss();
                        Toast.makeText(MainActivity.this, ""+error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private void showRegisterDialog(FirebaseUser user) {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(new ContextThemeWrapper(this,R.style.AlertDialogCustom));
        builder.setTitle("Register");
//        builder.setIcon(R.drawable.logo_explorer);

        builder.setCancelable(false);
        try {
            View itemView = LayoutInflater.from(this).inflate(R.layout.layout_register, null);
            EditText edt_name = (EditText) itemView.findViewById(R.id.edt_name);
            EditText edt_email = (EditText) itemView.findViewById(R.id.edt_email);

            builder.setPositiveButton("REGISTER", (dialogInterface, i) -> {
                if (TextUtils.isEmpty(edt_name.getText().toString())) {
                    Toast.makeText(this, "enter Name !", Toast.LENGTH_SHORT).show();
                    return;
                } else if (TextUtils.isEmpty(edt_email.getText().toString())) {
                    Toast.makeText(this, "enter Email", Toast.LENGTH_SHORT).show();
                    return;
                }
                UserModel userModel = new UserModel();
                userModel.setUid(user.getUid());
                userModel.setName(edt_name.getText().toString());
                userModel.setEmail(edt_email.getText().toString());
                userModel.setPhone(user.getPhoneNumber());
                userRef.child(userModel.getUid())
                        .setValue(userModel)
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                dialog.dismiss();
                                Toast.makeText(MainActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        goToHomeActivity(userModel);
                    }
                });
            });
            builder.setView(itemView);
            androidx.appcompat.app.AlertDialog registerDialog = builder.create();
            registerDialog.setCanceledOnTouchOutside(false);
            registerDialog.setCancelable(false);
            registerDialog.show();
        }
        catch (Exception e){
            //Toast.makeText(MainActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    private void goToHomeActivity(UserModel userModel) {
        dialog.dismiss();
        Common.currentUserModel = userModel;
        Intent intent = new Intent(this,HomeActivity.class);
        startActivity(intent);
        finish();
    }
    private void PhoneLogin() {

        startActivityForResult(AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .setLogo(R.drawable.logo_explorer)
                .setTheme(R.style.LoginTheme)
                .build(),APP_REQUEST_CODE);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("check","check");
        if(requestCode == APP_REQUEST_CODE) {
            IdpResponse response = IdpResponse.fromResultIntent(data);
            if (resultCode == RESULT_OK) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            }
        }
        else
        {
            Toast.makeText(this, "Failed to sign in", Toast.LENGTH_SHORT).show();
        }

    }
}