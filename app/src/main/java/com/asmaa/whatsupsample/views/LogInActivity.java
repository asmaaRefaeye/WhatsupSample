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
import com.google.firebase.auth.FirebaseUser;

public class LogInActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private EditText loginEmail , loginPassword ;
    private TextView forgrtPassword , needNewAccount , loginwithyours;
    private Button loginButton , phoneButton;
    private ImageView loginImage;
    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        mAuth = FirebaseAuth.getInstance();


        iniate();

        needNewAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                sendUserToRegisterActivity();
            }
        });
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AllowUserToLogin();
            }
        });

        phoneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                sendToPhoneActivity();
            }
        });
    }

    private void sendToPhoneActivity()
    {
        Intent phoneIntent = new Intent(LogInActivity.this,PhoneLoginActivity.class);
        startActivity(phoneIntent);
    }

    private void iniate() {
        loginEmail=findViewById(R.id.login_email);
        loginPassword=findViewById(R.id.login_password);
        forgrtPassword=findViewById(R.id.login_forget_password_link);
        needNewAccount=findViewById(R.id.need_an_account_link);
        loginwithyours=findViewById(R.id.login_using);
        loginButton=findViewById(R.id.login_button);
        phoneButton=findViewById(R.id.Phone_login_button);
        loginImage=findViewById(R.id.login_image);
        loadingBar=new ProgressDialog(this);
        
    }


    private void sendUserToMainActivity() {

        Intent loginIntent = new Intent(LogInActivity.this, MainActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
        finish();
    }


    private void sendUserToRegisterActivity() {

        Intent registerIntent = new Intent(LogInActivity.this, RegisterAcivity.class);
        startActivity(registerIntent);
    }

    private void AllowUserToLogin(){

        String email = loginEmail.getText().toString();
        String password = loginPassword.getText().toString();

        if (TextUtils.isEmpty(email)){
            Toast.makeText(this, "please enter your email....", Toast.LENGTH_SHORT).show();
        }

        if (TextUtils.isEmpty(password)){
            Toast.makeText(this, "please enter your password....", Toast.LENGTH_SHORT).show();
        }
        else{

            loadingBar.setTitle("Sign IN");
            loadingBar.setMessage("please wait ....");
            loadingBar.setCanceledOnTouchOutside(true);
            loadingBar.show();

            mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if ((task.isSuccessful())){
                        sendUserToMainActivity();
                        Toast.makeText(LogInActivity.this, "Logged in is successfull ....", Toast.LENGTH_SHORT).show();
                        loadingBar.dismiss();
                    }
                    else {
                        String message = task.getException().toString();
                        Toast.makeText(LogInActivity.this, "Error :"+ message, Toast.LENGTH_SHORT).show();
                        loadingBar.dismiss();
                    }
                }
            });

        }
    }
}
