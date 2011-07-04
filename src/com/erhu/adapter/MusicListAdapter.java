package com.erhu.adapter;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.erhu.R;
import com.erhu.util.Tools;

/**
 * life is good:-)
 */
public class MusicListAdapter extends BaseAdapter {
    private Context context;
    private Cursor cursor;
    private int pos = -1;

    public MusicListAdapter(Context con, Cursor cur) {
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
        convertView = LayoutInflater.from(context).inflate(R.layout.music_list_item, null);
        TextView title = (TextView) convertView.findViewById(R.id.music_list_item_title);
        TextView singer = (TextView) convertView.findViewById(R.id.artist);
        TextView time = (TextView) convertView.findViewById(R.id.music_list_item_time);
        ImageView img = (ImageView) convertView.findViewById(R.id.music_list_item_img);

        cursor.moveToPosition(position);
        title.setText(cursor.getString(0));
        time.setText(Tools.toTime(cursor.getInt(1)));
        singer.setText(cursor.getString(2).equals(MediaStore.UNKNOWN_STRING) ? "悲剧的艺术家" : cursor.getString(2));
        img.setImageResource(position == pos ? R.drawable.isplaying : R.drawable.folder_item_img);
        return convertView;
    }


}
