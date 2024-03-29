package com.asmaa.whatsupsample.views;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;


import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;



import com.asmaa.whatsupsample.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;

public class GroupChatActivity extends AppCompatActivity
{

    private Toolbar mtoolbar;
    private ImageButton sendMessageButton;
    private EditText userMessageInput;
    private ScrollView mscrollview ;
    private TextView displayTextMessage ;

    private String currentGroupName , currentUserID , currentUserName , currentDate , currentTime;

    private FirebaseAuth mAuth ;
    private DatabaseReference userRef , GroupNameRef , GroupMessageKeyRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);

        currentGroupName = getIntent().getExtras().get("groupName").toString();
        Toast.makeText(GroupChatActivity.this, currentGroupName, Toast.LENGTH_SHORT).show();

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        userRef = FirebaseDatabase.getInstance().getReference().child("Users");
        GroupNameRef = FirebaseDatabase.getInstance().getReference().child("Groups").child(currentGroupName);

        initializateFields();
        getUserInfo();

        sendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                saveMessageInfoToDataBase();
                userMessageInput.setText("");
                mscrollview.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();

        GroupNameRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s)
            {
                if (dataSnapshot.exists()){

                    DisplayMessages(dataSnapshot);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s)
            {
                if (dataSnapshot.exists())
                {

                    DisplayMessages(dataSnapshot);
                }

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }



    private void initializateFields()
    {
    /*  mtoolbar=findViewById(R.id.group_chat_bar_layout);
      setSupportActionBar(mtoolbar);
     getSupportActionBar().setTitle("Group Name");*/

        ActionBar actionBar = getSupportActionBar();
        getSupportActionBar().setTitle(currentGroupName);

        sendMessageButton=findViewById(R.id.send_message_buttton);
        userMessageInput=findViewById(R.id.input_group_message);
        mscrollview=findViewById(R.id.scroll_view);
        displayTextMessage=findViewById(R.id.group_chat_text_display);

    }


    private void getUserInfo()
    {

        userRef.child(currentGroupName).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {

                if (dataSnapshot.exists())
                {
                    currentUserName = dataSnapshot.child("name").getValue().toString();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void saveMessageInfoToDataBase()
    {
        String message = userMessageInput.getText().toString();
        String messageKEY = GroupNameRef.push().getKey();

        if (TextUtils.isEmpty(message))
        {
            Toast.makeText(this, " Please write message frist .... ", Toast.LENGTH_SHORT).show();
        }
        else
        {
            Calendar calenderForDate = Calendar.getInstance();
            SimpleDateFormat currentDateFormate = new SimpleDateFormat("MM dd , yyyy");
            currentDate = currentDateFormate.format(calenderForDate.getTime());

            Calendar calenderForTime = Calendar.getInstance();
            SimpleDateFormat currentTimeFormate = new SimpleDateFormat("hh:mm a");
            currentTime = currentTimeFormate.format(calenderForTime.getTime());

            HashMap<String ,Object> groupMessageKey = new HashMap<>();
            GroupNameRef.updateChildren(groupMessageKey);

            GroupMessageKeyRef = GroupNameRef.child(messageKEY);


            HashMap<String ,Object> messageInfoMap = new HashMap<>();
               // Log.e("#1",currentUserName);


              messageInfoMap.put("name",currentUserName);
              messageInfoMap.put("message",message);
              messageInfoMap.put("date",currentDate);
              messageInfoMap.put("time",currentTime);

             GroupMessageKeyRef.updateChildren(messageInfoMap);
        }
    }



    private void DisplayMessages(DataSnapshot dataSnapshot)
    {

        Iterator iterator = dataSnapshot.getChildren().iterator();
        while (iterator.hasNext())
        {
            /*Log.e("#1",(String) ((DataSnapshot)iterator.next()).getValue() );
            Log.e("#1",(String) ((DataSnapshot)iterator.next()).getValue());
            Log.e("#1",(String) ((DataSnapshot)iterator.next()).getValue());
            Log.e("#1",(String) ((DataSnapshot)iterator.next()).getValue());
            Log.e("#1","hasNext() ->"+iterator.hasNext());*/

            String chatData = (String) ((DataSnapshot)iterator.next()).getValue();
            String chatMessage = (String) ((DataSnapshot)iterator.next()).getValue();
            /*String chatName = (String) ((DataSnapshot)iterator.next()).getValue();*/
            String chatTime = (String) ((DataSnapshot)iterator.next()).getValue();

            displayTextMessage.append(/*chatName + " \n" + */chatMessage + "\n "+ chatTime + "      " + chatData +"\n\n\n");

            mscrollview.fullScroll(ScrollView.FOCUS_DOWN);
        }


    }
}
