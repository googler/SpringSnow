package com.erhu.activity;

import android.app.Activity;
import android.util.Log;

/**
 * Favourite
 */
public class FavouriteListActivity extends Activity {
    @Override
    protected void onStart() {
        super.onStart();
        log("start");
    }

    @Override
    public void finish() {
        log("finish");
        super.finish();
    }

    @Override
    protected void onStop() {
        log("stop");
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        log("destroy");
    }

    private void log(String _msg) {
        String TAG = FavouriteListActivity.class.getSimpleName();
        Log.w(TAG, "log@:::::[" + TAG + "]: " + _msg);
    }
}
