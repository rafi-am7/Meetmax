package adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;


import com.bumptech.glide.Glide;
import com.example.meetmax.R;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import models.FeedPostModel;

public class FeedPostAdapter extends RecyclerView.Adapter<FeedPostAdapter.FeedPostViewHolder> {
    private static FeedPostAdapter.CustomClickListener mCustomClickListener;
    private final ArrayList<FeedPostModel> feedPostModelArrayList;
    private FeedPostModel feedPostModel;
    private  String dir;
    private final Context mContext;
    //private final int bookedColor;
    //private final int unBookedColor;

    //constructor
    public FeedPostAdapter(Context context, ArrayList<FeedPostModel> directorAdminClassArrayList) {
        this.feedPostModelArrayList = directorAdminClassArrayList;
        this.mContext = context;
       // this.bookedColor = bookedColor;
        //this.unBookedColor = unbookedColor;
    }

    public void setCustomClickListener(FeedPostAdapter.CustomClickListener customClickListener) //called from mainactivity
    {
        mCustomClickListener = customClickListener; //setting data
    }

    @NonNull
    @Override
    public FeedPostAdapter.FeedPostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {  //an object of roomview holder which contain itemview
        View view = LayoutInflater.from(mContext).inflate(R.layout.feed_post_item, parent, false);
        return new FeedPostAdapter.FeedPostViewHolder(view); //passed in itemview

    }

    @Override
    public void onBindViewHolder(@NonNull FeedPostAdapter.FeedPostViewHolder holder, int position) {

        feedPostModel =  feedPostModelArrayList.get(position);
        holder.postUsernameTextView.setText(feedPostModel.getUsername());
        holder.postTimestampTextView.setText(feedPostModel.getTimestamp());
        holder.postDescriptionTextView.setText(feedPostModel.getPostDescription());
        holder.postPrivacyTextView.setText(feedPostModel.getPrivacy());

        switch (feedPostModel.getLikeCount()){
            case "0":
                holder.likeCountTextView.setVisibility(View.INVISIBLE);
                break;
            case "1":
                holder.likeCountTextView.setText(""+feedPostModel.getLikeCount()+" Like");
                break;
            default:
                holder.likeCountTextView.setText(""+feedPostModel.getLikeCount()+" Likes");
                break;
        }
        switch (feedPostModel.getCommentCount()){
            case "0":
                holder.commentCountTextView.setVisibility(View.INVISIBLE);
                break;
            case "1":
                holder.commentCountTextView.setText(""+feedPostModel.getCommentCount()+" Comment");
                break;
            default:
                holder.commentCountTextView.setText(""+feedPostModel.getCommentCount()+" Comments");
                break;
        }
        switch (feedPostModel.getCommentCount()){
            case "0":
                holder.shareCountTextView.setVisibility(View.INVISIBLE);
                break;
            case "1":
                holder.shareCountTextView.setText(""+feedPostModel.getShareCount()+" Share");
                break;
            default:
                holder.shareCountTextView.setText(""+feedPostModel.getShareCount()+" Shares");
                break;
        }

        Glide.with(mContext.getApplicationContext())
                .load(feedPostModel.getProfileImage())
                .placeholder(R.drawable.profile_photo)
                .timeout(6500)
                .into(holder.postProfilePicture);

        int color = Color.argb(255,255,150,240);
        Glide.with(mContext.getApplicationContext())
                .load(feedPostModel.getPostImages())
                .placeholder(new ColorDrawable(color))
                .timeout(7000)
                .into(holder.postImageView);
        holder.likeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCustomClickListener != null) {
                    //int position = holder.getAdapterPosition();
                    if (holder.getAdapterPosition() != RecyclerView.NO_POSITION) {
                        mCustomClickListener.onLikeClick(holder.getAdapterPosition());
                    }
                }

            }
        });
        holder.commentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCustomClickListener != null) {
                    //int position = holder.getAdapterPosition();
                    if (holder.getAdapterPosition() != RecyclerView.NO_POSITION) {
                        mCustomClickListener.onCommentClick(holder.getAdapterPosition());
                    }
                }

            }
        });
        holder.shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCustomClickListener != null) {
                    //int position = holder.getAdapterPosition();
                    if (holder.getAdapterPosition() != RecyclerView.NO_POSITION) {
                        mCustomClickListener.onShareClick(holder.getAdapterPosition());
                    }
                }

            }
        });
        /////
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
        //declaring method which will provide to main activity //position and view will also be provided
    }

    public class FeedPostViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        private final TextView postUsernameTextView, postTimestampTextView, postPrivacyTextView, postDescriptionTextView,
                likeCountTextView, commentCountTextView, shareCountTextView;
        private final ImageView postImageView;
        private final CircleImageView postProfilePicture;
        private final Button likeButton, commentButton, shareButton;

        public FeedPostViewHolder(@NonNull View itemView) {
            super(itemView);
//            roomNameTextView = itemView.findViewById(R.id.tvRoomName);
//            containerCardView = itemView.findViewById(R.id.llContainerCardView);
//            roomRatingTextView = itemView.findViewById(R.id.tvRoomRating);
            //  containerCardView=itemView.findViewById(R.id.llContainerCardView);


            /* -------------------------------------- */


            postUsernameTextView=itemView.findViewById(R.id.post_item_username_textview);
            postTimestampTextView= itemView.findViewById(R.id.post_item_timestamp_textview);
            postDescriptionTextView=itemView.findViewById(R.id.post_description_textview);
            postImageView = itemView.findViewById(R.id.post_imageview);
            postProfilePicture = itemView.findViewById(R.id.post_profile_picture);
            likeButton = itemView.findViewById(R.id.post_like_button);
            commentButton=itemView.findViewById(R.id.post_comment_button);
            shareButton=itemView.findViewById(R.id.post_share_button);

            postPrivacyTextView=itemView.findViewById(R.id.post_item_privacy_textview);
            likeCountTextView = itemView.findViewById(R.id.post_like_count_textview);
            commentCountTextView = itemView.findViewById(R.id.post_comment_count_textview);
            shareCountTextView = itemView.findViewById(R.id.post_share_count_textview);

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);

        } //to create view of every list item

        @Override
        public void onClick(View view) {
            mCustomClickListener.customOnClick(getAdapterPosition(), view);  //position and view setting to provide to mainactivity


        }

        public boolean onLongClick(View view) {

            mCustomClickListener.customOnLongClick(getAdapterPosition(), view);  //position and view setting to provide to mainactivity
            return true;

        }
    }


}

