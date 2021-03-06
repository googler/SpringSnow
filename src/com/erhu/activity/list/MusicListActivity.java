package com.erhu.activity.list;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import com.erhu.R;
import com.erhu.activity.Mp3ProfileActivity;
import com.erhu.activity.PlayActivity;
import com.erhu.activity.SSApplication;
import com.erhu.activity.base.BaseListActivity;
import com.erhu.adapter.MusicListAdapter;
import com.erhu.util.Constants;

import java.io.File;

/**
 * 音乐列表
 */
public class MusicListActivity extends BaseListActivity {

    private ListView listview;
    private Cursor mCursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        listview = getListView();
        listview.setFastScrollEnabled(true);
        listview.setDrawSelectorOnTop(false);
        listview.setDivider(null);
        listview.setScrollingCacheEnabled(false);
        listview.setFadingEdgeLength(0);
        listview.setOnItemClickListener(new ListItemClickListener());
        //listview.setOnItemLongClickListener(new ListItemLongClickListener());
    }

    @Override
    protected void onStart() {
        super.onStart();
        mCursor = getCursor();
        listview.setAdapter(new MusicListAdapter(this, mCursor));
    }

    // 列表项单击操作监听器类
    class ListItemClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> arg0, View view, int position, long id) {
            SSApplication.setCursor(mCursor, position);
            SSApplication.playerState = Constants.STOPPED_STATE;
            Intent intent = new Intent(MusicListActivity.this, PlayActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            intent.putExtra("op", "new One");
            startActivity(intent);
        }
    }

    // if long click, we provide a popup dialog.
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
                                Toast.makeText(MusicListActivity.this, "不可以删除正在播放的音乐:-)", Toast.LENGTH_SHORT).show();
                            else
                                new AlertDialog.Builder(MusicListActivity.this)
                                        .setIcon(R.drawable.alert_dialog_icon)
                                        .setTitle("你真的要永久删除这首歌吗?")
                                        .setPositiveButton("删除!", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int whichButton) {
                                                Cursor t_cur = getCursor();
                                                t_cur.moveToPosition(_position);
                                                Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                                                uri = ContentUris.withAppendedId(uri, t_cur.getInt(0));
                                                getContentResolver().delete(uri, null, null);
                                                Toast.makeText(MusicListActivity.this, "删除成功:-)", Toast.LENGTH_SHORT).show();
                                                getContentResolver().notifyChange(uri, null);
                                                // delete file
                                                new File(t_cur.getString(5).substring(4)).delete();
                                                t_cur.close();
                                                // fresh UI
                                                {
                                                    mCursor = getCursor();
                                                    listview.setAdapter(new MusicListAdapter(MusicListActivity.this, mCursor));
                                                }
                                                // 如果当前播放序列中有被删除的歌曲，则要重新给全局cursor赋值
                                                SSApplication.resetCursor(MusicListActivity.this);
                                                // 被删除的歌曲在当前播放歌曲的前面，position - 1
                                                if (_position < SSApplication.getPosition())
                                                    SSApplication.setPosition(SSApplication.getPosition() - 1);
                                            }
                                        })
                                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
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
            builder.setItems(new String[]{"编辑", "删除"}, listener);
            builder.create().show();
            return true;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && requestCode == 2)
            Toast.makeText(this, "删除成功:-)", Toast.LENGTH_SHORT).show();
        super.onActivityResult(requestCode, resultCode, data);
    }

    private Cursor getCursor() {
        Cursor cur = null;
        Intent t_intent = getIntent();
        Bundle bundle = t_intent.getExtras();
        if (bundle != null) {// 只查询某艺术家的歌曲
            String artist_name = bundle.getString("artist_name");
            String album_name = bundle.getString("album_name");
            if (album_name != null) {
                cur = getContentResolver().query(
                        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                        Constants.MUSIC_CUR,
                        MediaStore.Audio.Media.ARTIST + " = ? AND "
                                + MediaStore.Audio.Media.ALBUM + " = ?",
                        new String[]{artist_name, album_name},
                        MediaStore.Audio.Media.TITLE);
            } else {
                cur = getContentResolver().query(
                        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, Constants.MUSIC_CUR,
                        MediaStore.Audio.Media.ARTIST + " = ?", new String[]{artist_name},
                        MediaStore.Audio.Media.TITLE);
            }
        } else {
            cur = getContentResolver().query(
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, Constants.MUSIC_CUR,
                    null, null, MediaStore.Audio.Media.TITLE);
        }
        return cur;
    }
}
