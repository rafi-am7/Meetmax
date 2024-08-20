package com.example.meetmax;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import models.StoryModel;

public class StoryDetailsActivity extends AppCompatActivity {

    private ImageView storyImageView;
    private TextView userNameTextView;
    private FirebaseFirestore firestore;
    private String storyId;
    private ImageButton closeButon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story_details);

        storyImageView = findViewById(R.id.story_image_view);
        closeButon = findViewById(R.id.close_button);
        //userNameTextView = findViewById(R.id.story_user_name);

        firestore = FirebaseFirestore.getInstance();
        storyId = getIntent().getStringExtra("STORY_ID");

        if (storyId != null) {
            loadStoryDetails();
        }
        closeButon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void loadStoryDetails() {
        firestore.collection("Stories").document(storyId).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            StoryModel story = document.toObject(StoryModel.class);
                            if (story != null) {
                                displayStoryDetails(story);
                            }
                        }
                    } else {
                        // Handle error
                    }
                });
    }

    private void displayStoryDetails(StoryModel story) {
        Glide.with(this).load(story.getStoryImageUrl()).into(storyImageView);
      //  userNameTextView.setText(story.getUserName());
    }
}
