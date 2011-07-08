package com.erhu.adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.erhu.R;
import com.erhu.util.Tools;

/**
 * life is good:-)
 */
public class MusicListAdapter extends BaseAdapter {
    private Context context;
    private Cursor mCursor;

    public MusicListAdapter(Context _context, Cursor mCursor) {
        context = _context;
        this.mCursor = mCursor;
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
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(context).inflate(R.layout.music_list_item, null);
        TextView title = (TextView) convertView.findViewById(R.id.music_list_item_title);
        TextView singer = (TextView) convertView.findViewById(R.id.music_list_item_artist);
        TextView time = (TextView) convertView.findViewById(R.id.music_list_item_time);

        mCursor.moveToPosition(position);
        title.setText(mCursor.getString(1));
        time.setText(Tools.toTime(mCursor.getInt(2)));
        singer.setText(mCursor.getString(3));
        return convertView;
    }
}
