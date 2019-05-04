package com.example.dani.fitfork.Objetos;

/**
 * Created by Dani on 05/05/2018.
 */

public class Upload {
    private String mName;
    private String mImageUrl;
    private String mUser;
    private String hashTag;
    private String ingredients;
    private String instructions;

    public Upload() {
        //constructor vacio
    }


    public Upload(String name, String imageUrl, String user, String hashTag, String ingredients, String instructions) {
        if (name.trim().equals("")) {
            name = "No Name";
        }

        mName = name;
        mImageUrl = imageUrl;
        mUser = user;
        this.hashTag = hashTag;
        this.ingredients = ingredients;
        this.instructions = instructions;
    }

    public String getHashTag() {
        return hashTag;
    }

    public void setHashTag(String hashTag) {
        this.hashTag = hashTag;
    }

    public String getIngredients() {
        return ingredients;
    }

    public void setIngredients(String ingredients) {
        this.ingredients = ingredients;
    }

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getImageUrl() {
        return mImageUrl;
    }

    public void setImageUrl(String imageUrl) {
        mImageUrl = imageUrl;
    }

    public String getUser() {
        return mUser;
    }

    public void setUser(String user) {
        mUser = user;
    }
}