package com.animir.tetris.model;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Create by Animir [2019.08.22]
 */
public class MainModel {

    private static MainModel mMainModel = null;
    private String key_Name = "TESTRIS_YYH";

    private MainModel() {}
    public static MainModel getInstance(){
        if(mMainModel == null)
            mMainModel = new MainModel();

        return mMainModel;
    }


    public void setStringPref(Context context, String key, String value){
        SharedPreferences preferences = context.getSharedPreferences(key_Name, context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, value);
        editor.commit();
    }
    public String getStringPref(Context context, String key){
        SharedPreferences preferences = context.getSharedPreferences(key_Name, context.MODE_PRIVATE);
        return preferences.getString(key,"");
    }

    public void setIntegerPref(Context context, String key, int value){
        SharedPreferences preferences = context.getSharedPreferences(key_Name, context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(key, value);
        editor.commit();
    }
    public int getIntegerPref(Context context, String key){
        SharedPreferences preferences = context.getSharedPreferences(key_Name, context.MODE_PRIVATE);
        return preferences.getInt(key,0);
    }

}
