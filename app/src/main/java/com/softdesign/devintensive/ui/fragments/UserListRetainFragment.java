package com.softdesign.devintensive.ui.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.softdesign.devintensive.data.storage.models.User;

import java.util.List;

/**
 * Created by ihb on 20.07.16.
 */
public class UserListRetainFragment extends Fragment {
    private List<User> mUsers;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    public void setUserList(List<User> users) {
        mUsers = users;
    }

    public List<User> getUserList() {
        return mUsers;
    }

}
