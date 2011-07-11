package com.erhu.util;

import android.app.NotificationManager;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import org.farng.mp3.MP3File;
import org.farng.mp3.TagException;
import org.farng.mp3.id3.AbstractID3v2;
import org.farng.mp3.id3.ID3v1;
import org.farng.mp3.id3.ID3v2_4;

import java.io.IOException;

public final class Tools {
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

    /**
     * edit mp3 file
     *
     * @param _fileName
     * @param _mp3Info
     * @return
     */
    public static boolean editMp3(final String _fileName, String[] _mp3Info) {
        if (TextUtils.isEmpty(_fileName) || null == _mp3Info)
            return false;

        final String artist = _mp3Info[0];
        final String album = _mp3Info[1];
        final String title = _mp3Info[2];
        try {
            MP3File mp3 = new MP3File(_fileName);
            AbstractID3v2 id3v2 = mp3.getID3v2Tag();
            if (id3v2 != null && id3v2.getClass().toString().indexOf("ID3v2_3") > 0) {
                id3v2.setLeadArtist(artist);
                id3v2.setAlbumTitle(album);
                id3v2.setSongTitle(title);
                mp3.setID3v2Tag(id3v2);
                mp3.save();
            } else if (mp3.getID3v1Tag() != null) {
                ID3v1 id3v1 = mp3.getID3v1Tag();
                id3v1.setArtist(artist);
                id3v1.setAlbumTitle(album);
                id3v1.setSongTitle(title);
                mp3.setID3v1Tag(id3v1);
                mp3.save();
            } else {
                AbstractID3v2 id3v2_4 = new ID3v2_4();
                id3v2_4.setLeadArtist(artist);
                id3v2_4.setAlbumTitle(album);
                id3v2_4.setSongTitle(title);
                mp3.setID3v1Tag(id3v2_4);
                mp3.save();
            }
            /*// ===== rename file =====
            if (!title.equals(_oldTitle)) {
                final String path = new File(_fileName).getPath().substring(0, _fileName.lastIndexOf(File.separatorChar) + 1);
                final String postfix = _fileName.substring(_fileName.lastIndexOf('.'), _fileName.length());
                final String new_file_name = path + title + postfix;
                File new_file = new File(new_file_name);
                File old_file = new File(_fileName);
                if (!new_file.exists()) {
                    old_file.renameTo(new_file);
                    old_file.delete();
                }
            }*/
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } catch (TagException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private static void log(String _msg) {
        String TAG = Tools.class.getSimpleName();
        Log.w(TAG, "log@:::::[" + TAG + "]: " + _msg);
    }
}
