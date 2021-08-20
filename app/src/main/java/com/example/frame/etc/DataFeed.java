package com.example.frame.etc;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

public class DataFeed implements Serializable {

    //private String title;
    private String writer;
    private String feed_user_id;
    //private ArrayList<String> contents;
    private String contents;
    private boolean isChecked = false;

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public String getFeed_user_id() {
        return feed_user_id;
    }

    public void setFeed_user_id(String feed_user_id) {
        this.feed_user_id = feed_user_id;
    }

    private String date;
    private String feed_id;
    private String feed_img;

    private String userImg;
    private String feed_uid;

    public String getFeed_uid() {
        return feed_uid;
    }

    public void setFeed_uid(String feed_uid) {
        this.feed_uid = feed_uid;
    }

    private String user_id;
    private ArrayList<String> images;
//private ArrayList<String> formats;// 전체 포맷 어레이

    private ArrayList<DataFeedImg> dataFeedImgList;

    public DataFeed(){

    }


    public DataFeed(String writer,String contents,String feed_img, String date, String userImg ) {
        this.writer = writer;
        this.contents = contents;
        this.feed_img = feed_img;
        this.date = date;
        this.userImg = userImg;

    }

    public DataFeed(String writer,String contents,ArrayList<DataFeedImg> dataFeedImgList, String date, String userImg, String feed_user_id,String feed_uid) {
        this.writer = writer;
        this.contents = contents;
        this.dataFeedImgList = dataFeedImgList;
        this.date = date;
        this.userImg = userImg;
        this.feed_user_id = feed_user_id;
        this.feed_uid = feed_uid;

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

    public void setImages(ArrayList<String> images) {
        this.images = images;
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

    public ArrayList<String> getImages() {
        return images;
    }

    public ArrayList<DataFeedImg> getDataFeedImgList() {
        return dataFeedImgList;
    }

    public void setDataFeedImgList(ArrayList<DataFeedImg> dataFeedImgList) {
        this.dataFeedImgList = dataFeedImgList;
    }
}
