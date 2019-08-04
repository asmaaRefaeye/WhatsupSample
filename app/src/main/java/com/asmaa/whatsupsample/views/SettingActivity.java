package com.asmaa.whatsupsample.views;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
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

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingActivity extends AppCompatActivity {

    private Button updateAccountSettings;
    private EditText usereName , userStatus ;
    private CircleImageView userImageView ;

    private String currentUserId;
    private FirebaseAuth mAuth ;
    private DatabaseReference Rootref;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();
        Rootref = FirebaseDatabase.getInstance().getReference();



        inializeFields();

        usereName.setVisibility(View.INVISIBLE);

        updateAccountSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                updateSettings();
            }
        });
        
        retrieveUserInfo();
    }




    private void inializeFields() {
        usereName=findViewById(R.id.set_Profile_name);
        userStatus=findViewById(R.id.set_Profile_status);
        userImageView=findViewById(R.id.set_profile_image);
        updateAccountSettings=findViewById(R.id.update_settings_button);

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
