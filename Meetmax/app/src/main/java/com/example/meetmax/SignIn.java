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
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.ArrayList;
import java.util.Objects;

public class SignIn extends AppCompatActivity {
    AutoCompleteTextView languageTextView;
    EditText passwordEditText, emailEditText;
    String email, password;
    MaterialButton signInButton,logInWithGoogle;
    TextView signUpButton,forgetPasswordButton;
    CheckBox rememberMeCheckBox;
    boolean checkBoxIsChecked;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firestore;
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
        logInWithGoogle=findViewById(R.id.log_in_with_google_button);

        firebaseAuth=FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

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
        logInWithGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

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



        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(SignIn.this,"Sign in successful",Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(SignIn.this, MainActivity.class));
//                    uid = Objects.requireNonNull(fAuth.getCurrentUser()).getUid();
//                    uidRef = fStore.collection("UID").document(uid);
//                    uidRef.addSnapshotListener(SignInActivity.this, new EventListener<DocumentSnapshot>() {
//                        @Override
//                        public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
//                            status = value.getString(KEY_VERIFY);
//                            name = value.getString(KEY_NAME);
//                            position = value.getString(KEY_POS);
//
//                            if (Objects.equals(status, "Yes")) {
//                                if (Objects.equals(position, "Doctor") || Objects.equals(position, "Receptionist")) {
//                                    Toast.makeText(SignInActivity.this, "Welcome " + name, Toast.LENGTH_LONG).show();
//                                    startActivity(new Intent(SignInActivity.this, MainActivity.class));
//                                    finish();
//                                } else if (Objects.equals(position, "Director")) {
//                                    Toast.makeText(SignInActivity.this, "Welcome " + name, Toast.LENGTH_LONG).show();
//                                    startActivity(new Intent(SignInActivity.this, DirectorMainActivity.class));
//                                    finish();
//                                } else if (Objects.equals(position, "Admin")) {
//                                    Toast.makeText(SignInActivity.this, "Welcome " + name, Toast.LENGTH_LONG).show();
//                                    startActivity(new Intent(SignInActivity.this, AdminMainActivity.class));
//                                    finish();
//                                } else {
//                                    Toast.makeText(SignInActivity.this, "Welcome " + name, Toast.LENGTH_LONG).show();
//                                    startActivity(new Intent(SignInActivity.this, NurseMainActivity.class));
//                                    finish();
//                                }
//                            } else {
//                                Toast.makeText(SignInActivity.this, "Please wait for Director to assign you", Toast.LENGTH_LONG).show();
//                            }
//                        }
//                    });

                } else {
                    Toast.makeText(SignIn.this, "Invalid Email or Password", Toast.LENGTH_LONG).show();
                }

            }
        });
    }
}