package adapters;



import static com.example.meetmax.FeedFragment.getCurrentTimestamp;
import static com.example.meetmax.FeedFragment.getCurrentUserId;
import static com.example.meetmax.FeedFragment.getTimeDifference;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.meetmax.R;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import models.CommentModel;
import models.FeedPostModel;
import models.UserModel;

public class FeedPostAdapter extends RecyclerView.Adapter<FeedPostAdapter.FeedPostViewHolder> {

    private static CustomClickListener mCustomClickListener;
    private final ArrayList<FeedPostModel> feedPostModelArrayList;
    private final Context mContext;

    // Constructor
    public FeedPostAdapter(Context context, ArrayList<FeedPostModel> feedPostModelArrayList) {
        this.feedPostModelArrayList = feedPostModelArrayList;
        this.mContext = context;
    }

    public void setCustomClickListener(CustomClickListener customClickListener) {
        mCustomClickListener = customClickListener;
    }

    @NonNull
    @Override
    public FeedPostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.feed_post_item, parent, false);
        return new FeedPostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FeedPostViewHolder holder, int position) {
        FeedPostModel feedPostModel = feedPostModelArrayList.get(position);

        holder.postUsernameTextView.setText(feedPostModel.getUsername());
        holder.postTimestampTextView.setText(feedPostModel.getTimestamp());
        holder.postDescriptionTextView.setText(feedPostModel.getPostDescription());
        holder.postPrivacyTextView.setText(feedPostModel.getPrivacy());

        Glide.with(mContext)
                .load(feedPostModel.getProfileImage())
                .placeholder(R.drawable.profile_photo)
                .timeout(6500)
                .into(holder.postProfilePicture);

        holder.postImagesGrid.removeAllViews();

        ArrayList<String> postImages = feedPostModel.getPostImages();
        int numberOfImages = postImages.size();

        for (int i = 0; i < numberOfImages; i++) {
            ImageView imageView = new ImageView(mContext);
            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = 0;
            params.height = GridLayout.LayoutParams.WRAP_CONTENT;
            params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1, 1f);
            params.rowSpec = GridLayout.spec(GridLayout.UNDEFINED, 1, 1f);
            imageView.setLayoutParams(params);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            Glide.with(mContext)
                    .load(postImages.get(i))
                    .placeholder(new ColorDrawable(Color.GRAY))
                    .timeout(7000)
                    .into(imageView);
            holder.postImagesGrid.addView(imageView);
        }

        holder.likeCountTextView.setText(getCountText(feedPostModel.getLikeCount(), "Like"));
        holder.commentCountTextView.setText(getCountText(feedPostModel.getCommentCount(), "Comment"));
        holder.shareCountTextView.setText(getCountText(feedPostModel.getShareCount(), "Share"));

        holder.likeButton.setOnClickListener(v -> {
            if (mCustomClickListener != null && holder.getAdapterPosition() != RecyclerView.NO_POSITION) {
                //int position = holder.getAdapterPosition();
                FeedPostModel post = feedPostModelArrayList.get(position);
                boolean liked = false;//!post.isLikedByCurrentUser();
                //post.setLikedByCurrentUser(liked); // Update like status
                updateLikeButtonIcon(holder.likeButton, liked);
                liked=true;
                mCustomClickListener.onLikeClick(position);
            }
        });

        holder.commentButton.setOnClickListener(v -> {
            if (mCustomClickListener != null && holder.getAdapterPosition() != RecyclerView.NO_POSITION) {
                mCustomClickListener.onCommentClick(holder.getAdapterPosition());
            }
        });

        holder.shareButton.setOnClickListener(v -> {
            if (mCustomClickListener != null && holder.getAdapterPosition() != RecyclerView.NO_POSITION) {
                mCustomClickListener.onShareClick(holder.getAdapterPosition());
            }
        });
        // Toggle comment section visibility
        holder.commentButton.setOnClickListener(v -> {
            if (holder.commentLayout.getVisibility() == View.VISIBLE) {
                holder.commentLayout.setVisibility(View.GONE);
            } else {
                holder.commentLayout.setVisibility(View.VISIBLE);
            }
            if (holder.commentsRecyclerView.getVisibility() == View.VISIBLE) {
                holder.commentsRecyclerView.setVisibility(View.GONE);
            } else {
                holder.commentsRecyclerView.setVisibility(View.VISIBLE);
            }
        });

        // Initialize CommentAdapter
        CommentAdapter commentAdapter = new CommentAdapter(mContext, new ArrayList<>());
        holder.commentsRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        holder.commentsRecyclerView.setAdapter(commentAdapter);

        // Load comments and update the adapter
        loadComments(feedPostModel.getPostId(), comments -> commentAdapter.updateComments(comments));

        // Handle comment send button click
        holder.commentSendButton.setOnClickListener(v -> {
            String commentText = holder.commentEditText.getText().toString().trim();
            if (!commentText.isEmpty()) {
                submitComment(feedPostModel.getPostId(), commentText, commentAdapter);
                holder.commentEditText.setText(""); // Clear the input field
            }
        });



    }
    private void updateLikeButtonIcon(Button likeButton, boolean isLiked) {
        if (isLiked) {
            likeButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_like, 0, 0, 0);
            // Optionally, update like status in your data model and Firestore
        } else {
            likeButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_like_filled, 0, 0, 0);
            // Optionally, update like status in your data model and Firestore
        }
    }


    private void loadComments(String postId, OnCommentsLoadedListener listener) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Posts")
                .document(postId)
                .collection("Comments")
                .orderBy("timestamp")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        ArrayList<CommentModel> comments = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            CommentModel comment = document.toObject(CommentModel.class);
                            if (comment != null) {
                                getUserDetailsForComment(comment, updatedComment -> {
                                    Log.d("gama"," "+updatedComment.getUserId());
                                    Log.d("gama"," "+updatedComment.getCommentText());
                                    Log.d("gama"," "+updatedComment.getUsername());
                                    comments.add(updatedComment);
                                    if (comments.size() == task.getResult().size()) {
                                        // Ensure all comments are loaded before triggering the listener
                                        listener.onCommentsLoaded(comments);
                                    }
                                });

                            }
                        }
                        listener.onCommentsLoaded(comments);
                    } else {
                        Log.d("FeedPostAdapter", "Error getting comments: ", task.getException());
                    }
                });
    }
    private void getUserDetailsForComment(CommentModel commentModel, OnUserDetailsFetchedForCommentListener listener) {
        DocumentReference userRef = FirebaseFirestore.getInstance().
                collection("Users").document(commentModel.getUserId());

        userRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    UserModel user = document.toObject(UserModel.class);
                    if (user != null) {
                        commentModel.setUsername(user.getName());
                        commentModel.setUserProfileImage(user.getProfileImage());
                        commentModel.setTimestamp(getTimeDifference(commentModel.getTimestamp()));
                        listener.onUserDetailsFetched(commentModel); // Trigger callback with the updated commentModel
                    }
                } else {
                    Log.d("getUserDetailsForComment", "User document does not exist.");
                }
            } else {
                Log.d("getUserDetailsForComment", "Failed to load user details.", task.getException());
            }
        });
    }

    // Define the callback interface
    interface OnUserDetailsFetchedForCommentListener {
        void onUserDetailsFetched(CommentModel commentModel);
    }

    private void submitComment(String postId, String commentText, CommentAdapter commentAdapter) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CommentModel newComment = new CommentModel();
        newComment.setCommentText(commentText);
        newComment.setTimestamp(getCurrentTimestamp()); // Or use a proper timestamp
        newComment.setUserId(getCurrentUserId()); // Replace with actual user ID

        db.collection("Posts")
                .document(postId)
                .collection("Comments")
                .add(newComment)
                .addOnSuccessListener(documentReference -> {
                    Log.d("FeedPostAdapter", "Comment added successfully.");
                    Toast.makeText(mContext.getApplicationContext(),"Comment Added",Toast.LENGTH_SHORT).show();
                    // Reload comments here or notify the adapter
                    loadComments(postId, comments -> commentAdapter.updateComments(comments));
                })
                .addOnFailureListener(e -> Log.w("FeedPostAdapter", "Error adding comment", e));
    }

    private String getCountText(String count, String singular) {
        if (count.equals("0")) {
            return "";
        } else if (count.equals("1")) {
            return "1 " + singular;
        } else {
            return count + " " + singular + "s";
        }
    }

    @Override
    public int getItemCount() {
        return feedPostModelArrayList.size();
    }

    public interface CustomClickListener {
        void customOnClick(int position, View v);
        void customOnLongClick(int position, View v);
        void onLikeClick(int position);
        void onCommentClick(int position);
        void onShareClick(int position);
    }

    public interface OnCommentsLoadedListener {
        void onCommentsLoaded(ArrayList<CommentModel> comments);
    }

    public class FeedPostViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        private final TextView postUsernameTextView, postTimestampTextView, postPrivacyTextView, postDescriptionTextView,
                likeCountTextView, commentCountTextView, shareCountTextView;
        private final GridLayout postImagesGrid;
        private final CircleImageView postProfilePicture;
        private final Button likeButton, commentButton, shareButton;
        private final RecyclerView commentsRecyclerView;
        private final EditText commentEditText;
        private final ImageButton commentSendButton;
        private LinearLayout commentLayout;


        public FeedPostViewHolder(@NonNull View itemView) {
            super(itemView);

            postUsernameTextView = itemView.findViewById(R.id.post_item_username_textview);
            postTimestampTextView = itemView.findViewById(R.id.post_item_timestamp_textview);
            postDescriptionTextView = itemView.findViewById(R.id.post_description_textview);
            postImagesGrid = itemView.findViewById(R.id.post_images_grid);
            postProfilePicture = itemView.findViewById(R.id.post_profile_picture);
            likeButton = itemView.findViewById(R.id.post_like_button);
            commentButton = itemView.findViewById(R.id.post_comment_button);
            shareButton = itemView.findViewById(R.id.post_share_button);
            postPrivacyTextView = itemView.findViewById(R.id.post_item_privacy_textview);
            likeCountTextView = itemView.findViewById(R.id.post_like_count_textview);
            commentCountTextView = itemView.findViewById(R.id.post_comment_count_textview);
            shareCountTextView = itemView.findViewById(R.id.post_share_count_textview);
            commentsRecyclerView = itemView.findViewById(R.id.comments_recycler_view);
            commentEditText = itemView.findViewById(R.id.comment_edit_text);
            commentSendButton = itemView.findViewById(R.id.comment_send_button);
            commentLayout = itemView.findViewById(R.id.add_comment_layout);


            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mCustomClickListener != null) {
                mCustomClickListener.customOnClick(getAdapterPosition(), view);
            }
        }

        @Override
        public boolean onLongClick(View view) {
            if (mCustomClickListener != null) {
                mCustomClickListener.customOnLongClick(getAdapterPosition(), view);
                return true;
            }
            return false;
        }
    }
}
