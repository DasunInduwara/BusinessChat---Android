package com.example.businesschat.models;

public class UsersModel {

   private String uId;
   private String phoneNumber;
   private String email;
   private String displayName;
   private String identification;
   private String imgUrl;

    public UsersModel(String uId, String phoneNumber, String email, String  displayName,String identification, String imgUrl) {
        this.uId = uId;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.displayName = displayName;
        this.identification = identification;
        this.imgUrl = imgUrl;
    }

    public UsersModel() {
    }

    public String getuId() {
        return uId;
    }

    public void setuId(String uId) {
        this.uId = uId;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getIdentification() {
        return identification;
    }

    public void setIdentification(String identification) {
        this.identification = identification;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }
}
