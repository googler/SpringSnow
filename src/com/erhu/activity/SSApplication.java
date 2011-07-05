package com.erhu.activity;

import android.app.Application;
import com.erhu.util.Constants;

/**
 * Life is good:-)
 */
public class SSApplication extends Application {
    public static int[] ids;
    public static int[] durations;
    public static String[] titles;
    public static String[] artists;
    public static String[] albums;
    public static int position = -1;// 当前播放的音乐
    public static int playerState = Constants.STOPPED_STATE;// 播放器的状态

    public static void setData(int[] _ids, String[] _titles, String[] _artists, String[] _albums, int[] _durations) {
        ids = _ids;
        titles = _titles;
        artists = _artists;
        albums = _albums;
        durations = _durations;
    }
}
