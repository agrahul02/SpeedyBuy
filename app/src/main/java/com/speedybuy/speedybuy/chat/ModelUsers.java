package com.speedybuy.speedybuy.chat;

public class ModelUsers {
    String name;
    String description;
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    public ModelUsers() {
    }
    String onlineStatus;
    String typingTo;
    String email;
    String image;
    String uid;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public ModelUsers(String name, String onlineStatus, String typingTo, String email, String image, String uid) {
        this.name = name;
        this.onlineStatus = onlineStatus;
        this.typingTo = typingTo;
        this.email = email;
        this.image = image;
        this.uid = uid;
    }
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    String title;
    public ModelUsers(String description, String title) {
        this.description = description;

        this.title = title;

    }

}
