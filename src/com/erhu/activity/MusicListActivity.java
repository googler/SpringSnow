package com.erhu.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import com.erhu.R;
import com.erhu.adapter.MusicListAdapter;

import java.io.File;

/**
 * 音乐列表
 */
public class MusicListActivity extends ListActivity {

    private ListView listview;
    private Cursor mCursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        log("create");
        super.onCreate(savedInstanceState);
        listview = getListView();
        listview.setFastScrollEnabled(true);
        listview.setDrawSelectorOnTop(false);
        listview.setDivider(null);
        listview.setScrollingCacheEnabled(false);
        listview.setFadingEdgeLength(0);
        listview.setOnItemClickListener(new ListItemClickListener());
        listview.setOnItemLongClickListener(new ListItemLongClickListener());
        this.resetCursor();
    }

    @Override
    protected void onStart() {
        super.onStart();
        log("start");
        // if music be edited, reGet cursor
        if (SSApplication.musicEdit) {
            this.resetCursor();
            SSApplication.musicEdit = false;
        }
    }

    /**
     * 音乐被编辑后，重新设置Cursor
     */
    private void resetCursor() {
        log("reset cursor");
        if (mCursor != null)
            mCursor.close();
        mCursor = this.getContentResolver()
                .query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                        new String[]{
                                MediaStore.Audio.Media._ID,
                                MediaStore.Audio.Media.TITLE,
                                MediaStore.Audio.Media.DURATION,
                                MediaStore.Audio.Media.ARTIST,
                                MediaStore.Audio.Media.ALBUM,
                                MediaStore.Audio.Media.DISPLAY_NAME},
                        null, null, null);
        SSApplication.setCursor(mCursor);
        listview.setAdapter(new MusicListAdapter(this, mCursor));
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
        log("destroy");
        super.onDestroy();
    }

    /**
     * 列表项单击操作监听器类
     */
    class ListItemClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> arg0, View view, int position, long id) {
            SSApplication.setCursor(mCursor, position);
            Intent intent = new Intent(MusicListActivity.this, PlayActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            intent.putExtra("op", "new One");
            startActivity(intent);
        }
    }

    /**
     * if long click, we provide a popup dialog.
     */
    class ListItemLongClickListener implements AdapterView.OnItemLongClickListener {
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, final int _position, long id) {
            DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                        case 0:
                            Intent intent = new Intent(MusicListActivity.this, Mp3ProfileActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                            intent.putExtra("pos", _position);
                            startActivityForResult(intent, 1);
                            break;
                        case 1:
                            if (_position == SSApplication.getPosition())
                                Toast.makeText(MusicListActivity.this, "不可以删除正在播放的歌曲:)", Toast.LENGTH_SHORT).show();
                            else
                                new AlertDialog.Builder(MusicListActivity.this)
                                        .setIcon(R.drawable.alert_dialog_icon)
                                        .setTitle("真的要删吗??")
                                        .setPositiveButton("真删!!", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int whichButton) {
                                                Cursor t_cur = getContentResolver()
                                                        .query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                                                                new String[]{MediaStore.Audio.Media._ID,
                                                                        MediaStore.Audio.Media.TITLE,
                                                                        MediaStore.Audio.Media.DATA},
                                                                null, null, null);
                                                t_cur.moveToPosition(_position);
                                                Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                                                uri = ContentUris.withAppendedId(uri, t_cur.getInt(0));
                                                getContentResolver().delete(uri, null, null);
                                                Toast.makeText(MusicListActivity.this, "删除成功:)", Toast.LENGTH_SHORT).show();
                                                getContentResolver().notifyChange(uri, null);
                                                new File(t_cur.getString(2).substring(4)).delete();
                                                SSApplication.musicEdit = true;
                                                resetCursor();
                                            }
                                        })
                                        .setNegativeButton("我不删:)", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int whichButton) {
                                            }
                                        }).create().show();
                            break;
                        default:
                            break;
                    }
                }
            };
            AlertDialog.Builder builder = new AlertDialog.Builder(MusicListActivity.this);
            builder.setTitle("你可以...");
            builder.setItems(new String[]{"编辑音乐信息", "删除它"}, listener);
            builder.create().show();
            return true;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 1)
                Toast.makeText(this, "保存成功", Toast.LENGTH_SHORT).show();
            else if (requestCode == 2)
                Toast.makeText(this, "删除成功", Toast.LENGTH_SHORT).show();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void log(String _msg) {
        final String TAG = MusicListActivity.class.getSimpleName();
        Log.w(TAG, "log@:::::[" + TAG + "]: " + _msg);
    }
}
