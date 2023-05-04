package com.example.businesschat.models;

import java.io.Serializable;

public class Messages implements Serializable {
    String key;
    String message;
    String senderId;
    String mType;
    String price;
    String productName;
    String description;
    String img;
    //
    String bankSlipImg;
    String depositAmount;
    String depositBank;
    String accountHolderName;
    String accountNumber;
    String isValid;
    long timeStamp;

    public Messages() {
    }

    public Messages(String key, String message, String senderId, String type, String price, String productName, String description, String img, String bankSlipImg, String depositAmount, String depositBank, String accountHolderName, String accountNumber,String isValid ,long timeStamp) {
        this.key = key;
        this.message = message;
        this.senderId = senderId;
        this.mType = type;
        this.price = price;
        this.productName = productName;
        this.description = description;
        this.img = img;
        this.bankSlipImg = bankSlipImg;
        this.depositAmount = depositAmount;
        this.depositBank = depositBank;
        this.accountHolderName = accountHolderName;
        this.accountNumber = accountNumber;
        this.isValid = isValid;
        this.timeStamp = timeStamp;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getType() {
        return mType;
    }

    public void setType(String type) {
        mType = type;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public String getBankSlipImg() {
        return bankSlipImg;
    }

    public void setBankSlipImg(String bankSlipImg) {
        this.bankSlipImg = bankSlipImg;
    }

    public String getDepositAmount() {
        return depositAmount;
    }

    public void setDepositAmount(String depositAmount) {
        this.depositAmount = depositAmount;
    }

    public String getDepositBank() {
        return depositBank;
    }

    public void setDepositBank(String depositBank) {
        this.depositBank = depositBank;
    }

    public String getAccountHolderName() {
        return accountHolderName;
    }

    public void setAccountHolderName(String accountHolderName) {
        this.accountHolderName = accountHolderName;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getIsValid() {
        return isValid;
    }

    public void setIsValid(String isValid) {
        this.isValid = isValid;
    }
}
