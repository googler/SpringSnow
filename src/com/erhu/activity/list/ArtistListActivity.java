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
import com.erhu.activityGroup.ArtistActivityGroup;
import com.erhu.adapter.ArtistListAdapter;
import com.erhu.util.Constants;

// 艺术家
public class ArtistListActivity extends BaseListActivity {
    private ListView listview;
    private Cursor mCursor;
    private String[] _artists;

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

    // 给列表填充数据
    private void setListData() {
        mCursor = this.getContentResolver().query(
                MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI,
                Constants.ARTIST_CUR, null, null, MediaStore.Audio.Artists.ARTIST);
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

    // 列表项单击操作监听器类
    class ListItemClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> arg0, View view, int position, long id) {
            LinearLayout layout = (LinearLayout) view;
            TextView artist = (TextView) layout.findViewById(R.id.artist_list_item_singer);
            Intent intent = new Intent(ArtistListActivity.this, ArtistActivityGroup.class);
            intent.putExtra("artist_name", artist.getText().toString());
            startActivity(intent);
        }
    }
}
