package models;

public class StoryModel {
    private String storyId;
    private String userId;
    private String storyImageUrl;
    private String username;
    private String profileImageUrl;
    private String timestamp;
    private String privacy="";

    public StoryModel() {
        // Required empty constructor for Firestore
    }


    public StoryModel(String storyId, String userId, String storyImageUrl, String username, String profileImageUrl, String timestamp) {
        this.storyId = storyId;
        this.userId = userId;
        this.storyImageUrl = storyImageUrl;
        this.username = username;
        this.profileImageUrl = profileImageUrl;
        this.timestamp = timestamp;
    }

    public String getPrivacy() {
        return privacy;
    }

    public void setPrivacy(String privacy) {
        this.privacy = privacy;
    }

    // Getters and setters
    public String getStoryId() {
        return storyId;
    }

    public void setStoryId(String storyId) {
        this.storyId = storyId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getStoryImageUrl() {
        return storyImageUrl;
    }

    public void setStoryImageUrl(String storyImageUrl) {
        this.storyImageUrl = storyImageUrl;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
