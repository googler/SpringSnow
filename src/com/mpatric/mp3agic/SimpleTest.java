package com.mpatric.mp3agic;

import com.erhu.util.Tools;
import com.mpatric.mp3agic.*;

import java.io.File;
import java.io.IOException;

public class SimpleTest {
    private static final String filePath = "C:\\Documents and Settings\\Erhu\\桌面\\mp3_bak\\飞的更高.mp3";

    public static void main(String[] args) {
        try {
            String[] _mp3Info = {"二f胡", "好人", "飞的更高"};
            final String file_path = new File(filePath).getPath().substring(0, filePath.lastIndexOf(File.separatorChar) + 1);
            final String postfix = filePath.substring(filePath.lastIndexOf('.'), filePath.length());
            String new_file_name = file_path + _mp3Info[2] + postfix;

            Mp3File mp3 = null;
            // 重新命名的文件与原文件重名时，拷贝源文件为临时文件，并删除源文件，使用临时文件处理
            if (new_file_name.equalsIgnoreCase(filePath)) {
                long time = System.currentTimeMillis();
                Mp3File t_mp3 = new Mp3File(filePath);
                t_mp3.save(file_path + time + postfix);
                new File(filePath).delete();

                mp3 = new Mp3File(file_path + time + postfix);
                changeTag(mp3, _mp3Info, filePath);
                new File(file_path + time + postfix).delete();
            } else {
                if (new File(new_file_name).exists()) {
                    new_file_name = file_path + _mp3Info[2] + "_" + postfix;
                }
                mp3 = new Mp3File(filePath);
                changeTag(mp3, _mp3Info, new_file_name);
                new File(filePath).delete();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void changeTag(Mp3File mp3, final String[] _mp3Info, final String _new_file_name) throws InvalidDataException, IOException, UnsupportedTagException, NotSupportedException {
        final String artist = Tools.convertGBK2ISO8859(_mp3Info[0]);
        final String album = Tools.convertGBK2ISO8859(_mp3Info[1]);
        final String title = Tools.convertGBK2ISO8859(_mp3Info[2]);
        ID3v2 id3v2 = mp3.getId3v2Tag();
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
            ID3v2 id3v2_4 = new ID3v23Tag();
            id3v2_4.setArtist(artist);
            id3v2_4.setAlbum(album);
            id3v2_4.setTitle(title);
            mp3.setId3v2Tag(id3v2_4);
        }
        mp3.save(_new_file_name);
    }
}
