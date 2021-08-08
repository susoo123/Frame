package com.example.frame.etc;

import android.widget.ImageView;


//search frag 리사이클러뷰 클래스!! 검색
public class DataModel {


    private int rv_img1;
    private String title;
    private String place;

    public DataModel(int rv_img1, String title,String place){
        this.rv_img1 = rv_img1;
        this.title = title;
        this.place = place;

    }

    public DataModel(String title,String place){
        this.title = title;
        this.place = place;

    }

    public int getRv_img1() {
        return rv_img1;
    }


    public String getPlace() {
        return place;
    }





    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
