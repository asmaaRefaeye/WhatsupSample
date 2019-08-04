package com.asmaa.whatsupsample;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.asmaa.whatsupsample.adapter.TabsAdapter;
import com.asmaa.whatsupsample.views.LogInActivity;
import com.asmaa.whatsupsample.views.SettingActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ViewPager viewPager;
    private TabsAdapter tabsAdapter;
    private TabLayout tabLayout;

    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;
    private DatabaseReference RootRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        iniate();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);
        Log.e("test","onCreateOptionMenu");
        Log.e("test","id of the item : "+menu.getItem(0).getItemId());
        return true ;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        if (item.getItemId() == R.id.main_log_out_option)
        {
            Log.e("test","onItemSelected");

            mAuth.signOut();
            sendUserToLoginActivity();
        }

        if (item.getItemId() == R.id.main_settings_option)
        {
            sendUserToSettingsActivity();
        }

        if (item.getItemId() == R.id.main_create_group_option)
        {
              requestNewGroup();
        }

        if (item.getItemId() == R.id.main_find_friends_option)
        {

        }

        return  true ;

    }

    private void iniate() {

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        RootRef= FirebaseDatabase.getInstance().getReference();

    /*  //  toolbar = findViewById(R.id.app_bar_layout);
        Log.e("test","OnCreate initiate()");
      //  setSupportActionBar(toolbar);
       // toolbar.setSubtitle("WhatsUp App");
       // toolbar.inflateMenu(R.menu.options_menu);
        */

        tabLayout = findViewById(R.id.main_tabs);
        viewPager = findViewById(R.id.main_tabs_viewpager);

        tabLayout.addTab(tabLayout.newTab().setText("Chat"));
        tabLayout.addTab(tabLayout.newTab().setText("Groups"));
        tabLayout.addTab(tabLayout.newTab().setText("Contacts"));
        tabLayout.setTabGravity(tabLayout.GRAVITY_FILL);
        tabLayout.setupWithViewPager(viewPager);
        tabsAdapter = new TabsAdapter(getSupportFragmentManager());
        viewPager.setAdapter(tabsAdapter);


        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();


        if (currentUser == null) {

            sendUserToLoginActivity();
        }
        else {

            VerifyUserExistance();
        }
    }

    private void requestNewGroup()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this,R.style.AlertDialog);
        builder.setTitle("Create Group Name ..... ");

        final EditText groupNameField = new EditText(MainActivity.this);
        groupNameField.setHint("Android coding ...");
        builder.setView(groupNameField);

        builder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String groupName = groupNameField.getText().toString();
                if (TextUtils.isEmpty(groupName))
                {
                    Toast.makeText(MainActivity.this, "Please write Group Name .....", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    createNewGroup(groupName);
                }
            }
        });


        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.cancel();
            }
        });

        builder.show();
    }

    private void createNewGroup(final String groupName)
    {
        RootRef.child("Groups").child(groupName).setValue("")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful())
                        {
                            Toast.makeText(MainActivity.this, groupName +"group is created successfully ... ", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void VerifyUserExistance() {
        String currentUserID = mAuth.getCurrentUser().getUid();
        RootRef.child("Users").child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if((dataSnapshot.child("name").exists()))
                {
                    Toast.makeText(MainActivity.this, " Welcome .... ", Toast.LENGTH_SHORT).show();
                }
                else
                  {
                    sendUserToSettingsActivity();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {

            }
        });

    }

    private void sendUserToLoginActivity() {

        Intent loginIntent = new Intent(MainActivity.this, LogInActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
        finish();
    }

    private void sendUserToSettingsActivity(){
        Intent settingsIntent = new Intent(MainActivity.this, SettingActivity.class);
        settingsIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(settingsIntent);
        finish();

    }

}

