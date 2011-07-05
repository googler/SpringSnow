package com.erhu.activity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

/**
 * this activity is used to edit mp3 profile
 */
public class Mp3ProfileActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        log("onCreate");
    }

    @Override
    protected void onStart() {
        super.onStart();
        log("onStart");
    }

    @Override
    protected void onStop() {
        super.onStop();
        log("onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        log("onDestroy");
    }

    private void log(String _msg) {
        String TAG = Mp3ProfileActivity.class.getSimpleName();
        Log.w(TAG, "log@::::::::::::::::::::::::::::::::[" + TAG + "]: " + _msg);
    }
}
