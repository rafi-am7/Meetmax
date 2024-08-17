package com.example.meetmax;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
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
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

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
    private static final String KEY_NAME = "Name", KEY_EMAIL = "Email", KEY_PASS = "Password",
            KEY_GENDER = "Gender", KEY_DOB = "DoB", KEY_VERIFY = "Verified", KEY_UID = "Uid";
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
        fullNameEditText=findViewById(R.id.sign_up_fullname_textview);
        emailEditText=findViewById(R.id.sign_up_email_text_view);
        passwordEditText=findViewById(R.id.sign_up_password_text_view);
        signUpButton=findViewById(R.id.sign_up_button);
        logInWithAppleButton=findViewById(R.id.log_in_with_apple_button);
        logInWithGoogleButton=findViewById(R.id.log_in_with_google_button);
        signInButton=findViewById(R.id.sign_in_button);
        dateOfBirthButton=findViewById(R.id.date_of_birth_button);
        genderRadioGroup = findViewById(R.id.sign_up_gender_radio_group);

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
    }
    void signUp()
    {
        fullName=fullNameEditText.getText().toString();
        email=emailEditText.getText().toString();
        password=passwordEditText.getText().toString();
        int selectedId = genderRadioGroup.getCheckedRadioButtonId();
        if(fullName.isEmpty()) {
            fullnameLayout.setError("Enter Name");
            fullnameLayout.requestFocus();
            return;
        }

        if(email.isEmpty()) {
            emailLayout.setError("Enter Name");
            emailLayout.requestFocus();
            return;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches())
        {
            emailLayout.setError("Please provide valid email");
            emailLayout.requestFocus();
            return;
        }


        if(password.length() < 6) {
            passwordLayout.setError("Minimum 6 digit");
            passwordLayout.requestFocus();
            return;
        }


        if(selectedId == -1) {
            genderLayout.setError("Select Gender");
            genderLayout.requestFocus();
            return;
        }

        if(Objects.equals(birthdate, "")) {
            Toast.makeText(SignUp.this,"Select Birthdate",Toast.LENGTH_LONG).show();
            return;
        }

        firebaseAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            uid = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid();
                            dbReference = fStore.collection("UID").document(uid);

                            Map<String,Object> val = new HashMap<>();
                            val.put(KEY_NAME,fullName);
                            val.put(KEY_EMAIL,email);
                            val.put(KEY_PASS,password);
                            val.put(KEY_GENDER,gender);
                            val.put(KEY_DOB,Birthdate);
                            val.put(KEY_VERIFY,verified);

                            dbReference.set(val).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful())
                                    {
                                        if(Objects.equals(position, "Director")){
                                            adminRef = fStore.collection("Admin").document(uid);

                                            Map<String, Object> dir = new HashMap<>();
                                            dir.put(KEY_NAME,name);
                                            dir.put(KEY_UID, uid);
                                            dir.put(KEY_HID, hid);
                                            dir.put(KEY_EMAIL,email);

                                            adminRef.set(dir).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        Toast.makeText(SignUpUserInputActivity.this, "SignUp complete. Data sent to Admin for validation", Toast.LENGTH_LONG).show();
                                                        startActivity(new Intent(SignUpUserInputActivity.this, SignInActivity.class));
                                                        finish();
                                                    } else {
                                                        Toast.makeText(SignUpUserInputActivity.this, "Failed to Complete Request", Toast.LENGTH_LONG).show();
                                                    }
                                                }
                                            });
                                        } else {
                                            hospRef = fStore.collection("Hospitals").document(hid).collection(position).document(uid);

                                            Map<String, Object> work = new HashMap<>();
                                            work.put(KEY_UID, uid);
                                            work.put(KEY_VERIFY, verified);

                                            hospRef.set(work).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        Toast.makeText(SignUpUserInputActivity.this, "SignUp complete. Data sent for Director for validation", Toast.LENGTH_LONG).show();
                                                        startActivity(new Intent(SignUpUserInputActivity.this, SignInActivity.class));
                                                        finish();
                                                    } else {
                                                        Toast.makeText(SignUpUserInputActivity.this, "Failed to Complete Request", Toast.LENGTH_LONG).show();
                                                    }
                                                }
                                            });
                                        }

                                    } else {
                                        Toast.makeText(SignUp.this,"Failed to Complete Request", Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                        } else {
                            Toast.makeText(SignUp.this,"Unable to create id. Please try again",Toast.LENGTH_LONG).show();
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

}