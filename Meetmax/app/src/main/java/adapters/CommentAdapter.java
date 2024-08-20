package adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.meetmax.R;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import models.CommentModel;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {
    private final Context mContext;
    private final ArrayList<CommentModel> commentList;

    public CommentAdapter(Context context, ArrayList<CommentModel> commentList) {
        this.mContext = context;
        this.commentList = commentList;
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.comment_item, parent, false);
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        CommentModel comment = commentList.get(position);
        holder.usernameTextView.setText(comment.getUsername());
        holder.commentTextView.setText(comment.getCommentText());
        holder.timestampTextView.setText(comment.getTimestamp());

        Glide.with(mContext)
                .load(comment.getUserProfileImage())
                .placeholder(R.drawable.profile_photo)
                .timeout(6500)
                .into(holder.profileImageView);
    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }

    public void updateComments(ArrayList<CommentModel> comments) {
        this.commentList.clear();
        this.commentList.addAll(comments);
        notifyDataSetChanged();
    }

    class CommentViewHolder extends RecyclerView.ViewHolder {
        private final TextView usernameTextView;
        private final TextView commentTextView;
        private final TextView timestampTextView;
        private final CircleImageView profileImageView;

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            usernameTextView = itemView.findViewById(R.id.comment_username);
            commentTextView = itemView.findViewById(R.id.comment_text);
            timestampTextView = itemView.findViewById(R.id.comment_timestamp);
            profileImageView = itemView.findViewById(R.id.comment_item_user_image);
        }
    }
}
