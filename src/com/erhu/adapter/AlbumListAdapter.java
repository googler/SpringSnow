package com.erhu.adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.erhu.R;

public class AlbumListAdapter extends BaseAdapter {
    private Context context;
    private Cursor mCursor;

    public AlbumListAdapter(Context con, Cursor cur) {
        context = con;
        mCursor = cur;
    }

    @Override
    public int getCount() {
        return mCursor.getCount();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int _position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item_album, null);
        }
        mCursor.moveToPosition(_position);
        //TextView count = (TextView) convertView.findViewById(R.id.artist_list_item_count);
        //count.setText(mCursor.getString(2));
        TextView album = (TextView) convertView.findViewById(R.id.album_list_item_name);
        album.setText(mCursor.getString(1));
        return convertView;
    }
}
