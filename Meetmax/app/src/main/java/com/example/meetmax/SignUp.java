package com.example.meetmax;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import models.UserModel;

public class SignUp extends AppCompatActivity {
    AutoCompleteTextView languageTextView;
    EditText fullNameEditText, emailEditText, passwordEditText;
    public static ArrayAdapter<String> languageAdapter;
    MaterialButton signUpButton, logInWithGoogleButton, logInWithAppleButton, dateOfBirthButton;
    TextView signInButton;
    public static ArrayList<String> languageList;
    MaterialDatePicker.Builder<Long> materialDateBuilder;
    String birthdate,showDate;
    public static String curLanguage;
    String fullName, email, password;
    RadioGroup genderRadioGroup;
    RadioButton selectedRadioButton;
    FirebaseFirestore firestore;
    FirebaseAuth firebaseAuth;
    TextInputLayout emailLayout, fullnameLayout, passwordLayout, genderLayout, dobLayout;
    ProgressBar progressBar;
    View overlay;
    private static final String KEY_NAME = "Name", KEY_EMAIL = "Email", KEY_PASS = "Password",
            KEY_GENDER = "Gender", KEY_DOB = "DoB", KEY_VERIFY = "Verified", KEY_UID = "Uid";
    String verified,uid;
    DocumentReference documentReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        if (checkSignInState()) {
            // User is already signed in, redirect to MainActivity
            startActivity(new Intent(SignUp.this, MainActivity.class));
            finish();
            return; // Prevent further execution of onCreate
        }

        fullNameEditText=findViewById(R.id.sign_up_fullname_textview);
        emailEditText=findViewById(R.id.sign_up_email_text_view);
        passwordEditText=findViewById(R.id.sign_up_password_text_view);
        signUpButton=findViewById(R.id.sign_up_button);
        logInWithAppleButton=findViewById(R.id.log_in_with_apple_button);
        logInWithGoogleButton=findViewById(R.id.log_in_with_google_button);
        signInButton=findViewById(R.id.sign_in_button);
        dateOfBirthButton=findViewById(R.id.date_of_birth_button);
        genderRadioGroup = findViewById(R.id.sign_up_gender_radio_group);
        progressBar = findViewById(R.id.progress_bar);
        overlay = findViewById(R.id.overlay);

        emailLayout = findViewById(R.id.sign_up_email);
        fullnameLayout = findViewById(R.id.sign_up_full_name);
        passwordLayout = findViewById(R.id.sign_up_password);
        genderLayout = findViewById(R.id.sign_up_password);
        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();



        materialDateBuilder = MaterialDatePicker.Builder.datePicker();
        //materialDateBuilder.setTitleText("Select Birth Date");
        final MaterialDatePicker<Long> materialDatePicker = materialDateBuilder.build();

        dateOfBirthButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        materialDatePicker.show(getSupportFragmentManager(), "MATERIAL_DATE_PICKER");
                    }
                });

        materialDatePicker.addOnPositiveButtonClickListener(
                selection -> {
                    birthdate = materialDatePicker.getHeaderText();
                    showDate = "  "+birthdate;
                    if(showDate!=null) {
                        dateOfBirthButton.setText(showDate);
                    }
                });



        languageList=new ArrayList<>();
        curLanguage="English (UK)";
        languageList.add("English (UK)");
        languageList.add("Bengali");
        languageTextView =findViewById(R.id.language_manu_text_view);
        languageAdapter=new ArrayAdapter<>(this,R.layout.auto_complete_text_view_list,languageList);
        languageTextView.setAdapter(languageAdapter);
        if(curLanguage!=null)
        {
            languageTextView.setText(curLanguage, false);
        }
        else if (languageList != null && !languageList.isEmpty()) {
            languageTextView.setText(languageList.get(0), false);
        }

        languageTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                curLanguage = parent.getItemAtPosition(position).toString();
                setLanguage();
            }
        });

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToSignInActivity();

            }
        });
        logInWithGoogleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logInWithGoogle();
            }
        });
        logInWithAppleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logInWithApple();
            }
        });
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signUp();
            }
        });
    }
    void goToSignInActivity()
    {
        Intent intent = new Intent(SignUp.this, SignIn.class);
        startActivity(intent);
        finish();
    }
    void signUp() {
        fullName = fullNameEditText.getText().toString();
        email = emailEditText.getText().toString();
        password = passwordEditText.getText().toString();
        int selectedId = genderRadioGroup.getCheckedRadioButtonId();

        if (fullName.isEmpty()) {
            fullnameLayout.setError("Enter Name");
            fullnameLayout.requestFocus();
            return;
        }

        if (email.isEmpty()) {
            emailLayout.setError("Enter Email");
            emailLayout.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailLayout.setError("Please provide valid email");
            emailLayout.requestFocus();
            return;
        }

        if (password.length() < 6) {
            passwordLayout.setError("Minimum 6 digits");
            passwordLayout.requestFocus();
            return;
        }

        if (selectedId == -1) {
            genderLayout.setError("Select Gender");
            genderLayout.requestFocus();
            return;
        }

        RadioButton selectedRadioButton = findViewById(selectedId);
        String gender = selectedRadioButton.getText().toString();

        if (Objects.equals(birthdate, "")) {
            Toast.makeText(SignUp.this, "Select Birthdate", Toast.LENGTH_LONG).show();
            return;
        }

        // Show the ProgressBar and overlay, and disable other inputs
        progressBar.setVisibility(View.VISIBLE);
        overlay.setVisibility(View.VISIBLE);

        signUpButton.setEnabled(false);
        logInWithGoogleButton.setEnabled(false);
        logInWithAppleButton.setEnabled(false);
        dateOfBirthButton.setEnabled(false);
        languageTextView.setEnabled(false);

        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        // Hide the ProgressBar and overlay, and re-enable inputs
                        progressBar.setVisibility(View.GONE);
                        overlay.setVisibility(View.GONE);
                        signUpButton.setEnabled(true);
                        logInWithGoogleButton.setEnabled(true);
                        logInWithAppleButton.setEnabled(true);
                        dateOfBirthButton.setEnabled(true);
                        languageTextView.setEnabled(true);

                        if (task.isSuccessful()) {
                            uid = firebaseAuth.getCurrentUser().getUid();

                            UserModel user = new UserModel(uid, fullName, email, password, gender, birthdate, verified);

                            firestore.collection("Users").document(uid).set(user)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                saveSignInState(true);
                                                sendVerificationEmail();

                                            } else {
                                                Toast.makeText(SignUp.this, "Sign Up Failed!", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        } else {
                            Toast.makeText(SignUp.this, "Unable to create account. Please try again", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }


    public static void setLanguage()
    {
    }
    void logInWithGoogle()
    {

    }
    void logInWithApple()
    {
        startActivity(new Intent(SignUp.this,MainActivity.class));
        finish();

    }
    private void saveSignInState(boolean isSignedIn) {
        SharedPreferences preferences = getSharedPreferences("sign_in_prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("is_signed_in", isSignedIn);
        editor.apply();
    }
    private boolean checkSignInState() {
        SharedPreferences preferences = getSharedPreferences("sign_in_prefs", MODE_PRIVATE);
        return preferences.getBoolean("is_signed_in", false);
    }
    private void sendVerificationEmail() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            user.sendEmailVerification()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                // Email sent
                                emailEditText.setText("");
                                fullNameEditText.setText("");
                                passwordEditText.setText("");
                                Toast.makeText(SignUp.this, "Verification email sent", Toast.LENGTH_SHORT).show();
                            } else {
                                // Email not sent
                                Log.e(TAG, "sendEmailVerification", task.getException());
                                Toast.makeText(SignUp.this, "Failed to send verification email.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }


}