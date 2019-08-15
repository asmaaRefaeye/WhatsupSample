package com.asmaa.whatsupsample.views;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.asmaa.whatsupsample.MainActivity;
import com.asmaa.whatsupsample.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingActivity extends AppCompatActivity {

    private Button updateAccountSettings;
    private EditText usereName , userStatus ;
    private CircleImageView userImageView ;

    private String currentUserId;
    private FirebaseAuth mAuth ;
    private DatabaseReference Rootref;

    private static final int GallaryPick = 1 ;

    private StorageReference userProfileImageRef ;

    private ProgressDialog loadingBar ;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();
        Rootref = FirebaseDatabase.getInstance().getReference();
        userProfileImageRef= FirebaseStorage.getInstance().getReference().child("Profile Images");



        inializeFields();

        usereName.setVisibility(View.INVISIBLE);

        updateAccountSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                updateSettings();
            }
        });
        
        retrieveUserInfo();

        userImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {

                Intent gallaryIntent = new Intent();
                gallaryIntent.setAction(Intent.ACTION_GET_CONTENT);
                gallaryIntent.setType("image/*");
                startActivityForResult(gallaryIntent,GallaryPick);
            }
        });
    }




    private void inializeFields() {
        usereName=findViewById(R.id.set_Profile_name);
        userStatus=findViewById(R.id.set_Profile_status);
        userImageView=findViewById(R.id.set_profile_image);
        updateAccountSettings=findViewById(R.id.update_settings_button);
        loadingBar = new ProgressDialog(this);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==GallaryPick && resultCode==RESULT_OK && data!=null)
        {
            Uri imageUri = data.getData();
            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1,1)
                    .start(this);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE)
        {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode==RESULT_OK)
            {
                loadingBar.setTitle("Set Profile Image ");
                loadingBar.setMessage("please wait , your photo is downloading ....");
                loadingBar.setCanceledOnTouchOutside(false);
                loadingBar.show();

                Uri resultUri = result.getUri();

                StorageReference filePath = userProfileImageRef.child(currentUserId + ".jpg");
                filePath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task)
                    {
                       if(task.isSuccessful())
                       {
                           Toast.makeText(SettingActivity.this, "Profile Image uploaded successfully ..", Toast.LENGTH_SHORT).show();

                              final String downloadURl = task.getResult().getMetadata().getReference().getDownloadUrl().toString();
                              Rootref.child("Users").child(currentUserId).child("image")
                                      .setValue(downloadURl)
                                      .addOnCompleteListener(new OnCompleteListener<Void>() {
                                          @Override
                                          public void onComplete(@NonNull Task<Void> task) {

                                              if (task.isSuccessful())
                                              {
                                                  Toast.makeText(SettingActivity.this, "Image Saved in Database , Successfully ... ", Toast.LENGTH_SHORT).show();
                                                  loadingBar.dismiss();
                                              }
                                              else
                                              {
                                                  String message = task.getException().toString();
                                                  Toast.makeText(SettingActivity.this, "Error : "+ message, Toast.LENGTH_SHORT).show();
                                                  loadingBar.dismiss();
                                              }

                                          }
                                      });

                       }
                       else
                       {
                           String message = task.getException().toString();
                           Toast.makeText(SettingActivity.this, "Error  .." + message, Toast.LENGTH_SHORT).show();
                           loadingBar.dismiss();
                       }
                    }
                });
            }



        }
    }

    private void updateSettings()
    {
       String setUserName = usereName.getText().toString();
       String setUserStatus = userStatus.getText().toString();

       if (TextUtils.isEmpty(setUserName))
       {
           Toast.makeText(this, "Please Write your user Name ....", Toast.LENGTH_SHORT).show();
       }

        if (TextUtils.isEmpty(setUserStatus))
        {
            Toast.makeText(this, "Please Write your user Status ....", Toast.LENGTH_SHORT).show();
        }
        else
        {
            HashMap<String , String> profileMap = new HashMap<>();
               profileMap.put("uid",currentUserId);
               profileMap.put("name",setUserName);
               profileMap.put("status",setUserStatus);
            Rootref.child("Users").child(currentUserId).setValue(profileMap)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task)
                        {
                               if (task.isSuccessful())
                               {
                                   sendUserToMainActivity();
                                   Toast.makeText(SettingActivity.this, "Profile Updated Successfully ..", Toast.LENGTH_SHORT).show();
                               }
                               else
                               {
                                   String message = task.getException().toString();
                                   Toast.makeText(SettingActivity.this, "Error " + message, Toast.LENGTH_SHORT).show();
                               }
                        }
                    });
        }

    }


    private void retrieveUserInfo()
    {
        Rootref.child("Users").child(currentUserId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                    {
                        if((dataSnapshot.exists()) && (dataSnapshot.hasChild("name")) && dataSnapshot.hasChild("image"))
                        {
                           String retrieveUserName = dataSnapshot.child("name").getValue().toString();
                            String retrieveStatus = dataSnapshot.child("status").getValue().toString();
                            String retrieveProfileImage = dataSnapshot.child("image").getValue().toString();

                            usereName.setText(retrieveUserName);
                            userStatus.setText(retrieveStatus);
                            Picasso.get().load(retrieveProfileImage).into(userImageView);

                        }
                        else if ((dataSnapshot.exists()) && (dataSnapshot.hasChild("name")))
                        {

                            String retrieveUserName = dataSnapshot.child("name").getValue().toString();
                            String retrieveStatus = dataSnapshot.child("status").getValue().toString();

                            usereName.setText(retrieveUserName);
                            userStatus.setText(retrieveStatus);
                        }
                        else
                        {
                            usereName.setVisibility(View.VISIBLE);
                            Toast.makeText(SettingActivity.this, "Please set and update your profile information ....", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void sendUserToMainActivity() {

        Intent loginIntent = new Intent(SettingActivity.this, MainActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
        finish();
    }

}
