package com.example.socialapp.model;

public class Notification {
    private String userId;
    private String text;
    private String postId;
    private boolean isPost;
    private Long timestamp;

    public Notification(String userId, String text, String postId, boolean isPost, Long timestamp) {
        this.userId = userId;
        this.text = text;
        this.postId = postId;
        this.isPost = isPost;

        this.timestamp = timestamp;
    }

    public Notification() {
    }

    public String getUserId() {
        return userId;
    }

    public String getText() {
        return text;
    }

    public String getPostId() {
        return postId;
    }

    public boolean isPost() {
        return isPost;
    }

    public Long getTimestamp() {
        return timestamp;
    }
}
