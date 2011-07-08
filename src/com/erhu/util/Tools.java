package com.erhu.util;

import android.app.NotificationManager;
import android.content.Context;

public class Tools {
    /**
     * 时间格式转换
     *
     * @param time
     * @return
     */
    public static String toTime(int time) {
        time /= 1000;
        int minute = time / 60;
        int hour = minute / 60;
        int second = time % 60;
        minute %= 60;
        return String.format("%02d:%02d", minute, second);
    }

    public static void stopNotification(Context _context) {
        NotificationManager manager = (NotificationManager) _context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancel(Constants.NOTIFICATION_ID);
    }
}
