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
    private Cursor cursor;
    private int[] _ids;
    private String[] _titles;
    private String[] _artists;
    private String[] _path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        log("onCreate\n" + this.toString());
        super.onCreate(savedInstanceState);
        listview = getListView();
        this.setListData();
        // 消除listview的线
        listview.setDivider(null);
        // 消除listview的全选黑线
        listview.setScrollingCacheEnabled(false);
        listview.setFadingEdgeLength(0);
        listview.setBackgroundResource(R.drawable.list_bg);
        listview.setOnItemClickListener(new ListItemClickListener());
    }

    /**
     * 给列表填充数据
     */
    private void setListData() {
        cursor = this.getContentResolver()
                .query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                        new String[]{
                                MediaStore.Audio.Media.TITLE,
                                MediaStore.Audio.Media.DURATION,
                                MediaStore.Audio.Media.ARTIST,
                                MediaStore.Audio.Media._ID,
                                MediaStore.Audio.Media.DISPLAY_NAME,
                                MediaStore.Audio.Media.DATA},
                        null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            _ids = new int[cursor.getCount()];
            _titles = new String[cursor.getCount()];
            _path = new String[cursor.getCount()];
            _artists = new String[cursor.getCount()];

            for (int i = 0; i < cursor.getCount(); i++) {
                _ids[i] = cursor.getInt(3);
                _titles[i] = cursor.getString(0);
                _artists[i] = cursor.getString(2);
                _path[i] = cursor.getString(5).substring(4);
                cursor.moveToNext();
            }
            MusicListAdapter adapter = new MusicListAdapter(this, cursor);
            listview.setAdapter(adapter);
        }
    }

    /**
     * 播放音乐
     */
    private void playMusic(int position) {
        Intent intent = new Intent(this, PlayActivity.class);
        intent.putExtra("_ids", _ids);
        intent.putExtra("_titles", _titles);
        intent.putExtra("_artists", _artists);
        intent.putExtra("position", position);
        startActivity(intent);
    }

    /**
     * 列表项单击操作监听器类
     */
    class ListItemClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> arg0, View view, int position, long id) {
            playMusic(position);
        }
    }

    private void log(String _msg) {
        String TAG = MusicListActivity.class.getSimpleName();
        Log.i(TAG, "log@::::::::::::::::::::::::::::::::[" + TAG + "]: " + _msg);
    }
}
