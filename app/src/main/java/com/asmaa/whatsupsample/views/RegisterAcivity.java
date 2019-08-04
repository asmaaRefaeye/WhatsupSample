package com.asmaa.whatsupsample.views;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.asmaa.whatsupsample.MainActivity;
import com.asmaa.whatsupsample.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterAcivity extends AppCompatActivity {

    private ImageView registerImage ;
    private EditText registerEmail ,registerPassword ;
    private Button createAccountButton;
    private TextView haveAnAccount;
    private FirebaseAuth mAuth;
    private DatabaseReference RootRef;
    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_acivity);

        mAuth =FirebaseAuth.getInstance();
        RootRef = FirebaseDatabase.getInstance().getReference();

        iniate();

        haveAnAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                sendUserToLoginActivity();
            }
        });

        createAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                creatNewAccount();
            }
        });
    }

    private void iniate() {

        registerImage=findViewById(R.id.register_image);
        registerEmail=findViewById(R.id.register_email);
        registerPassword=findViewById(R.id.register_password);
        createAccountButton=findViewById(R.id.creat_account_button);
        haveAnAccount=findViewById(R.id.already_have_an_account_link);
        loadingBar = new ProgressDialog(this);

    }

    private void sendUserToLoginActivity() {

        Intent loginIntent = new Intent(RegisterAcivity.this, LogInActivity.class);
        startActivity(loginIntent);
    }

    private void creatNewAccount(){
        String email = registerEmail.getText().toString();
        String password = registerPassword.getText().toString();

        if (TextUtils.isEmpty(email)){
            Toast.makeText(this, "please enter your email....", Toast.LENGTH_SHORT).show();
        }

        if (TextUtils.isEmpty(password)){
            Toast.makeText(this, "please enter your password....", Toast.LENGTH_SHORT).show();
        }
        else {
            loadingBar.setTitle("Creating New Account");
            loadingBar.setMessage("Waiting while creating Account for you ....");
            loadingBar.setCanceledOnTouchOutside(true);
            loadingBar.show();

            mAuth.createUserWithEmailAndPassword(email,password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                String currentUserId = mAuth.getCurrentUser().getUid();
                                RootRef.child("Users").child(currentUserId).setValue("");

                                sendToMainActivity();
                                Toast.makeText(RegisterAcivity.this, "Account is created successfully....", Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();

                            }else {
                                String message = task.getException().toString();
                                Toast.makeText(RegisterAcivity.this, "Error :"+ message, Toast.LENGTH_SHORT).show();
                                 loadingBar.dismiss();
                            }
                        }
                    });
        }
    }

    private void sendToMainActivity(){

        Intent mainIntent = new Intent (RegisterAcivity.this, MainActivity.class);
            mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }
}
