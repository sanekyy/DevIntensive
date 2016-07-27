package com.softdesign.devintensive.data.network.res;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by ihb on 20.07.16.
 */
public class UploadPhotoRes {
    @SerializedName("success")
    @Expose
    private boolean success;
    @SerializedName("data")
    @Expose
    private Data data;

    public boolean isSuccess() {
        return success;
    }

    public Data getData() {
        return data;
    }

    public class Data {
        @SerializedName("photo")
        @Expose
        private String photo;
        @SerializedName("updated")
        @Expose
        private String updated;

        public String getPhoto() {
            return photo;
        }
    }
}