package com.erhu.activity;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import com.erhu.R;
import com.erhu.adapter.MusicListAdapter;

/**
 * 音乐列表
 */
public class MusicListActivity extends ListActivity {
    private ListView listview;
    private int[] ids;
    private int[] durations;
    private String[] titles;
    private String[] albums;
    private String[] artists;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        log("onCreate");
        super.onCreate(savedInstanceState);
        listview = getListView();
        listview.setDivider(null);
        listview.setScrollingCacheEnabled(false);
        listview.setFadingEdgeLength(0);
        listview.setBackgroundResource(R.drawable.list_bg);
        listview.setOnItemClickListener(new ListItemClickListener());
        listview.setOnItemLongClickListener(new ListItemLongCLickListener());
        this.setListData();
    }

    /**
     * 给列表填充数据
     */
    private void setListData() {
        Cursor cursor = this.getContentResolver()
                .query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                        new String[]{
                                MediaStore.Audio.Media._ID,
                                MediaStore.Audio.Media.TITLE,
                                MediaStore.Audio.Media.DURATION,
                                MediaStore.Audio.Media.ARTIST,
                                MediaStore.Audio.Media.ALBUM,
                                MediaStore.Audio.Media.DISPLAY_NAME},
                        null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            final int t_count = cursor.getCount();
            ids = new int[t_count];
            durations = new int[t_count];
            titles = new String[t_count];
            albums = new String[t_count];
            artists = new String[t_count];

            for (int i = 0; i < cursor.getCount(); i++) {
                ids[i] = cursor.getInt(0);
                titles[i] = cursor.getString(1);
                durations[i] = cursor.getInt(2);
                artists[i] = cursor.getString(3).equals(MediaStore.UNKNOWN_STRING) ? "悲剧的艺术家" : cursor.getString(3);
                albums[i] = cursor.getString(4);
                cursor.moveToNext();
            }
        }
    }

    @Override
    protected void onStart() {
        log("onStart");
        super.onStart();
        // indexActivity底部在切换选项卡时，调用此方法，重新给SSApplication中的
        // 全局变量赋值
        SSApplication.setData(ids, titles, artists, albums, durations);
        MusicListAdapter adapter = new MusicListAdapter(this);
        listview.setAdapter(adapter);
    }

    @Override
    protected void onStop() {
        log("onStop");
        super.onStop();
    }


    /**
     * 列表项单击操作监听器类
     */
    class ListItemClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> arg0, View view, int position, long id) {
            Intent intent = new Intent(MusicListActivity.this, PlayActivity.class);
            intent.putExtra("position", position);
            startActivity(intent);
        }
    }

    class ListItemLongCLickListener implements AdapterView.OnItemLongClickListener {
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int _position, long id) {
            Intent intent = new Intent(MusicListActivity.this, Mp3ProfileActivity.class);
            intent.putExtra("position", _position);
            startActivity(intent);
            log("hello ");
            return true;
        }
    }

    private void log(String _msg) {
        String TAG = MusicListActivity.class.getSimpleName();
        Log.w(TAG, "log@::::::::::::::::::::::::::::::::[" + TAG + "]: " + _msg);
    }
}
