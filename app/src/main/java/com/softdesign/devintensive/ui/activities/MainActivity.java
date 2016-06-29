package com.softdesign.devintensive.ui.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.softdesign.devintensive.R;
import com.softdesign.devintensive.data.managers.DataManager;
import com.softdesign.devintensive.utils.ConstantManager;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = ConstantManager.TAG_PREFIX + "Main Activity";

    private int mCurrentEditMode=0;

    private CoordinatorLayout mCoordinatorLayout;
    private Toolbar mToolbar;
    private DrawerLayout mNavigationDrawer;
    private NavigationView mNavigationView;
    private FloatingActionButton mFab;

    private EditText mUserPhone_et, mUserEmail_et, mUserVk_et, mUserGithub_et, mUserAbout_et;


    private List<EditText> mUserInfoViews;
    DataManager mDataManager;
    private Intent intent;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate");

        mCoordinatorLayout = (CoordinatorLayout) findViewById(R.id.main_coordinator_container);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mNavigationDrawer = (DrawerLayout) findViewById(R.id.navigation_drawer);
        mNavigationView = (NavigationView) findViewById(R.id.navigation_view);
        mFab = (FloatingActionButton) findViewById(R.id.fab);

        mDataManager= DataManager.getInstance();

        mUserPhone_et = (EditText) findViewById(R.id.phone_et);
        mUserEmail_et = (EditText) findViewById(R.id.email_et);
        mUserVk_et = (EditText) findViewById(R.id.vk_et);
        mUserGithub_et = (EditText) findViewById(R.id.github_et);
        mUserAbout_et = (EditText) findViewById(R.id.about_et);

        mUserInfoViews = new ArrayList<>();
        mUserInfoViews.add(mUserPhone_et);
        mUserInfoViews.add(mUserEmail_et);
        mUserInfoViews.add(mUserVk_et);
        mUserInfoViews.add(mUserGithub_et);
        mUserInfoViews.add(mUserAbout_et);




        if (((ImageView) findViewById(R.id.call_iv)) != null) {
            ((ImageView) findViewById(R.id.call_iv)).setOnClickListener(this);
        }
        if (((ImageView) findViewById(R.id.sendMessage_iv)) != null) {
            ((ImageView) findViewById(R.id.sendMessage_iv)).setOnClickListener(this);
        }
        if (((ImageView) findViewById(R.id.openLinkVK_iv)) != null) {
            ((ImageView) findViewById(R.id.openLinkVK_iv)).setOnClickListener(this);
        }
        if (((ImageView) findViewById(R.id.openLinkGIT_iv)) != null) {
            ((ImageView) findViewById(R.id.openLinkGIT_iv)).setOnClickListener(this);
        }

        mFab.setOnClickListener(this);



        setupToolbar();
        setupDrawer();
        loadUserInfoValue();

        if (savedInstanceState == null) {
            // start first
        } else {
            // start second and more
            mCurrentEditMode = savedInstanceState.getInt(ConstantManager.EDIT_MODE_KEY,0);
            changeEditMode(mCurrentEditMode);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==android.R.id.home){
            mNavigationDrawer.openDrawer(GravityCompat.START);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");

        if (getSupportActionBar() != null) getSupportActionBar().setTitle(R.string.dev_name);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
        saveUserInfoValue();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
    }


    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG, "onRestart");
    }


    @Override
    public void onBackPressed() {
        if(mNavigationView.isShown()){
            mNavigationDrawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    /**
     * Слушаем нажатие на иконки слева, и вызываем интенты.
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.call_iv: intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + mUserPhone_et.getText().toString()));
                startActivity(intent);
                break;
            case R.id.sendMessage_iv: String mailto = "mailto:" + mUserEmail_et.getText().toString() + "?cc=" + "alice@example.com" +
                                                        "&subject=" + "SUBJ" + "&body=" + "BODY";
                intent = new Intent(Intent.ACTION_SENDTO, Uri.parse(mailto));
                startActivity(intent);
                break;
            case R.id.openLinkVK_iv: intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://"+mUserVk_et.getText().toString()));
                startActivity(intent);
                break;
            case R.id.openLinkGIT_iv: intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://"+mUserGithub_et.getText().toString()));
                startActivity(intent);
                break;
            case R.id.fab:
                if(mCurrentEditMode==0){
                    changeEditMode(1);
                    mCurrentEditMode=1;
                }
                else{
                    changeEditMode(0);
                    mCurrentEditMode=0;
                }
            default: Log.d(TAG, "DEF"); return;
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(ConstantManager.EDIT_MODE_KEY,mCurrentEditMode);
    }

    private void showSnackbar(String message){
        Snackbar.make(mCoordinatorLayout, message,Snackbar.LENGTH_SHORT).show();
    }

    private void setupToolbar(){
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar!=null){
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void setupDrawer(){
        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener(){
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                showSnackbar(item.getTitle().toString());
                item.setChecked(true);
                mNavigationDrawer.closeDrawer(GravityCompat.START);
                return false;
            }
        });
    }

    private void changeEditMode(int mode){
        if(mode==1) {
            mFab.setImageResource(R.drawable.ic_mode_done_white_24dp);
            for (EditText userValue : mUserInfoViews) {
                userValue.setEnabled(true);
                userValue.setFocusable(true);
                userValue.setFocusableInTouchMode(true);
            }
        }
        else{
            for (EditText userValue : mUserInfoViews) {
                mFab.setImageResource(R.drawable.ic_mode_edit_white_24dp);
                userValue.setEnabled(false);
                userValue.setFocusable(false);
                userValue.setFocusableInTouchMode(false);
            }
        }
        saveUserInfoValue();
    }

    private void loadUserInfoValue(){
        List<String> userData = mDataManager.getPreferencesManager().loadUserProfileData();
        for (int i=0;i<userData.size();i++) {
            mUserInfoViews.get(i).setText(userData.get(i));
        }
    }

    private void saveUserInfoValue(){
        List<String> userData = new ArrayList<>();
        for (EditText userFielView: mUserInfoViews) {
            userData.add(userFielView.getText().toString());
        }
        mDataManager.getPreferencesManager().saveUserProfileData(userData);
    }

}
