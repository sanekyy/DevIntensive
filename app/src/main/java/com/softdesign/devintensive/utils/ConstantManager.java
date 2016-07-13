package com.softdesign.devintensive.utils;

/**
 * Created by ihb on 23.06.16.
 */
public interface ConstantManager {
    String TAG_PREFIX = "DEV ";

    String EDIT_MODE_KEY = "EDIT_MODE_KEY";

    String USER_PHONE_KEY = "USER_KEY_1";
    String USER_MAIL_KEY = "USER_KEY_2";
    String USER_VK_KEY = "USER_KEY_3";
    String USER_GIT_KEY = "USER_KEY_4";
    String USER_ABOUT_KEY = "USER_KEY_5";
    String USER_PHOTO_KEY = "USER_KEY_6";
    String USER_AVATAR_KEY = "USER_KEY_7";
    String AUTH_TOKEN_KEY = "AUTH_TOKEN_KEY";
    String USER_ID_KEY = "USER_ID_KEY";
    String USER_RATING_VALUE_KEY = "USER_RATING_VALUE_KEY";
    String USER_CODE_LINES_VALUE_KEY = "USER_CODE_LINES_VALUE_KEY";
    String USER_PROJECTS_KEY = "USER_PROJECTS_KEY";

    int LOAD_PROFILE_PHOTO = 1;
    int REQUEST_CAMERA_PICTURE = 99;
    int REQUEST_GALLERY_PICTURE = 88;

    int PERMISSION_REQUEST_SETTINGS_CODE=101;
    int CAMERA_REQUEST_PERMISSION_CODE=102;
    int GALLERY_REQUEST_PERMISSION_CODE=103;
}
