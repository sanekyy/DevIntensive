package com.softdesign.devintensive.ui.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.softdesign.devintensive.R;
import com.softdesign.devintensive.utils.ConstantManager;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = ConstantManager.TAG_PREFIX + "Main Activity";

    private TextView phone_et;
    private TextView email_et;
    private TextView vk_et;
    private TextView github_et;
    private Intent intent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate");

        phone_et = (TextView) findViewById(R.id.phone_et);
        email_et = (TextView) findViewById(R.id.email_et);
        vk_et = (TextView) findViewById(R.id.vk_et);
        github_et = (TextView) findViewById(R.id.github_et);

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

        if (savedInstanceState == null) {
            // start first

        } else {
            // start second and more
        }
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


    /**
     * Слушаем нажатие на иконки слева, и вызываем интенты.
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.call_iv: intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phone_et.getText().toString()));
                break;
            case R.id.sendMessage_iv: String mailto = "mailto:" + email_et.getText().toString() + "?cc=" + "alice@example.com" +
                                                        "&subject=" + "SUBJ" + "&body=" + "BODY";
                intent = new Intent(Intent.ACTION_SENDTO, Uri.parse(mailto));
                break;
            case R.id.openLinkVK_iv: intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://"+vk_et.getText().toString()));
                break;
            case R.id.openLinkGIT_iv: intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://"+github_et.getText().toString()));
                break;
            default: Log.d(TAG, "DEF"); return;
        }
            startActivity(intent);
    }
}
