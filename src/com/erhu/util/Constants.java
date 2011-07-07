package com.erhu.util;

import android.provider.MediaStore;

public class Constants {
    public static final String[] mCursorCols = new String[]{
            "audio._id AS _id",
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.MIME_TYPE,
            MediaStore.Audio.Media.ALBUM_ID,
            MediaStore.Audio.Media.ARTIST_ID,
            MediaStore.Audio.Media.DURATION
    };
    public final static String SERVICE_ACTION = "me.erhu.media.MUSIC_SERVICE";

    public final static int STOPPED_STATE = 0;
    public final static int PLAYING_STATE = 1;
    public final static int PAUSED_STATE = 2;

    public final static int STOP_OP = 3;
    public final static int PLAY_OP = 4;
    public final static int PAUSE_OP = 5;
    public final static int CONTINUE_OP = 6;
    public final static int PROCESS_CHANGE_OP = 7;

    public static final String CURRENT_ACTION = "me.erhu.currentTime";
    public static final String DURATION_ACTION = "me.erhu.duration";
    public static final String CONTINUE_ACTION = "me.erhu.continue";
}
