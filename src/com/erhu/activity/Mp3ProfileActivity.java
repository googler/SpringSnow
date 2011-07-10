package com.erhu.activity;

import android.app.Activity;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import com.erhu.R;
import com.erhu.util.Tools;

/**
 * this activity is used to edit mp3 profile
 */
public class Mp3ProfileActivity extends Activity {

    private EditText titleETxt;
    private EditText artistETxt;
    private EditText albumETxt;

    private String oldTitle;
    private Cursor mCursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        log("create");
        setContentView(R.layout.mp3_profile);
        int position = getIntent().getExtras().getInt("pos");
        titleETxt = (EditText) findViewById(R.id.mp3_profile_title);
        artistETxt = (EditText) findViewById(R.id.mp3_profile_artist);
        albumETxt = (EditText) findViewById(R.id.mp3_profile_album);

        mCursor = this.getContentResolver()
                .query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                        new String[]{
                                MediaStore.Audio.Media._ID,
                                MediaStore.Audio.Media.TITLE,
                                MediaStore.Audio.Media.ARTIST,
                                MediaStore.Audio.Media.ALBUM,
                                MediaStore.Audio.Media.DATA},
                        null, null, null);
        mCursor.moveToPosition(position);

        oldTitle = mCursor.getString(1);
        titleETxt.setText(oldTitle);
        artistETxt.setText(mCursor.getString(2));
        albumETxt.setText(mCursor.getString(3));
    }

    /**
     * you clicked the button named 'save'.
     * modify data in db and fileStream
     *
     * @param _view
     */
    public void saveBtnClicked(final View _view) {
        if (TextUtils.isEmpty(titleETxt.getText().toString().trim())) {
            Toast.makeText(this, "请填写标题:)", Toast.LENGTH_SHORT).show();
            return;
        }
        String title = titleETxt.getText().toString().trim();
        String artist = artistETxt.getText().toString().trim();
        String album = albumETxt.getText().toString().trim();

        ContentValues cv = new ContentValues();
        cv.put(MediaStore.Audio.Media.TITLE, title);
        cv.put(MediaStore.Audio.Media.ARTIST, artist);
        cv.put(MediaStore.Audio.Media.ALBUM, album);

        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        uri = ContentUris.withAppendedId(uri, mCursor.getInt(0));
        super.getContentResolver().update(uri, cv, null, null);
        if (Tools.editMp3(mCursor.getString(4).substring(4), new String[]{artist, album, title}, oldTitle))
            setResult(Activity.RESULT_OK);
        SSApplication.musicEdit = true;
        finish();
    }

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
        mCursor.close();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        log("destroy");
    }

    private void log(String _msg) {
        String TAG = Mp3ProfileActivity.class.getSimpleName();
        Log.w(TAG, "log@:::::[" + TAG + "]: " + _msg);
    }
}
