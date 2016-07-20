package com.softdesign.devintensive.utils;

import okhttp3.HttpUrl;

/**
 * Created by ihb on 13.07.16.
 */
public interface AppConfig {
    String BASE_URL = "http://devintensive.softdesign-apps.ru/api/";
    int MAX_CONNECT_TIMEOUT = 5000;
    int MAX_READ_TIMEOUT = 5000;
    int START_DELAY = 1500;
    int SEARCH_DELAY = 1500;
}