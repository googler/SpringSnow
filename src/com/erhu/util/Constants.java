package com.erhu.util;

public class Constants {
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

    public static int NOTIFICATION_ID = 0x0618;
}
