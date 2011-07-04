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

import java.io.UnsupportedEncodingException;

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
        title.setText(bSubstring(cursor.getString(0).trim(), 24));
        time.setText(Tools.toTime(cursor.getInt(1)));

        if (cursor.getString(2).equals(MediaStore.UNKNOWN_STRING))
            singer.setText("悲剧的艺术家");
        else
            singer.setText(cursor.getString(2));

        if (position == pos)
            img.setImageResource(R.drawable.isplaying);
        else
            img.setImageResource(R.drawable.folder_item_img);
        return convertView;
    }

    /**
     * 字符串裁剪
     *
     * @param s
     * @param length
     * @return
     * @throws Exception
     */
    public static String bSubstring(String s, int length) {
        byte[] bytes = new byte[0];
        try {
            bytes = s.getBytes("Unicode");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        int n = 0; // 表示当前的字节数
        int i = 2; // 要截取的字节数，从第3个字节开始
        for (; i < bytes.length && n < length; i++) {
            // 奇数位置，如3、5、7等，为UCS2编码中两个字节的第二个字节
            if (i % 2 == 1) {
                n++; // 在UCS2第二个字节时n加1
            } else {
                // 当UCS2编码的第一个字节不等于0时，该UCS2字符为汉字，一个汉字算两个字节
                if (bytes[i] != 0) {
                    n++;
                }
            }
        }
        // 如果i为奇数时，处理成偶数
        if (i % 2 == 1) {
            // 该UCS2字符是汉字时，去掉这个截一半的汉字
            if (bytes[i - 1] != 0)
                i = i - 1;
                // 该UCS2字符是字母或数字，则保留该字符
            else
                i = i + 1;
        }
        try {
            return new String(bytes, 0, i, "Unicode");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return "";
        }
    }
}
