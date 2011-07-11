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
    private static Cursor cursor;
    public static boolean musicEdit = false;// 如果有音乐被编辑过，则要重新获取MusicUri

    // move cursor to _pos
    public static void moveCursor(int _pos) {
        if (cursor != null && _pos != -1) {
            position = _pos;
            cursor.moveToPosition(position);
        }
    }

    //move cursor to position
    public static Cursor getCursor() {
        if (cursor != null && position != -1)
            cursor.moveToPosition(position);
        return cursor;
    }

    public static void setCursor(Cursor _cursor) {
        cursor.close();
        cursor = null;
        if (_cursor != null && cursor != _cursor) {
            cursor = _cursor;
            if (position < cursor.getCount() && position != -1)
                cursor.moveToPosition(position);
        }
    }

    public static void setCursor(Cursor _cursor, int _pos) {
        if (_cursor != null && cursor != _cursor) {
            cursor.close();
            cursor = null;
            cursor = _cursor;
            if (_pos != -1 && _pos < cursor.getCount()) {
                position = _pos;
                cursor.moveToPosition(position);
            }
        }
    }

    public static int getPosition() {
        return position;
    }
}
