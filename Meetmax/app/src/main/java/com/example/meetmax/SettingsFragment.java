package com.example.meetmax;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class SettingsFragment extends Fragment {
    private Button signOutButton;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init(view);
        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signOut();

            }
        });


    }
    void init(View view)
    {
        signOutButton = view.findViewById(R.id.sign_out_button);

    }
    private void signOut() {
        FirebaseAuth.getInstance().signOut(); // signs out the current user
        saveSignInState(false);
        updateUI(); // update your UI or navigate the user to the login screen
    }
    private void updateUI() {
        Intent intent = new Intent(getContext(), SignIn.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        if(getActivity() != null) {
            getActivity().finish(); // If you're in a Fragment and want to close the current Activity
        }
    }
    private void saveSignInState(boolean isSignedIn) {
        SharedPreferences preferences = getActivity().getSharedPreferences("sign_in_prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("is_signed_in", isSignedIn);
        editor.apply();
    }

}