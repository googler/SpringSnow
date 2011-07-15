package com.erhu.activity.base;

import android.app.ListActivity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

public abstract class BaseListActivity extends ListActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        log(this, "create");
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onStart() {
        log(this, "start");
        super.onStart();
    }

    @Override
    public void finish() {
        log(this, "finish");
        super.finish();
    }

    @Override
    protected void onStop() {
        log(this, "stop");
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        log(this, "destroy");
        super.onDestroy();
    }

    protected void log(final Context _context, String _msg) {
        String TAG = _context.getClass().getSimpleName();
        Log.w(TAG, "log@:::::[" + TAG + "]: " + _msg);
    }
}
