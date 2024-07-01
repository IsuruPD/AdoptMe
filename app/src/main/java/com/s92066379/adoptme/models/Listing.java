package com.s92066379.adoptme.models;

public class Listing {
    private String petName;
    private String subTitle;
    private String timestamp;
    private String description;
    private String contact;
    private String imageUrl;

    public Listing() {
    }

    public Listing(String petName, String subTitle, String timestamp, String description, String contact, String imageUrl) {
        this.petName = petName;
        this.subTitle = subTitle;
        this.timestamp = timestamp;
        this.description = description;
        this.contact = contact;
        this.imageUrl = imageUrl;
    }

    public String getPetName() {
        return petName;
    }

    public void setPetName(String petName) {
        this.petName = petName;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
