package com.example.socialapp.model;

public class Message {
    private String id;
    private String sendId;
    private String ReceiveId;
    private String message;
    private Long timestamp;


    public Message() {
    }

    public Message(String id, String sendId, String receiveId, String message, Long timestamp) {
        this.id = id;
        this.sendId = sendId;
        ReceiveId = receiveId;
        this.message = message;
        this.timestamp = timestamp;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSendId() {
        return sendId;
    }

    public void setSendId(String sendId) {
        this.sendId = sendId;
    }

    public String getReceiveId() {
        return ReceiveId;
    }

    public void setReceiveId(String receiveId) {
        ReceiveId = receiveId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }
}
