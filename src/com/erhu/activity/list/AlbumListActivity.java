package com.erhu.activity.list;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import com.erhu.R;
import com.erhu.activity.base.BaseListActivity;
import com.erhu.activityGroup.AlbumActivityGroup;
import com.erhu.adapter.AlbumListAdapter;
import com.erhu.util.Constants;

public class AlbumListActivity extends BaseListActivity {
    private ListView listview;
    private Cursor mCursor;
    private String[] _artists;
    private String artist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        listview = getListView();
        this.setListData();
        listview.setDivider(null);
        listview.setScrollingCacheEnabled(false);
        listview.setFadingEdgeLength(0);
        listview.setOnItemClickListener(new ListItemClickListener());
    }

    /**
     * 给列表填充数据
     */
    private void setListData() {
        Intent t_intent = getIntent();
        Bundle bundle = t_intent.getExtras();
        if (bundle != null) {// 只查询某艺术家的歌曲
            artist = bundle.getString("artist_name");
            mCursor = this.getContentResolver().query(
                    MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
                    Constants.ALBUM_CUR, MediaStore.Audio.Albums.ARTIST + " = ?",
                    new String[]{artist}, MediaStore.Audio.Albums.ALBUM);
        } else {
            mCursor = this.getContentResolver().query(
                    MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
                    Constants.ALBUM_CUR, null, null, null);
        }
        if (mCursor != null) {
            mCursor.moveToFirst();
            _artists = new String[mCursor.getCount()];
            for (int i = 0; i < mCursor.getCount(); i++) {
                _artists[i] = mCursor.getString(0);
                mCursor.moveToNext();
            }
            AlbumListAdapter adapter = new AlbumListAdapter(this, mCursor);
            listview.setAdapter(adapter);
        }
    }

    /**
     * 列表项单击操作监听器类
     */
    class ListItemClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> arg0, View view, int position, long id) {
            LinearLayout layout = (LinearLayout) view;
            TextView album_tv = (TextView) layout.findViewById(R.id.album_list_item_name);
            Intent intent = new Intent(AlbumListActivity.this, AlbumActivityGroup.class);
            intent.putExtra("artist_name", artist);
            intent.putExtra("album_name", album_tv.getText().toString());
            startActivity(intent);
        }
    }
}
