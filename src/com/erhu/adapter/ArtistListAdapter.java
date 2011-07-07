package com.erhu.adapter;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.erhu.R;

/**
 * life is very good:-)
 */
public class ArtistListAdapter extends BaseAdapter {
    private Context context;
    private Cursor mCursor;

    public ArtistListAdapter(Context con, Cursor cur) {
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
        convertView = LayoutInflater.from(context).inflate(R.layout.artist_list_item, null);
        mCursor.moveToPosition(_position);
        TextView count = (TextView) convertView.findViewById(R.id.artist_list_item_count);
        count.setText(mCursor.getString(2));
        TextView singer = (TextView) convertView.findViewById(R.id.artist_list_item_singer);
        singer.setText(mCursor.getString(3).equals(MediaStore.UNKNOWN_STRING) ? "无名氏:(" : mCursor.getString(1));
        return convertView;
    }
}
