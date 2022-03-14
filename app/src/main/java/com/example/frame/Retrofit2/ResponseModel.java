package com.example.frame.Retrofit2;


import com.google.gson.annotations.SerializedName;

public class ResponseModel {


    @SerializedName("feed_contents")
    String feed_contents;

    public String getTitle() {
        return feed_contents;
    }

    public void setTitle(String title) {
        this.feed_contents = title;
    }


}


