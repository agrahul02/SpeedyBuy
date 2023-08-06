package com.speedybuy.speedybuy.chat;

class ModelChatList {

    String description;
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public ModelChatList() {
    }

    public ModelChatList(String id) {
        this.id = id;
    }

    String id;
    String title;
    public ModelChatList(String description, String title) {
        this.description = description;

        this.title = title;

    }
}


