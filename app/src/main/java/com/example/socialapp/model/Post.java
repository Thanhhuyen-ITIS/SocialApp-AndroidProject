package com.example.socialapp.model;

import androidx.annotation.NonNull;

public class Post {
    private String description;
    private String imageUrl;
    private String publisher;
    private String postId;
    private Long timestamp;

    public Post() {
    }

    public Post(String description, String imageUrl, String publisher, String postId, Long timestamp) {
        this.description = description;
        this.imageUrl = imageUrl;
        this.publisher = publisher;
        this.postId = postId;

        this.timestamp = timestamp;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    @NonNull
    @Override
    public String toString() {
        return "Post{" +
                "description='" + description + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", publisher='" + publisher + '\'' +
                ", postId='" + postId + '\'' +
                '}';
    }
}
