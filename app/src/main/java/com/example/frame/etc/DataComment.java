package com.example.frame.etc;

public class DataComment {
    private String comment_img;
    private String comment_name;
    private String comment_text;
    private String comment_date;
    private String comment_id;

    public DataComment(String comment_img, String comment_name, String comment_text, String comment_date, String comment_id) {
        this.comment_img = comment_img;
        this.comment_name = comment_name;
        this.comment_text = comment_text;
        this.comment_date = comment_date;
        this.comment_id = comment_id;
    }

    public String getComment_img() {
        return comment_img;
    }

    public void setComment_img(String comment_img) {
        this.comment_img = comment_img;
    }

    public String getComment_name() {
        return comment_name;
    }

    public void setComment_name(String comment_name) {
        this.comment_name = comment_name;
    }

    public String getComment_text() {
        return comment_text;
    }

    public void setComment_text(String comment_text) {
        this.comment_text = comment_text;
    }

    public String getComment_date() {
        return comment_date;
    }

    public void setComment_date(String comment_date) {
        this.comment_date = comment_date;
    }
}
