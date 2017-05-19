package sdis.wetranslate.logic;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import sdis.wetranslate.LoginActivity;

public class User {

    private static User instance=null;

    private String username=null;
    private int currentRequestWatching=-1;

    protected User(String username){
        this.username=username;
    }

    public static User getInstance(){
        return instance;
    }

    public static void initSession(String username){
        instance=new User(username);
    }

    public String getUsername(){
        return username;
    }

    public void setCurrentRequestWatching(int requestWatching){
        this.currentRequestWatching=requestWatching;
    }

    public int getCurrentRequestWatching(){
        return currentRequestWatching;
    }

    public void resetUser(){
        instance=null;
        username=null;
        currentRequestWatching=-1;
    }

    public void saveSession(String username, String key, Activity activity){
        SharedPreferences sharedPreferences = activity.getSharedPreferences(LoginActivity.MyPREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(LoginActivity.Username,username);
        editor.putString(LoginActivity.KeyUser,key);
        editor.commit();
    }
}