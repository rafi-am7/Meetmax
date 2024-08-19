package models;

import java.util.ArrayList;

public class FeedPostModel {
    private String username, timestamp,postDescription, profileImage,uid,privacy;
    private String feeling;
    private ArrayList<String> postImages;
    private String likeCount,commentCount,shareCount;
    private String postId="";


    public FeedPostModel(String username, String timestamp, String postDescription, String profileImage,
                         String uid, String privacy,String feeling, ArrayList<String> postImages,
                         String likeCount, String commentCount, String shareCount) {
        this.username = username;
        this.timestamp = timestamp;
        this.postDescription = postDescription;
        this.profileImage = profileImage;
        this.uid = uid;
        this.privacy = privacy;
        this.feeling = feeling;
        this.postImages = postImages;
        this.likeCount = likeCount;
        this.commentCount = commentCount;
        this.shareCount = shareCount;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public FeedPostModel() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public ArrayList<String> getPostImages() {
        return postImages;
    }

    public void setPostImages(ArrayList<String> postImages) {
        this.postImages = postImages;
    }

    public String getPrivacy() {
        return privacy;
    }

    public void setPrivacy(String privacy) {
        this.privacy = privacy;
    }

    public String getPostDescription() {
        return postDescription;
    }

    public void setPostDescription(String postDescription) {
        this.postDescription = postDescription;
    }

    public String getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(String likeCount) {
        this.likeCount = likeCount;
    }

    public String getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(String commentCount) {
        this.commentCount = commentCount;
    }

    public String getShareCount() {
        return shareCount;
    }

    public void setShareCount(String shareCount) {
        this.shareCount = shareCount;
    }

    public String getFeeling() {
        return feeling;
    }

    public void setFeeling(String feeling) {
        this.feeling = feeling;
    }
}
