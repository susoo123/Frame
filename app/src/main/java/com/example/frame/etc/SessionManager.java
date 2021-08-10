package com.example.frame.etc;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.example.frame.LoginActivity;
import com.example.frame.MainActivity;

import java.util.HashMap;

public class SessionManager {
    SharedPreferences sharedPreferences;
    public SharedPreferences.Editor editor;
    public Context context;
    int PRIVATE_MODE = 0;

    private static final String PREF_NAME = "LOGIN";
    private static final String LOGIN = "IS_LOGIN";
    public static final String NAME = "NAME";
    public static final String EMAIL = "EMAIL";
    public static final String ID = "ID";
    public static final String PROFILE_IMG_PATH = "PROFILE_IMG_PATH";
    public static final String ROLE = "ROLE";

    public SessionManager(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = sharedPreferences.edit(); //쉐어드 저장
    }

    public void createSession(String name, String email, String id, String profile_img , String role){
        editor.putBoolean(LOGIN, true);
        editor.putString(NAME, name);
        editor.putString(EMAIL, email);
        editor.putString(ID, id);
        editor.putString(PROFILE_IMG_PATH, profile_img);
        editor.putString(ROLE, role);
        editor.apply();
    }

    public void updateSherd(String name){

        editor.putString(NAME, name);
        //editor.putString(PROFILE_IMG_PATH, profile_img);
        editor.apply();
    }

    public boolean isLogin(){
        return sharedPreferences.getBoolean(LOGIN, false);
    }

    public void checkLogin(){

        if (!this.isLogin()){
            Intent i = new Intent(context, LoginActivity.class);
            context.startActivity(i);
            ((MainActivity) context).finish();
        }
    }

    public HashMap<String, String> getUserDetail(){

        HashMap<String, String> user = new HashMap<>();
        user.put(NAME, sharedPreferences.getString(NAME, null));
        user.put(EMAIL, sharedPreferences.getString(EMAIL, null));
        user.put(ID, sharedPreferences.getString(ID, null));
        user.put(PROFILE_IMG_PATH, sharedPreferences.getString(PROFILE_IMG_PATH, null));
        user.put(ROLE, sharedPreferences.getString(ROLE, null));

        return user;
    }

    public void logout(){
        editor.clear();
        editor.commit();
        Intent i = new Intent(context, LoginActivity.class);
        context.startActivity(i);
        ((MainActivity) context).finish();
    }

}
