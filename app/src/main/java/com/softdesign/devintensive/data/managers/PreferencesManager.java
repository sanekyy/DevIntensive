package com.softdesign.devintensive.data.managers;

import android.content.SharedPreferences;
import android.net.Uri;

import com.softdesign.devintensive.utils.ConstantManager;
import com.softdesign.devintensive.utils.DevIntensiveApplication;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ihb on 29.06.16.
 */

/**
 * Менеджер пользовательских "настроек/небольших даных"
 */
public class PreferencesManager {

    private SharedPreferences mSharedPreferences;

    // Список ключей
    private static final String[] USER_FIELDS = {
            ConstantManager.USER_PHONE_KEY,
            ConstantManager.USER_MAIL_KEY,
            ConstantManager.USER_VK_KEY,
            ConstantManager.USER_GIT_KEY,
            ConstantManager.USER_ABOUT_KEY
    };

    private static final String[] USER_VALUES = {
            ConstantManager.USER_RATING_VALUE_KEY,
            ConstantManager.USER_CODE_LINES_VALUE_KEY,
            ConstantManager.USER_PROJECTS_KEY
    };


    public PreferencesManager(){
        this.mSharedPreferences = DevIntensiveApplication.getSharedPreferences();
    }

    /**
     * Сохраняем даные пользователя в mSharedPreferences,
     * парсим список с помощью ключей в USER_FIELD
     * @param userFields список с пользовательскими данными, которые нужно сохранить
     */
    public void saveUserProfileData(List<String> userFields){
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        for(int i=0; i<USER_FIELDS.length;i++){
            editor.putString(USER_FIELDS[i],userFields.get(i));
        }
        editor.apply();
    }

    /**
     * Подгружаем из mSharedPreferences данные пользователя по ключам в USER_FIELD
     * @return пользовательские данные
     */
    public List<String> loadUserProfileData(){
        List<String> userFields = new ArrayList<>();
        for(int i=0; i<USER_FIELDS.length;i++) {
            userFields.add(mSharedPreferences.getString(USER_FIELDS[i], "null"));
        }
        return userFields;
    }

    /**
     * Сохраняем ссылку на фото пользователя в mSharedPreferences
     * @param uri
     */
    public void saveUserPhoto(Uri uri){
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(ConstantManager.USER_PHOTO_KEY,uri.toString());
        editor.apply();
    }

    /**
     * Подгружаем ссылку на фото пользователя из ресурсов приложения
     * @return
     */
    public Uri loadUserPhoto(){
        return Uri.parse(mSharedPreferences.getString(ConstantManager.USER_PHOTO_KEY,"android.resource://com.softdesign.devintensive/drawable/user_photo"));
    }

    public void saveUserAvatar(Uri uri){
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(ConstantManager.USER_AVATAR_KEY,uri.toString());
        editor.apply();
    }

    public Uri loadUserAvatar(){
        return Uri.parse(mSharedPreferences.getString(ConstantManager.USER_AVATAR_KEY,"android.resource://com.softdesign.devintensive/drawable/user_photo"));
    }

    public void saveUserProfileValues(int[] userValues){
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        for(int i=0; i<USER_VALUES.length;i++){
            editor.putString(USER_VALUES[i],String.valueOf(userValues[i]));
        }
        editor.apply();
    }

    public List<String> loadUserProfileValues(){
        List<String> userValues = new ArrayList<>();
        for(int i=0; i<USER_VALUES.length;i++) {
            userValues.add(mSharedPreferences.getString(USER_VALUES[i], "null"));
        }
        return userValues;
    }

    public void saveAuthToken(String authToken){
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(ConstantManager.AUTH_TOKEN_KEY, authToken);
        editor.apply();
    }

    public String getAuthToken(){
        return mSharedPreferences.getString(ConstantManager.AUTH_TOKEN_KEY,"null");
    }

    public void saveUserId(String userId){
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(ConstantManager.USER_ID_KEY, userId);
        editor.apply();
    }

    public String getUserId(){
        return mSharedPreferences.getString(ConstantManager.USER_ID_KEY, "null");
    }
}
