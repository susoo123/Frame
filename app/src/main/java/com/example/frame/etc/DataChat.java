package com.example.frame.etc;

import java.io.Serializable;

public class DataChat implements Serializable {

    private String user_name; //유저이름
    private String user_email;
    private String user_id; //유저 아이디
    private String chat_date; //채팅 날짜
    private String chat_user_img; //유저 이미지
    private String chat_text;//채팅 내용
    private String type;//채팅 내용이 텍스트(0)인지 이미지(1)인지

    //model class
    private boolean isChecked = false;

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked){
        isChecked = checked;
    }

    public DataChat(){

    }



    public DataChat(String user_id, String user_name, String chat_user_img,String chat_text,String chat_date,String type) {
        this.user_id = user_id;
        this.user_name = user_name;
        this.chat_user_img = chat_user_img;
        this.chat_text = chat_text;
        this.chat_date = chat_date;
        this.type = type;

    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getUser_email() {
        return user_email;
    }

    public void setUser_email(String user_email) {
        this.user_email = user_email;
    }

    public String getChat_date() {
        return chat_date;
    }

    public void setChat_date(String chat_date) {
        this.chat_date = chat_date;
    }

    public String getChat_user_img() {
        return chat_user_img;
    }

    public void setChat_user_img(String chat_user_img) {
        this.chat_user_img = chat_user_img;
    }

    public String getChat_text() {
        return chat_text;
    }

    public void setChat_text(String chat_text) {
        this.chat_text = chat_text;
    }
}
