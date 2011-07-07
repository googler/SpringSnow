package com.erhu.activity;

import android.app.Application;
import android.database.Cursor;
import com.erhu.util.Constants;

/**
 * Life is good:-)
 */
public class SSApplication extends Application {
    public static int playerState = Constants.STOPPED_STATE;// 播放器的状态
    private static int position = -1;// 当前播放歌曲ID
    public static Cursor cursor;

    public static void setPosition(int _position) {
        position = _position;
        cursor.moveToPosition(position);
    }

    public static void setPosition() {
        cursor.moveToPosition(position);
    }

    public static int getPosition() {
        return position;
    }
}
