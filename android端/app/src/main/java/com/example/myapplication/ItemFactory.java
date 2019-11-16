package com.example.myapplication;


import java.io.Serializable;

public class ItemFactory implements Serializable {

    public String getObjectId() {
        return ObjectId;
    }

    public void setObjectId(String objectId) {
        ObjectId = objectId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    enum TYPE {HEAD,IMAGE,DETAIL}

    private String title;
    private TYPE type;
    private float scale;
    private int column;
    private float textSize;
    private String ObjectId;
    private String userId;
    private String username;

    public ItemFactory(TYPE type, String ObjectId, float scale, int column){
        this.type = type;
        this.ObjectId = ObjectId;
        this.scale = scale;
        this.column = column;
    }

    public ItemFactory(TYPE type, String ObjectId, float scale, int column, String userId, String username){
        this.type = type;
        this.ObjectId = ObjectId;
        this.scale = scale;
        this.column = column;
        this.userId = userId;
        this.username = username;
    }

    public ItemFactory(TYPE type, String title, float textSize){
        this.type = type;
        this.title = title;
        this.textSize = textSize;
    }
    public ItemFactory(TYPE type, String title, float textSize, String userId, String username){
        this.type = type;
        this.title = title;
        this.textSize = textSize;
        this.userId = userId;
        this.username = username;
    }

    public String getURL(){
        return "http://39.106.50.33:3000/images/"+ObjectId+".jpg";
    }

    public float getScale() {
        return scale;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public TYPE getType() {
        return type;
    }

    public void setType(TYPE type) {
        this.type = type;
    }


    public int getColumn() {
        return column;
    }

    public void setColumn(int column) {
        this.column = column;
    }

    public float getTextSize() {
        return textSize;
    }

    public void setTextSize(float textSize) {
        this.textSize = textSize;
    }
}
