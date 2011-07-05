package com.erhu.activity;

import android.app.Activity;
import android.util.Log;

/**
 * 专辑
 */
public class AlbumListActivity extends Activity {

    private void log(String _msg) {
        String TAG = IndexActivity.class.getSimpleName();
        Log.w(TAG, "log@::::::::::::::::::::::::::::::::[" + TAG + "]: " + _msg);
    }
}
