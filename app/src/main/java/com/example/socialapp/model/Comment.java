package com.example.socialapp.model;

public class Comment {

    private String id;
    private String comment;
    private String publisher;

    private Long timestamp;

    public Comment() {
    }

    public Comment(String id, String comment, String publisher, Long timestamp) {
        this.id = id;
        this.comment = comment;
        this.publisher = publisher;
        this.timestamp = timestamp;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public Long getTimestamp() {
        return timestamp;
    }

}
