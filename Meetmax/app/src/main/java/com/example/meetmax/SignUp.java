package com.example.meetmax;

import android.content.Intent;
import android.os.Bundle;
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
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.datepicker.MaterialDatePicker;

import java.util.ArrayList;

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
        if (selectedId != -1) {
            RadioButton selectedRadioButton = findViewById(selectedId);
            String selectedGender = selectedRadioButton.getText().toString();
            Toast.makeText(this, "Selected Gender: " + selectedGender, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "No gender selected", Toast.LENGTH_SHORT).show();
        }

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