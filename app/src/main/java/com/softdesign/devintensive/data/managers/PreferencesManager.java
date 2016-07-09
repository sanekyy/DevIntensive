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
    private static final String[] USER_FIELDS = {ConstantManager.USER_PHONE_KEY, ConstantManager.USER_MAIL_KEY, ConstantManager.USER_VK_KEY, ConstantManager.USER_GIT_KEY, ConstantManager.USER_ABOUT_KEY};

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
}
