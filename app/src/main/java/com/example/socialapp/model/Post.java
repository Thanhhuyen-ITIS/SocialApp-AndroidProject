package com.example.socialapp.model;

import androidx.annotation.NonNull;

public class Post {
    private String description;
    private String imageUrl;
    private String publisher;
    private String postId;

    public Post() {
    }

    public Post(String description, String imageUrl, String publisher, String postId) {
        this.description = description;
        this.imageUrl = imageUrl;
        this.publisher = publisher;
        this.postId = postId;
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
