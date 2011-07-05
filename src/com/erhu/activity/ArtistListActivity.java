package com.erhu.activity;

import android.app.ListActivity;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import com.erhu.R;
import com.erhu.adapter.ArtistListAdapter;

/**
 * 艺术家
 */
public class ArtistListActivity extends ListActivity {
    private ListView listview;
    private Cursor cursor;
    private String[] _artists;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
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
                        new String[]{MediaStore.Audio.Media.ARTIST},
                        null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            _artists = new String[cursor.getCount()];
            for (int i = 0; i < cursor.getCount(); i++) {
                _artists[i] = cursor.getString(0);
                cursor.moveToNext();
            }
            ArtistListAdapter adapter = new ArtistListAdapter(this, cursor);
            listview.setAdapter(adapter);
        }
    }


    /**
     * 列表项单击操作监听器类
     */
    class ListItemClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> arg0, View view, int position, long id) {
            //playMusic(position);
        }
    }

    private void log(String _msg) {
        String TAG = IndexActivity.class.getSimpleName();
        Log.w(TAG, "log@::::::::::::::::::::::::::::::::[" + TAG + "]: " + _msg);
    }
}
