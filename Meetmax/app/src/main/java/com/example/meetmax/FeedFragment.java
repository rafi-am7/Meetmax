package com.example.meetmax;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

import adapters.FeedPostAdapter;
import models.FeedPostModel;

public class FeedFragment extends Fragment {
    private RecyclerView postRecyclerView;
    FeedPostAdapter feedPostAdapter;
    private ArrayList<FeedPostModel> postArrayList;
    private FirebaseUser user;
    DocumentReference reference;

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

        //reference = FirebaseFirestore.getInstance().collection("Posts").document(user.getUid());

        postArrayList = new ArrayList<>();
        feedPostAdapter = new FeedPostAdapter(getContext(),postArrayList);
        postRecyclerView.setAdapter(feedPostAdapter);

        loadPostDataFromFirestore();
    }

    void init(View view)
    {
        postRecyclerView= view.findViewById(R.id.feed_post_recycler_view);
        postRecyclerView.setHasFixedSize(true);
        postRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        FirebaseAuth auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
    }
    private void loadPostDataFromFirestore()
    {
        postArrayList.add(new FeedPostModel("Rafi","2h","hello!","",
                "","12223","public","12","11","2"));

        postArrayList.add(new FeedPostModel("Rafi","2h","hello!","",
                "","12223","public","12","11","2"));

        postArrayList.add(new FeedPostModel("Rafi","2h","hello!","",
                "","12223","public","12","11","2"));

        postArrayList.add(new FeedPostModel("Rafi","2h","hello!","",
                "","12223","public","12","11","2"));

        postArrayList.add(new FeedPostModel("Rafi","2h","hello!","",
                "","12223","public","12","11","2"));

        feedPostAdapter.notifyDataSetChanged();
    }
}