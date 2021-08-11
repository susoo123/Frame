package com.example.frame.etc;

public class HomePosterItem {

    private int image;
    private String title;
    private String place;

    public HomePosterItem(int image, String title, String place){
        this.image = image;
        this.title = title;
        this.place = place;
    }

    public int getImage() {
        return image;
    }

    public String getTitle() {
        return title;
    }

    public String getPlace() {
        return place;
    }
}
