package com.example.businesschat.models;

public class ContactsModel {
    String id;
    String imageUrl;
    String contactName;
    String lastMessage;
    String messageTime;
    String unseenMessages;

    public ContactsModel(String id, String imageUrl, String contactName, String lastMessage, String messageTime, String unseenMessages) {
        this.id = id;
        this.imageUrl = imageUrl;
        this.contactName = contactName;
        this.lastMessage = lastMessage;
        this.messageTime = messageTime;
        this.unseenMessages = unseenMessages;
    }

    public ContactsModel() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public String getMessageTime() {
        return messageTime;
    }

    public void setMessageTime(String messageTime) {
        this.messageTime = messageTime;
    }

    public String getUnseenMessages() {
        return unseenMessages;
    }

    public void setUnseenMessages(String unseenMessages) {
        this.unseenMessages = unseenMessages;
    }
}
