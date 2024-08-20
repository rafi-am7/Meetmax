package adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.meetmax.R;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import models.StoryModel;

public class StoryAdapter extends RecyclerView.Adapter<StoryAdapter.StoryViewHolder> {

    private Context context;
    private ArrayList<StoryModel> storyList;
    private OnStoryClickListener onStoryClickListener;

    public StoryAdapter(Context context, ArrayList<StoryModel> storyList) {
        this.context = context;
        this.storyList = storyList;
    }

    @NonNull
    @Override
    public StoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.story_item, parent, false);
        return new StoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StoryViewHolder holder, int position) {
        StoryModel story = storyList.get(position);

        if (position == 0) {
            // First item is the upload button
        } else {

            if (story.getStoryImageUrl() != null && !story.getStoryImageUrl().isEmpty()) {
                Glide.with(context).load(story.getStoryImageUrl()).into(holder.storyImage);
            } else {
                holder.storyImage.setImageResource(R.drawable.profile_photo);
            }
        }

        holder.itemView.setOnClickListener(v -> {
            if (onStoryClickListener != null) {
                onStoryClickListener.onStoryClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return storyList.size();
    }

    public void setOnStoryClickListener(OnStoryClickListener onStoryClickListener) {
        this.onStoryClickListener = onStoryClickListener;
    }

    public interface OnStoryClickListener {
        void onStoryClick(int position);
    }

    static class StoryViewHolder extends RecyclerView.ViewHolder {
        LinearLayout storyUploadLayout;
        CircleImageView storyImage;

        public StoryViewHolder(@NonNull View itemView) {
            super(itemView);
            storyUploadLayout = itemView.findViewById(R.id.story_upload_layout);
            storyImage = itemView.findViewById(R.id.story_profile_pic);
        }
    }
}
