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
        if (_cursor != null && cursor != _cursor) {
            if (cursor != null) {
                cursor.close();
                cursor = null;
            }
            cursor = _cursor;
            if (cursor != null && position < cursor.getCount() && position != -1)
                cursor.moveToPosition(position);
        }
    }

    public static void setCursor(Cursor _cursor, int _pos) {
        if (_cursor != null && cursor != _cursor) {
            if (cursor != null) {
                cursor.close();
                cursor = null;
            }
            cursor = _cursor;
        }
        if (_pos != -1 && _pos < cursor.getCount()) {
            position = _pos;
            cursor.moveToPosition(position);
        }
    }

    public static void setPosition(int _pos) {
        position = _pos;
    }

    public static int getPosition() {
        return position;
    }
}
