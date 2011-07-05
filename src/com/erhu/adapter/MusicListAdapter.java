package com.erhu.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.erhu.R;
import com.erhu.activity.SSApplication;
import com.erhu.util.Tools;

/**
 * life is good:-)
 */
public class MusicListAdapter extends BaseAdapter {
    private Context context;


    public MusicListAdapter(Context con) {
        context = con;
    }

    @Override
    public int getCount() {
        return SSApplication.ids.length;
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
        title.setText(SSApplication.titles[position]);
        time.setText(Tools.toTime(SSApplication.durations[position]));
        singer.setText(SSApplication.artists[position]);
        return convertView;
    }
}
