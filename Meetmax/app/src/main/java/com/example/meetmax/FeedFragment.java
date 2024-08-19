package com.example.meetmax;

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
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import adapters.CommentAdapter;
import adapters.FeedPostAdapter;
import models.CommentModel;
import models.FeedPostModel;
import models.UserModel;

public class FeedFragment extends Fragment {
    private RecyclerView postRecyclerView,commentsRecyclerView;
    private FeedPostAdapter feedPostAdapter;
    private ArrayList<FeedPostModel> postArrayList;
    private FirebaseFirestore firestore;

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
        feedPostAdapter.setCustomClickListener(new FeedPostAdapter.CustomClickListener() {
            @Override
            public void customOnClick(int position, View v) {

            }

            @Override
            public void customOnLongClick(int position, View v) {

            }

            @Override
            public void onLikeClick(int position) {

            }

            @Override
            public void onCommentClick(int position) {
                //FeedPostModel selectedPost = postArrayList.get(position);
                //if(selectedPost.getCommentCount()!=null && Integer.parseInt(selectedPost.getCommentCount())>0)
                //{
                    //openCommentSection(selectedPost.getPostId());
               // }
            }

            @Override
            public void onShareClick(int position) {

            }
        });
    }

    private void init(View view) {
        postRecyclerView = view.findViewById(R.id.feed_post_recycler_view);
        postRecyclerView.setHasFixedSize(true);
        postRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        firestore = FirebaseFirestore.getInstance();

        postArrayList = new ArrayList<>();
        feedPostAdapter = new FeedPostAdapter(getContext(), postArrayList);
        postRecyclerView.setAdapter(feedPostAdapter);


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
                                    // This callback runs when the user details are fetched
                                    Log.d("gama", "   "+updatedPost.getPostId());
                                    postArrayList.add(updatedPost);
                                    feedPostAdapter.notifyDataSetChanged(); // Notify adapter about data change
                                });
                            }
                        }
                        feedPostAdapter.notifyDataSetChanged(); // Notify adapter about data change
                    } else {
                        // Handle the error
                        // For example, show a toast message or log the error
                    }
                });
    }
    private void getUserDetails(FeedPostModel postModel, String postId ,OnUserDetailsFetchedListener listener) {
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
                Toast.makeText(getContext(), "Failed to load!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Define the callback interface
    interface OnUserDetailsFetchedListener {
        void onUserDetailsFetched(FeedPostModel postModel);
    }

    public static String getTimeDifference(String timestamp) {
        DateTimeFormatter formatter = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        }
        else return "";
        LocalDateTime inputTime = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            inputTime = LocalDateTime.parse(timestamp, formatter);
        }
        else return "";

        LocalDateTime now = LocalDateTime.now();
        Duration duration = Duration.between(inputTime, now);

        long hours = duration.toHours();
        long minutes = duration.toMinutes() % 60;
        long seconds = duration.getSeconds() % 60;
        String res="";
        if(hours!=0){
            res+=hours;
            res+="h";
        }
        else if(minutes!=0){
            res+=minutes;
            res+="m";
        }
        else {
            res += seconds;
            res+="s";
        }
        //Log.d("gama", res);
            // Format the result as "Xh Ym Zs"
        return res;

    }

    // Function to open the comment section
    private void openCommentSection(String postId) {
        // Load the comments for the selected post
        firestore.collection("Posts")
                .document(postId)
                .collection("Comments")
                .orderBy("timestamp")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<CommentModel> comments = new ArrayList<>();
                        for (DocumentSnapshot document : task.getResult()) {
                            CommentModel comment = document.toObject(CommentModel.class);
                            if (comment != null) {
                                comments.add(comment);
                            }
                        }
                        // Show comments using a RecyclerView or a Dialog
                        showCommentDialog(comments);
                    } else {
                        Toast.makeText(getContext(), "Failed to load comments", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Function to show comments in a dialog or another fragment
    private void showCommentDialog(List<CommentModel> comments) {
        // Show the comment section UI
    }





    public static String getCurrentUserId() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();

        if (currentUser != null) {
            return currentUser.getUid(); // Returns the UID of the currently logged-in user
        } else {
            return null; // Return null if no user is logged in
        }
    }
    public static String getCurrentTimestamp() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            return LocalDateTime.now().format(formatter); // Returns the current timestamp in the specified format
        } else {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            return sdf.format(new Date()); // Returns the timestamp for older Android versions
        }
    }













}
