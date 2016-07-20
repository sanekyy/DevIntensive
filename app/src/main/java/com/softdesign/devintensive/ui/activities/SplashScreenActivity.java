package com.softdesign.devintensive.ui.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.softdesign.devintensive.R;
import com.softdesign.devintensive.data.managers.DataManager;
import com.softdesign.devintensive.data.network.res.UserModelRes;
import com.softdesign.devintensive.utils.ConstantManager;

public class SplashScreenActivity extends AppCompatActivity {

    private DataManager mDataManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        mDataManager = DataManager.getInstance();

        if(mDataManager.getPreferencesManager().getAuthToken().isEmpty()||mDataManager.getPreferencesManager().getAuthToken()=="null"){
            startActivityForResult(new Intent(mDataManager.getContext(), AuthActivity.class), ConstantManager.AUTH_ACTIVITY_CODE);
        } else{
            startActivityForResult(new Intent(mDataManager.getContext(), UserListActivity.class), ConstantManager.USER_LIST_ACTIVITY_CODE);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (resultCode){
            case ConstantManager.EXIT_APP_CODE:
                finish();
                break;
            case ConstantManager.LOGOUT_CODE:
                startActivityForResult(new Intent(mDataManager.getContext(), AuthActivity.class), ConstantManager.AUTH_ACTIVITY_CODE);
                break;
            case ConstantManager.AUTH_ACTIVITY_COMPLETE_CODE:
                startActivityForResult(new Intent(mDataManager.getContext(), MainActivity.class), ConstantManager.MAIN_ACTIVITY_CODE);
                break;
            case ConstantManager.MAIN_ACTIVITY_CODE:
                startActivityForResult(new Intent(mDataManager.getContext(), MainActivity.class), ConstantManager.MAIN_ACTIVITY_CODE);
                break;
            case ConstantManager.USER_LIST_ACTIVITY_CODE:
                startActivityForResult(new Intent(mDataManager.getContext(), UserListActivity.class), ConstantManager.USER_LIST_ACTIVITY_CODE);
                break;
            default: finish();
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}
