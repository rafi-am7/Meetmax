package com.example.meetmax;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;

public class SignIn extends AppCompatActivity {
    AutoCompleteTextView languageTextView;
    EditText passwordEditText, emailEditText;
    String email, password;
    MaterialButton signInButton;
    TextView signUpButton,forgetPasswordButton;
    CheckBox rememberMeCheckBox;
    boolean checkBoxIsChecked;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_in);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        languageTextView=findViewById(R.id.language_manu_text_view);
        emailEditText=findViewById(R.id.sign_in_email_edit_text);
        passwordEditText=findViewById(R.id.sign_in_password_edit_text);
        signInButton=findViewById(R.id.sign_in_button);
        signUpButton=findViewById(R.id.sign_up_button);
        forgetPasswordButton=findViewById(R.id.forget_password_button);
        rememberMeCheckBox=findViewById(R.id.remeber_me_checkbox);



        languageTextView.setAdapter(SignUp.languageAdapter);
        if(SignUp.curLanguage!=null)
        {
            languageTextView.setText(SignUp.curLanguage, false);
        }
        else if (SignUp.languageList != null && !SignUp.languageList.isEmpty()) {
            languageTextView.setText(SignUp.languageList.get(0), false);
        }

        languageTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                SignUp.curLanguage = parent.getItemAtPosition(position).toString();
                SignUp.setLanguage();
            }
        });

        forgetPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToForgetPasswordActivity();
            }
        });
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToSignUpActivity();
            }
        });
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });

    }
    void goToForgetPasswordActivity()
    {
        Intent intent = new Intent(SignIn.this,ForgotPassword.class);
        startActivity(intent);
        finish();
    }
    void goToSignUpActivity()
    {
        Intent intent = new Intent(SignIn.this,SignUp.class);
        startActivity(intent);
        finish();

    }
    void signIn()
    {
        email=emailEditText.getText().toString();
        password=passwordEditText.getText().toString();
        checkBoxIsChecked = rememberMeCheckBox.isChecked();
    }
}