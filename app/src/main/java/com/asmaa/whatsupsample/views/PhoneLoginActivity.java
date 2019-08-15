package com.asmaa.whatsupsample.views;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.asmaa.whatsupsample.MainActivity;
import com.asmaa.whatsupsample.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class PhoneLoginActivity extends AppCompatActivity


{

    private  Button sendVerificationCode_Button , verify_Button ;
    private          EditText phoneNumber_EditText , codeVerification_EditText ;

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks ;

    private String mVerificationId ;
    private PhoneAuthProvider.ForceResendingToken mResendToken ;

    private FirebaseAuth mAuth ;

    private ProgressDialog loadingBar ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_login);


        mAuth = FirebaseAuth.getInstance();

        inializeFields();
    }

    private void inializeFields()
    {
        phoneNumber_EditText=findViewById(R.id.phone_number_editText);
        codeVerification_EditText=findViewById(R.id.verification_code_editText);
        sendVerificationCode_Button=findViewById(R.id.send_ver_code_button);
        verify_Button=findViewById(R.id.verify_button);

        loadingBar= new ProgressDialog(this);

        sendVerificationCode_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                sendVerificationCode_Button.setVisibility(View.INVISIBLE);
                phoneNumber_EditText.setVisibility(View.INVISIBLE);

                codeVerification_EditText.setVisibility(View.VISIBLE);
                verify_Button.setVisibility(View.VISIBLE);


                String phoneNumber = phoneNumber_EditText.getText().toString();

                if(TextUtils.isEmpty(phoneNumber))
                {
                    Toast.makeText(PhoneLoginActivity.this, "phone number required ..... ", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    loadingBar.setTitle("Phone Verification ");
                    loadingBar.setMessage("please wait, while we are verifying your phone number .. ");
                    loadingBar.setCanceledOnTouchOutside(false);
                    loadingBar.show();

                    setUpVerificationCallBacks();

                    PhoneAuthProvider.getInstance().verifyPhoneNumber(
                            phoneNumber,        // Phone number to verify
                            60,                 // Timeout duration
                            TimeUnit.SECONDS,   // Unit of timeout
                            PhoneLoginActivity.this ,             // Activity (for callback binding)
                            callbacks);
                }
            }
        });

        verify_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                sendVerificationCode_Button.setVisibility(View.VISIBLE);
                phoneNumber_EditText.setVisibility(View.VISIBLE);

                String verificationCode = codeVerification_EditText.getText().toString();
                if (TextUtils.isEmpty(verificationCode))
                {
                    Toast.makeText(PhoneLoginActivity.this, "please write verification code ....", Toast.LENGTH_SHORT).show();
                }
                else
                {

                    loadingBar.setTitle("Verification code");
                    loadingBar.setMessage("please wait, while we are verifying Verification code .. ");
                    loadingBar.setCanceledOnTouchOutside(false);
                    loadingBar.show();

                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, verificationCode);
                    signInWithPhoneAuthCredential(credential);
                }
            }
        });




    }

    private void setUpVerificationCallBacks()
    {
        callbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential)
            {

                signInWithPhoneAuthCredential(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e)
            {


                Log.e("failed", e.getMessage());
                Toast.makeText(PhoneLoginActivity.this, "Incorrect number ... , please enter your phone number with your country code ", Toast.LENGTH_SHORT).show();
                loadingBar.dismiss();
                sendVerificationCode_Button.setVisibility(View.VISIBLE);
                phoneNumber_EditText.setVisibility(View.VISIBLE);

                codeVerification_EditText.setVisibility(View.INVISIBLE);
                verify_Button.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onCodeSent(String verificationId,
                                   PhoneAuthProvider.ForceResendingToken token)
            {

                mVerificationId = verificationId;
                mResendToken = token;

                loadingBar.dismiss();
                Toast.makeText(PhoneLoginActivity.this, "code has been send ...", Toast.LENGTH_SHORT).show();

                sendVerificationCode_Button.setVisibility(View.INVISIBLE);
                phoneNumber_EditText.setVisibility(View.INVISIBLE);

                codeVerification_EditText.setVisibility(View.VISIBLE);
                verify_Button.setVisibility(View.VISIBLE);
            }
        };



    }



    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful())
                        {
                            loadingBar.dismiss();
                            Toast.makeText(PhoneLoginActivity.this, "you successfully login .. ", Toast.LENGTH_SHORT).show();

                            sendUserToMainActivity();


                        } else
                         {
 
                             String message = task.getException().toString();
                             Toast.makeText(PhoneLoginActivity.this, "Error : "+ message, Toast.LENGTH_SHORT).show();
                             loadingBar.dismiss();
                        }
                    }
                });
    }

    private void sendUserToMainActivity()
    {

        Intent mainIntent = new Intent(PhoneLoginActivity.this , MainActivity.class);
        startActivity(mainIntent);
        finish();
    }

}
