package com.example.frame.etc;

import java.util.ArrayList;
import java.util.Date;

public class DataFeed {

    //private String title;
    private String writer;
    //private ArrayList<String> contents;
    private String contents;
    private String date;
    private String feed_id;
    private String feed_img;

    private String userImg;
    private String user_id;
//private ArrayList<String> formats;// 전체 포맷 어레이


    public DataFeed(String writer,String contents,String feed_img, String date, String userImg ) {
        this.writer = writer;
        this.contents = contents;
        this.feed_img = feed_img;
        this.date = date;
        this.userImg = userImg;

    }

    public String getWriter() {
        return writer;
    }

    public String getContents() {
        return contents;
    }
    //    public ArrayList<String> getContents() {
//        return contents;
//    }

//    public Date getCreatedAt() {
//        return createdAt;

    public String getFeed_img() {
        return feed_img;
    }
//    }

    public String getDate() {
        return date;
    }

    public String getFeed_id() {
        return feed_id;
    }

    public String getUserImg() {
        return userImg;
    }

    public String getUser_id() { //user_id를 가지고 서버에서 유저 프로필이미지랑, 유저 네임 가져오기
        return user_id;
    }
}
