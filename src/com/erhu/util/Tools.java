package com.erhu.util;

import android.app.NotificationManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import com.mpatric.mp3agic.ID3v1;
import com.mpatric.mp3agic.ID3v2;
import com.mpatric.mp3agic.ID3v24Tag;
import com.mpatric.mp3agic.Mp3File;

import java.io.File;
import java.io.UnsupportedEncodingException;

public final class Tools {

    public static void stopNotification(Context _context) {
        NotificationManager manager = (NotificationManager) _context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancel(Constants.NOTIFICATION_ID);
    }

    // edit mp3 file
    public static boolean editMp3(final Context _context, final int _id, String[] _mp3Info) {
        if (null == _mp3Info)
            return false;
        // ----- update file
        try {
            final String filePath = _mp3Info[3].substring(4);
            final String file_path = new File(filePath).getPath().substring(0, filePath.lastIndexOf(File.separatorChar) + 1);
            final String postfix = filePath.substring(filePath.lastIndexOf('.'), filePath.length());
            String new_file_name = file_path + _mp3Info[2] + postfix;

            Mp3File mp3 = null;
            // 新文件与原文件重名时，拷贝源文件为临时文件，
            // 然后删除源文件，然后读取临时文件的数据
            // 然后删除临时文件（so many 然后）
            if (new_file_name.equalsIgnoreCase(filePath)) {
                long time = System.currentTimeMillis();
                Mp3File t_mp3 = new Mp3File(filePath);
                t_mp3.save(file_path + time + postfix);
                new File(filePath).delete();

                mp3 = new Mp3File(file_path + time + postfix);
                changeTag(mp3, _mp3Info, new_file_name);
                new File(file_path + time + postfix).delete();
            } else {
                // 如果新文件名称与其它文件重名，则修改新文件的物理名称
                if (new File(new_file_name).exists())
                    new_file_name = file_path + _mp3Info[2] + "_" + postfix;
                mp3 = new Mp3File(filePath);
                changeTag(mp3, _mp3Info, new_file_name);
                new File(filePath).delete();
            }
            // ----- update db
            ContentValues cv = new ContentValues();
            cv.put(MediaStore.Audio.Media.TITLE, _mp3Info[2]);
            cv.put(MediaStore.Audio.Media.ARTIST, _mp3Info[0]);
            cv.put(MediaStore.Audio.Media.ALBUM, _mp3Info[1]);
            cv.put(MediaStore.Audio.Media.DATA, _mp3Info[3].substring(0, 4) + new_file_name);
            Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
            uri = ContentUris.withAppendedId(uri, _id);
            _context.getContentResolver().update(uri, cv, null, null);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private static void changeTag(final Mp3File mp3, final String[] _mp3Info, final String _new_file_name) {
        try {
            final String artist = Tools.gbk2ISO8859(_mp3Info[0]);
            final String album = Tools.gbk2ISO8859(_mp3Info[1]);
            final String title = Tools.gbk2ISO8859(_mp3Info[2]);
            /*ID3v2 id3v2 = mp3.getId3v2Tag();
            ID3v1 id3v1 = mp3.getId3v1Tag();
            if (id3v2 != null) {
                id3v2.setArtist(artist);
                id3v2.setAlbum(album);
                id3v2.setTitle(title);
                mp3.setId3v2Tag(id3v2);
            }
            if (id3v1 != null) {
                id3v1.setArtist(artist);
                id3v1.setAlbum(album);
                id3v1.setTitle(title);
                mp3.setId3v1Tag(id3v1);
            } else {
            */
            mp3.setId3v1Tag(null);
            ID3v24Tag id3v2_4 = new ID3v24Tag();
            id3v2_4.setArtist(artist);
            id3v2_4.setAlbum(album);
            id3v2_4.setTitle(title);
            mp3.setId3v2Tag(id3v2_4);
            //}
            mp3.save(_new_file_name);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // convert gbk to iso-8859-1
    public static String gbk2ISO8859(final String _gbk_str) {
        try {
            return new String(_gbk_str.getBytes("GBK"), "ISO-8859-1");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return "";
        }
    }

    // 时间格式转换
    public static String toTime(int time) {
        time /= 1000;
        int minute = time / 60;
        int hour = minute / 60;
        int second = time % 60;
        minute %= 60;
        return String.format("%02d:%02d", minute, second);
    }

    // log
    private static void log(String _msg) {
        String TAG = Tools.class.getSimpleName();
        Log.w(TAG, "log@:::::[" + TAG + "]: " + _msg);
    }
}
