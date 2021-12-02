package com.example.frame.etc;

import java.io.Serializable;

//이벤트 명단
public class DataRegister implements Serializable {
    private String user_name;
    private String user_email;
    private String user_id;

    //model class
    private boolean isChecked = false;

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
