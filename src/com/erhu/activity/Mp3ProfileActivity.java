package com.erhu.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.erhu.R;
import com.erhu.util.Constants;
import com.erhu.util.Tools;

/**
 * this activity is used to edit mp3 profile
 */
public class Mp3ProfileActivity extends Activity {

    private EditText titleETxt;
    private EditText artistETxt;
    private EditText albumETxt;
    private ProgressDialog progressDlg;
    private TextView tipTView;

    private String oldTitle;
    private Cursor mCursor;

    final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Toast.makeText(Mp3ProfileActivity.this, "保存成功:)", Toast.LENGTH_SHORT).show();
            progressDlg.dismiss();
            finish();
            super.handleMessage(msg);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        log("create");
        setContentView(R.layout.mp3_profile);
        int position = getIntent().getExtras().getInt("pos");
        titleETxt = (EditText) findViewById(R.id.mp3_profile_title);
        artistETxt = (EditText) findViewById(R.id.mp3_profile_artist);
        albumETxt = (EditText) findViewById(R.id.mp3_profile_album);
        tipTView = (TextView) findViewById(R.id.mp3_profile_tip);

        mCursor = this.getContentResolver()
                .query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                        Constants.MUSIC_CUR, null, null, null);
        mCursor.moveToPosition(position);

        oldTitle = mCursor.getString(1);
        titleETxt.setText(oldTitle);
        artistETxt.setText(mCursor.getString(3));
        albumETxt.setText(mCursor.getString(4));
        String str = "友情提示:\n1.如果编辑框信息显示不全，把屏幕横着放试试看吧:)\n2.编辑后的中文信息在系统重启后会乱码，" +
                "使用iTunes将文件TAG转成ID3V2.4可解决此问题(erhu.com@gmail.com).";
        tipTView.setText(str);
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
        final String title = titleETxt.getText().toString().trim();
        final String artist = artistETxt.getText().toString().trim();
        final String album = albumETxt.getText().toString().trim();

        if (!(title.equals(mCursor.getString(1)) && artist.equals(mCursor.getString(3)) &&
                album.equals(mCursor.getString(4)))) {
            progressDlg = ProgressDialog.show(Mp3ProfileActivity.this, "友情提示:)", "我们正在努力...");
            new Thread(new Runnable() {
                @Override
                public void run() {
                    final int id = mCursor.getInt(0);
                    final String path = mCursor.getString(5);
                    if (Tools.editMp3(Mp3ProfileActivity.this, id, new String[]{artist, album, title, path})) {
                        SSApplication.resetCursor(Mp3ProfileActivity.this);
                        mCursor.close();
                        handler.sendMessage(handler.obtainMessage());
                    }
                }
            }).start();
        } else
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
