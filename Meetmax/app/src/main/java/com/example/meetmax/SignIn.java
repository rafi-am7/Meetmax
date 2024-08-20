package com.example.meetmax;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
    ProgressBar progressBar;
    View overlay;
    TextInputLayout textInputLayoutLang;
    ImageView logoImage;
    ConstraintLayout signInConst;
    @SuppressLint("MissingInflatedId")
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
        progressBar = findViewById(R.id.progress_bar);
        overlay = findViewById(R.id.overlay);
        logoImage = findViewById(R.id.sign_up_logo_with_text);
        signInConst = findViewById(R.id.sign_in_const);
        textInputLayoutLang = findViewById(R.id.language_manu);
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
                finish();
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
    void signIn() {
        email = emailEditText.getText().toString();
        password = passwordEditText.getText().toString();
        checkBoxIsChecked = rememberMeCheckBox.isChecked();

        // Check if the email or password fields are empty
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(SignIn.this, "Email and password cannot be empty.", Toast.LENGTH_SHORT).show();
            return;
        }

        // If not, proceed with signing in
        progressBar.setVisibility(View.VISIBLE); // Show ProgressBar
        overlay.setVisibility(View.VISIBLE); // Show overlay
        // Disable relevant UI components to prevent multiple sign-in attempts
        signInButton.setEnabled(false);
        signUpButton.setEnabled(false);
        logInWithGoogle.setEnabled(false);
        forgetPasswordButton.setEnabled(false);
        rememberMeCheckBox.setEnabled(false);
        languageTextView.setEnabled(false);
        emailEditText.setEnabled(false);
        passwordEditText.setEnabled(false);

        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progressBar.setVisibility(View.GONE); // Hide ProgressBar
                overlay.setVisibility(View.GONE); // Hide overlay
                // Re-enable the UI components
                signInButton.setEnabled(true);
                signUpButton.setEnabled(true);
                logInWithGoogle.setEnabled(true);
                forgetPasswordButton.setEnabled(true);
                rememberMeCheckBox.setEnabled(true);
                languageTextView.setEnabled(true);
                emailEditText.setEnabled(true);
                passwordEditText.setEnabled(true);
                if (task.isSuccessful()) {
                    // Sign in success
                    FirebaseUser user = firebaseAuth.getCurrentUser();
                    if (user != null) {
                        if (user.isEmailVerified()) {
                            // Email is verified, proceed to your MainActivity or another activity
                            saveSignInState(true);
                            Intent intent = new Intent(SignIn.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            // Email not verified, send a verification email
                            user.sendEmailVerification()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                emailEditText.setText("");
                                                passwordEditText.setText("");
                                                // Notify user about the verification email sent
                                                Toast.makeText(SignIn.this,
                                                        "Verification email sent to " + user.getEmail(),
                                                        Toast.LENGTH_SHORT).show();
                                                // Optionally log out the user until they verify their email
                                                FirebaseAuth.getInstance().signOut();
                                            } else {
                                                // Handle if there's an error sending the email
                                                Toast.makeText(SignIn.this,
                                                        "Failed to send verification email.",
                                                        Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        }
                    }
                } else {
                    // If sign in fails, display a message to the user
                    Toast.makeText(SignIn.this, "Authentication failed.",
                            Toast.LENGTH_SHORT).show();
                }

//                if (task.isSuccessful()) {
//                    saveSignInState(true);
//                    Toast.makeText(SignIn.this,"Sign in successful",Toast.LENGTH_SHORT).show();
//                    // Clear the EditText fields
//                    emailEditText.setText("");
//                    passwordEditText.setText("");
//                    startActivity(new Intent(SignIn.this, MainActivity.class));
//                    finish(); // End the activity to prevent returning here on back press
//                } else {
//                    // Show error message
//                    Toast.makeText(SignIn.this, "Invalid Email or Password", Toast.LENGTH_LONG).show();
//                }
            }
        });
    }
    private void saveSignInState(boolean isSignedIn) {
        SharedPreferences preferences = getSharedPreferences("sign_in_prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("is_signed_in", isSignedIn);
        editor.apply();
    }


}