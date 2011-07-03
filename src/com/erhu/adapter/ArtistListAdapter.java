package com.erhu.adapter;

import android.content.Context;
import android.database.Cursor;
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
    private Cursor cursor;

    public ArtistListAdapter(Context con, Cursor cur) {
        context = con;
        cursor = cur;
    }

    @Override
    public int getCount() {
        return cursor.getCount();
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
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(context).inflate(R.layout.artist_list_item, null);
        cursor.moveToPosition(position);

        TextView tv_singer = (TextView) convertView.findViewById(R.id.artist_list_item_singer);
        if (cursor.getString(0).equals("<unknown>"))
            tv_singer.setText("悲剧的艺术家");
        else
            tv_singer.setText(cursor.getString(0));
        return convertView;
    }
}
