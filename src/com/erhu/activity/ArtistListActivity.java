package com.erhu.activity;

import android.app.ListActivity;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import com.erhu.R;
import com.erhu.adapter.ArtistListAdapter;

/**
 * 艺术家
 */
public class ArtistListActivity extends ListActivity {
    private ListView listview;
    private Cursor mCursor;
    private String[] _artists;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        log("create");
        super.onCreate(savedInstanceState);
        listview = getListView();
        this.setListData();
        listview.setDivider(null);
        listview.setScrollingCacheEnabled(false);
        listview.setFadingEdgeLength(0);
        listview.setBackgroundResource(R.drawable.list_bg);
        listview.setOnItemClickListener(new ListItemClickListener());
    }

    /**
     * 给列表填充数据
     */
    private void setListData() {
        mCursor = this.getContentResolver()
                .query(MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI,
                        new String[]{MediaStore.Audio.Artists._ID,
                                MediaStore.Audio.Artists.ARTIST,
                                MediaStore.Audio.Artists.NUMBER_OF_TRACKS,
                                MediaStore.Audio.Artists.NUMBER_OF_ALBUMS},
                        null, null, MediaStore.Audio.Artists.ARTIST_KEY);
        if (mCursor != null) {
            mCursor.moveToFirst();
            _artists = new String[mCursor.getCount()];
            for (int i = 0; i < mCursor.getCount(); i++) {
                _artists[i] = mCursor.getString(0);
                mCursor.moveToNext();
            }
            ArtistListAdapter adapter = new ArtistListAdapter(this, mCursor);
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
            TextView artist = (TextView) layout.findViewById(R.id.artist_list_item_singer);
            /*mCursor = getContentResolver()
                    .query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                            new String[]{
                                    MediaStore.Audio.Media._ID,
                                    MediaStore.Audio.Media.TITLE,
                                    MediaStore.Audio.Media.DURATION,
                                    MediaStore.Audio.Media.ARTIST,
                                    MediaStore.Audio.Media.ALBUM,
                                    MediaStore.Audio.Media.DISPLAY_NAME},
                            null, null, null);//TODO:这里加where条件过滤
                            */
        }
    }

    @Override
    public void finish() {
        log("finish");
        super.finish();
    }

    private void log(String _msg) {
        String TAG = ArtistListActivity.class.getSimpleName();
        Log.w(TAG, "log@:::::[" + TAG + "]: " + _msg);
    }
}
