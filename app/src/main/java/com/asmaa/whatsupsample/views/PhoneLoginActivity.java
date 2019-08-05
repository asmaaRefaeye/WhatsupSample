package com.asmaa.whatsupsample.views;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.asmaa.whatsupsample.R;

public class PhoneLoginActivity extends AppCompatActivity
{

    Button sendVerificationCode_Button , verify_Button ;
    EditText phoneNumber_EditText , codeVerification_EditText ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_login);

        inializeFields();
    }

    private void inializeFields()
    {
        phoneNumber_EditText=findViewById(R.id.phone_number_editText);
        codeVerification_EditText=findViewById(R.id.verification_code_editText);
        sendVerificationCode_Button=findViewById(R.id.send_ver_code_button);
        verify_Button=findViewById(R.id.verify_button);

        sendVerificationCode_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                sendVerificationCode_Button.setVisibility(View.INVISIBLE);
                phoneNumber_EditText.setVisibility(View.INVISIBLE);

                codeVerification_EditText.setVisibility(View.VISIBLE);
                verify_Button.setVisibility(View.VISIBLE);
            }
        });

    }
}
