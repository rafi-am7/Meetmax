package com.example.meetmax;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.UUID;

import adapters.SelectedImagesAdapter;
import de.hdodenhof.circleimageview.CircleImageView;
import models.FeedPostModel;

public class PostAddFragment extends Fragment {
    private CircleImageView writePostProfilePic, toolbarProfilePic;
    private EditText writePostDescriptionEditText;
    private Button liveVideoButton, imageButton, feelingButton;
    private AutoCompleteTextView privacyTextView;
    private MaterialButton postButton;
    private ArrayList<Uri> selectedImageList;
    private Uri imageUri;
    private ViewPager loadedFromGalleryViewpager;

    private SelectedImagesAdapter selectedImagesAdapter;

    private FirebaseAuth mAuth;
    private FirebaseFirestore firestore;
    private FirebaseStorage firebaseStorage;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_post_add, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init(view);

        imageButton.setOnClickListener(v -> {
            checkPermission();
            loadImageFromGallery();
        });

        postButton.setOnClickListener(v -> {
            if (selectedImageList != null && !selectedImageList.isEmpty()) {
                uploadImagesAndSavePost();
            } else {
                // Handle the case where no images are selected
                savePostToFirestore(new ArrayList<>()); // Empty list for images
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1 && data!=null && data.getClipData()!=null)
        {
            int count = data.getClipData().getItemCount();
            for(int i=0;i<count;i++)
            {
                imageUri=data.getClipData().getItemAt(i).getUri();
                selectedImageList.add(imageUri);
                selectedImagesAdapter.notifyDataSetChanged();
                loadedFromGalleryViewpager.setVisibility(View.VISIBLE);

            }
        }


    }
    private  void  checkPermission()
    {
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M)
        {
            if(ContextCompat.checkSelfPermission(getContext(),READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED)
            {
                ActivityCompat.requestPermissions(getActivity(), new String[]{READ_EXTERNAL_STORAGE}, 2);
            }
            else
            {
                loadImageFromGallery();
            }
        }
        else
        {
            loadImageFromGallery();
        }
    }



    void init(View view)
    {
        writePostProfilePic=view.findViewById(R.id.write_post_profile_picture);
        toolbarProfilePic= view.findViewById(R.id.feed_toolbar_profile_picture);
        writePostDescriptionEditText=view.findViewById(R.id.write_post_description_edit_text);
        liveVideoButton=view.findViewById(R.id.upload_live_video_button);
        imageButton=view.findViewById(R.id.upload_image_button);
        feelingButton=view.findViewById(R.id.upload_feeling_button);
        privacyTextView=view.findViewById(R.id.upload_post_privacy_text_view);
        postButton=view.findViewById(R.id.upload_post_button);
        loadedFromGalleryViewpager=view.findViewById(R.id.selected_image_viewpager);


        selectedImageList = new ArrayList<>();
        selectedImagesAdapter = new SelectedImagesAdapter(getContext(),selectedImageList);
        loadedFromGalleryViewpager.setAdapter(selectedImagesAdapter);

        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
    }
    void loadImageFromGallery()
    {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,1);

    }

    private void uploadImagesAndSavePost() {
        final ArrayList<String> imageUrls = new ArrayList<>();
        final int totalImages = selectedImageList.size();
        for (int i = 0; i < totalImages; i++) {
            Uri imageUri = selectedImageList.get(i);
            StorageReference imageRef = firebaseStorage.getReference().child("post_images/" + UUID.randomUUID().toString());
            imageRef.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        imageUrls.add(uri.toString());
                        if (imageUrls.size() == totalImages) {
                            savePostToFirestore(imageUrls);
                        }
                    }))
                    .addOnFailureListener(e -> {
                        // Handle unsuccessful uploads
                    });
        }
    }

    private void savePostToFirestore(ArrayList<String> imageUrls) {
        String username = mAuth.getCurrentUser().getDisplayName();
        String timestamp = String.valueOf(System.currentTimeMillis());
        String postDescription = writePostDescriptionEditText.getText().toString();
        String profileImage = mAuth.getCurrentUser().getPhotoUrl() != null ? mAuth.getCurrentUser().getPhotoUrl().toString() : "";
        String uid = mAuth.getCurrentUser().getUid();
        String privacy = privacyTextView.getText().toString();
        String feeling = "some_feeling"; // You might want to update this with actual feeling data
        String likeCount = "0";
        String commentCount = "0";
        String shareCount = "0";

        FeedPostModel postModel = new FeedPostModel(username, timestamp, postDescription, profileImage, uid, privacy, feeling, imageUrls, likeCount, commentCount, shareCount);
        firestore.collection("posts")
                .add(postModel)
                .addOnSuccessListener(documentReference -> {
                    clearAndNavigateToFeed();
                    Toast.makeText(getContext(),"Post added",Toast.LENGTH_SHORT).show();
                    // Handle success
                })
                .addOnFailureListener(e -> {
                    // Handle failure
                });
    }
    private void clearAndNavigateToFeed() {
        // Clear selected images
        selectedImageList.clear();
        selectedImagesAdapter.notifyDataSetChanged();
        loadedFromGalleryViewpager.setVisibility(View.GONE);

        // Clear input fields
        writePostDescriptionEditText.setText("");
        privacyTextView.setText("");

        // Navigate to FeedFragment
        if (getActivity() != null) {
            Fragment feedFragment = new FeedFragment();
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frame_layout_bottom_nav, feedFragment) // Replace with your container id
                    .commit();
        }
    }
}
