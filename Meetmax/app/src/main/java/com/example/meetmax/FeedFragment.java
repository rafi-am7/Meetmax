package com.example.meetmax;

import static android.app.Activity.RESULT_OK;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import adapters.CommentAdapter;
import adapters.FeedPostAdapter;
import adapters.StoryAdapter;
import models.CommentModel;
import models.FeedPostModel;
import models.StoryModel;
import models.UserModel;

public class FeedFragment extends Fragment {
    private static final int REQUEST_CODE_PICK_IMAGE = 1;

    private RecyclerView postRecyclerView, storyRecyclerView;
    private FeedPostAdapter feedPostAdapter;
    private StoryAdapter storyAdapter;
    private ArrayList<FeedPostModel> postArrayList;
    private FirebaseFirestore firestore;
    private ArrayList<StoryModel> storyArrayList;
    private ArrayList<Uri> selectedStoryImage;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_feed, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init(view);
        loadPostDataFromFirestore();
        loadStoryDataFromFirestore();

        storyAdapter.setOnStoryClickListener(position -> {
            if (position == 0) {
                // Handle "Add Story" button click
                loadImageFromGallery();
            } else {
                // Handle viewing the story
                StoryModel story = storyArrayList.get(position);
                showStoryDetails(story);
            }
        });

        feedPostAdapter.setCustomClickListener(new FeedPostAdapter.CustomClickListener() {
            @Override
            public void customOnClick(int position, View v) {
                // Implement custom click logic here
            }

            @Override
            public void customOnLongClick(int position, View v) {
                // Implement custom long click logic here
            }

            @Override
            public void onLikeClick(int position) {
                FeedPostModel feedPostModel = postArrayList.get(position);
                handleLikeClick(feedPostModel.getPostId());
                feedPostAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCommentClick(int position) {
                // Implement comment click logic here
            }

            @Override
            public void onShareClick(int position) {
                // Implement share click logic here
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_PICK_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri selectedImageUri = data.getData();
            if (selectedImageUri != null) {
                uploadImagesAndSavePost(selectedImageUri);
            }
        }
    }

    private void loadImageFromGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_CODE_PICK_IMAGE);
    }
    private void uploadImagesAndSavePost(Uri selectedImageUri) {
        final ArrayList<String> imageUrls = new ArrayList<>();

            Uri imageUri = selectedImageUri;
            StorageReference imageRef = FirebaseStorage.getInstance().getReference().child("post_images/" + UUID.randomUUID().toString());
            imageRef.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        uploadStory(uri.toString());
                    }))
                    .addOnFailureListener(e -> {
                        // Handle unsuccessful uploads
                    });
    }

    private void uploadStory(String storyUri) {
        String userId = getCurrentUserId();
        String storyId = UUID.randomUUID().toString();


        StoryModel story = new StoryModel(storyId, getCurrentUserId(), storyUri, "", "",
                getCurrentTimestamp());
        FirebaseFirestore.getInstance().collection("Stories").document(storyId).set(story)
                .addOnSuccessListener(aVoid -> Toast.makeText(getContext(), "Story uploaded", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Failed to upload story", Toast.LENGTH_SHORT).show());
    }

    private void loadPostDataFromFirestore() {
        firestore.collection("Posts")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<DocumentSnapshot> documents = task.getResult().getDocuments();
                        postArrayList.clear(); // Clear existing data
                        for (DocumentSnapshot document : documents) {
                            FeedPostModel post = document.toObject(FeedPostModel.class);
                            if (post != null) {
                                getUserDetails(post, document.getId(), updatedPost -> {
                                    postArrayList.add(updatedPost);
                                    feedPostAdapter.notifyDataSetChanged();
                                });
                            }
                        }
                        feedPostAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(getContext(), "Failed to load posts!", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void getUserDetails(FeedPostModel postModel, String postId, OnUserDetailsFetchedListener listener) {
        DocumentReference userRef = firestore.collection("Users").document(postModel.getUid());

        userRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    UserModel user = document.toObject(UserModel.class);
                    postModel.setUsername(user.getName());
                    postModel.setProfileImage(user.getProfileImage());
                    postModel.setTimestamp(getTimeDifference(postModel.getTimestamp()));
                    postModel.setPostId(postId);
                    listener.onUserDetailsFetched(postModel); // Trigger callback with the updated postModel
                } else {
                    Toast.makeText(getContext(), "Illegal User!", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getContext(), "Failed to load user details!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    interface OnUserDetailsFetchedListener {
        void onUserDetailsFetched(FeedPostModel postModel);
    }

    public static String getTimeDifference(String timestamp) {
        DateTimeFormatter formatter = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        }
        LocalDateTime inputTime = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            inputTime = LocalDateTime.parse(timestamp, formatter);
        }
        LocalDateTime now = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            now = LocalDateTime.now();
        }
        Duration duration = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            duration = Duration.between(inputTime, now);
        }

        long hours = 0;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            hours = duration.toHours();
        }
        long minutes = 0;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            minutes = duration.toMinutes() % 60;
        }
        long seconds = 0;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            seconds = duration.getSeconds() % 60;
        }
        String res = "";
        if (hours != 0) {
            res += hours + "h";
        } else if (minutes != 0) {
            res += minutes + "m";
        } else {
            res += seconds + "s";
        }
        return res;
    }

    public static String getCurrentUserId() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();
        return currentUser != null ? currentUser.getUid() : null;
    }

    public static String getCurrentTimestamp() {
        DateTimeFormatter formatter = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return LocalDateTime.now().format(formatter);
        }
        return "";
    }

    private void handleLikeClick(String postId) {
        String userId = getCurrentUserId();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference postRef = db.collection("Posts").document(postId);
        DocumentReference userLikesRef = db.collection("Users").document(userId).collection("PostLikes").document(postId);
        DocumentReference postLikersRef = postRef.collection("Likers").document(userId);

        postLikersRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                boolean alreadyLiked = task.getResult().exists();
                db.runTransaction(transaction -> {
                    DocumentSnapshot postSnapshot = transaction.get(postRef);

                    String currentLikeCountStr = postSnapshot.getString("likeCount");
                    int currentLikeCount = 0;
                    if (currentLikeCountStr != null) {
                        try {
                            currentLikeCount = Integer.parseInt(currentLikeCountStr);
                        } catch (NumberFormatException e) {
                            Log.w("FeedFragment", "Error parsing like count", e);
                        }
                    }
                    if (alreadyLiked) {
                        transaction.delete(postLikersRef); // Remove user from Likers
                        transaction.delete(userLikesRef); // Remove post from User's PostLikes
                        transaction.update(postRef, "likeCount", String.valueOf(currentLikeCount - 1)); // Decrement like count
                    } else {
                        transaction.set(postLikersRef, new HashMap<>()); // Add user to Likers
                        transaction.set(userLikesRef, new HashMap<>()); // Add post to User's PostLikes
                        transaction.update(postRef, "likeCount", String.valueOf(currentLikeCount + 1)); // Increment like count
                    }
                    return null;
                }).addOnSuccessListener(aVoid -> {
                    // Update successful
                }).addOnFailureListener(e -> {
                    // Handle failure
                });
            }
        });
    }

    private void showStoryDetails(StoryModel story) {
        Intent intent = new Intent(getActivity(), StoryDetailsActivity.class);
        intent.putExtra("STORY_ID", story.getStoryId());
        startActivity(intent);
    }

    private void init(View view) {
        firestore = FirebaseFirestore.getInstance();
        postArrayList = new ArrayList<>();
        selectedStoryImage = new ArrayList<>();

        postRecyclerView = view.findViewById(R.id.feed_post_recycler_view);
        postRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        feedPostAdapter = new FeedPostAdapter(getContext(),postArrayList);
        postRecyclerView.setAdapter(feedPostAdapter);

        storyArrayList = new ArrayList<>();
        storyRecyclerView = view.findViewById(R.id.story_recylerview);
        storyRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        storyAdapter = new StoryAdapter(getContext(),storyArrayList);
        storyRecyclerView.setAdapter(storyAdapter);
    }

    private void loadStoryDataFromFirestore() {
        storyArrayList.clear();
        storyArrayList.add(new StoryModel());
        firestore.collection("Stories")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<DocumentSnapshot> documents = task.getResult().getDocuments();
                        //storyArrayList.clear(); // Clear existing data
                        for (DocumentSnapshot document : documents) {
                            StoryModel story = document.toObject(StoryModel.class);
                            if (story != null) {
                                storyArrayList.add(story);
                            }
                        }
                        storyAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(getContext(), "Failed to load stories!", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
