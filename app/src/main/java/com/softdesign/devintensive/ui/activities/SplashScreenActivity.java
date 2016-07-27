package com.softdesign.devintensive.ui.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.softdesign.devintensive.R;
import com.softdesign.devintensive.data.managers.DataManager;
import com.softdesign.devintensive.data.network.res.UserInfoRes;
import com.softdesign.devintensive.data.network.res.UserModelRes;
import com.softdesign.devintensive.utils.ConstantManager;
import com.softdesign.devintensive.utils.NetworkStatusChecker;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SplashScreenActivity extends AppCompatActivity {

    private DataManager mDataManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        mDataManager = DataManager.getInstance();

        if (mDataManager.getPreferencesManager().getAuthToken().isEmpty() || mDataManager.getPreferencesManager().getAuthToken() == "null") {
            startActivityForResult(new Intent(mDataManager.getContext(), AuthActivity.class), ConstantManager.AUTH_ACTIVITY_CODE);
        } else {
            signInByToken();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (resultCode) {
            case ConstantManager.EXIT_APP_CODE:
                finish();
                break;
            case ConstantManager.LOGOUT_CODE:
                mDataManager.getPreferencesManager().clear();
                startActivityForResult(new Intent(mDataManager.getContext(), AuthActivity.class), ConstantManager.AUTH_ACTIVITY_CODE);
                break;
            case ConstantManager.AUTH_ACTIVITY_COMPLETE_CODE:
                startActivityForResult(new Intent(mDataManager.getContext(), UserListActivity.class), ConstantManager.USER_LIST_ACTIVITY_CODE);
                break;
            case ConstantManager.MAIN_ACTIVITY_CODE:
                startActivityForResult(new Intent(mDataManager.getContext(), MainActivity.class), ConstantManager.MAIN_ACTIVITY_CODE);
                break;
            case ConstantManager.USER_LIST_ACTIVITY_CODE:
                startActivityForResult(new Intent(mDataManager.getContext(), UserListActivity.class), ConstantManager.USER_LIST_ACTIVITY_CODE);
                break;
            default:
                finish();
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void signInByToken() {
        if (NetworkStatusChecker.isNetworkAvailable(this)) {
            Call<UserInfoRes> call = mDataManager.loginToken(mDataManager.getPreferencesManager().getUserId());
            call.enqueue(new Callback<UserInfoRes>() {
                @Override
                public void onResponse(Call<UserInfoRes> call, Response<UserInfoRes> response) {
                    if (response.code() == 200) {
                        saveUserValues(response.body());
                        saveUserFields(response.body());
                        startActivityForResult(new Intent(mDataManager.getContext(), UserListActivity.class), ConstantManager.USER_LIST_ACTIVITY_CODE);
                    }
                }

                @Override
                public void onFailure(Call<UserInfoRes> call, Throwable t) {
                    startActivityForResult(new Intent(mDataManager.getContext(), AuthActivity.class), ConstantManager.AUTH_ACTIVITY_CODE);
                }
            });
        } else {
            startActivityForResult(new Intent(mDataManager.getContext(), AuthActivity.class), ConstantManager.AUTH_ACTIVITY_CODE);
        }
    }

    private void saveUserValues(UserInfoRes userModel){
        int[] userValues = {
                userModel.getData().getProfileValues().getRating(),
                userModel.getData().getProfileValues().getLinesCode(),
                userModel.getData().getProfileValues().getProjects()
        };
        mDataManager.getPreferencesManager().saveUserProfileValues(userValues);
    }

    private void saveUserFields(UserInfoRes userModel){
        List<String> userFields = new ArrayList<>();
        userFields.add(userModel.getData().getContacts().getPhone());
        userFields.add(userModel.getData().getContacts().getEmail());
        userFields.add(userModel.getData().getContacts().getVk());
        if(userModel.getData().getRepositories().getRepo().size()!=0)
            userFields.add(userModel.getData().getRepositories().getRepo().get(0).getGit());
        userFields.add(userModel.getData().getPublicInfo().getBio());

        mDataManager.getPreferencesManager().saveUserProfileData(userFields);
        mDataManager.getPreferencesManager().saveUserFullName(userModel.getData().getFullName());
    }
}
