package com.example.chat_android.Models;

public class Chat {

    private String sender;
    private String receiver;
    private String message;
    private boolean is_seen;

    public Chat(String sender, String receiver, String message, boolean is_seen) {
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
        this.is_seen = is_seen;
    }

    public Chat() {
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isIsseen() {
        return is_seen;
    }

    public void setIsseen(boolean is_seen) {
        this.is_seen = is_seen;
    }
}
