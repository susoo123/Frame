package com.example.frame.etc;

import java.io.Serializable;

//이벤트 명단
public class DataRegister implements Serializable {
    private String user_name;
    private String user_email;
    private String user_id;
    private String send_check;

    //model class
    private boolean isChecked = false;
    private boolean SentTicket = false;

    public boolean isSentTicket() {
        return SentTicket;
    }

    public void setSentTicket(boolean sentTicket) {
        SentTicket = sentTicket;
    }

    public boolean isChecked() {
       return isChecked;
    }

    public void setChecked(boolean checked){
        isChecked = checked;
    }

    public DataRegister(){

    }

    public DataRegister(String user_id, String user_name, String user_email) {
        this.user_id = user_id;
        this.user_name = user_name;
        this.user_email = user_email;
    }

    public DataRegister(String user_id, String user_name, String user_email, String send_check) {
        this.user_id = user_id;
        this.user_name = user_name;
        this.user_email = user_email;
        this.send_check = send_check;
    }


    public String getSend_check() {
        return send_check;
    }

    public void setSend_check(String send_check) {
        this.send_check = send_check;
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
}
