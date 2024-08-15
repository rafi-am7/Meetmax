package com.example.meetmax;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
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

public class ForgotPassword extends AppCompatActivity {
    AutoCompleteTextView languageTextView;
    EditText emailEditText;
    String email;
    MaterialButton forgotPasswordButton;
    TextView signInButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_forgot_password);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        languageTextView=findViewById(R.id.language_manu_text_view);
        emailEditText=findViewById(R.id.forgot_password_email_edit_text);
        forgotPasswordButton=findViewById(R.id.forgot_password_button);
        signInButton=findViewById(R.id.sign_in_button);

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
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToSignIn();
            }
        });
        forgotPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                forgotPassword();
            }
        });

    }
    void goToSignIn()
    {
        Intent intent = new Intent(ForgotPassword.this,SignIn.class);
        startActivity(intent);
    }
    void forgotPassword()
    {
        email=emailEditText.getText().toString();
    }

}