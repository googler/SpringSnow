package com.erhu.service;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import com.erhu.activity.SSApplication;
import com.erhu.util.Constants;

import java.io.IOException;

import static com.erhu.activity.SSApplication.cursor;

public class MusicService extends Service implements MediaPlayer.OnCompletionListener {

    private Handler handler = null;
    private MediaPlayer player;
    private int musicPos = 0;// 某一首歌内部的位置

    @Override
    public void onCreate() {
        super.onCreate();
        log("create");
        if (player != null) {
            player.reset();
            player.release();
            player = null;
        }
        player = new MediaPlayer();
        player.setOnCompletionListener(this);
    }

    @Override
    public void onStart(Intent _intent, int startId) {
        super.onStart(_intent, startId);
        log("start");
        int operation = _intent.getIntExtra("op", -1);
        switch (operation) {
            case Constants.PLAY_OP:
                try {
                    player.reset();
                    player.setDataSource(this,
                            Uri.withAppendedPath(
                                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, cursor.getString(0)));
                    pre2Play();
                    handlePlayPos();
                    player.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case Constants.PAUSE_OP:
                pause();
                break;
            case Constants.CONTINUE_OP:
                player.seekTo(musicPos);
                player.start();
                break;
            case Constants.STOP_OP:
                stop();
                break;
            case Constants.PROCESS_CHANGE_OP:
                player.seekTo(_intent.getExtras().getInt("progress"));
                musicPos = player.getCurrentPosition();
                break;
        }
    }

    private void pre2Play() {
        try {
            if (!player.isPlaying())
                player.prepare();
            player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    handler.sendEmptyMessage(1);
                }
            });
            // 获取歌曲总时长
            final Intent intent = new Intent();
            intent.setAction(Constants.DURATION_ACTION);
            sendBroadcast(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handlePlayPos() {
        if (handler == null) {
            handler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    if (msg.what == 1) {
                        if (player != null) {
                            final Intent intent = new Intent();
                            intent.setAction(Constants.CURRENT_ACTION);
                            intent.putExtra("currentTime", player.getCurrentPosition());
                            sendBroadcast(intent);
                            handler.sendEmptyMessageDelayed(1, 500);
                        }
                    }
                }
            };
        }
    }

    /**
     * 停止播放
     */
    private void stop() {
        try {
            if (player != null) {
                player.stop();
                player.prepare();
                player.seekTo(0);
                handler.removeMessages(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        log("destroy!");
        if (player != null) {
            player.stop();
            player = null;
        }
        super.onDestroy();
    }

    /**
     * 歌曲播完了
     */
    @Override
    public void onCompletion(final MediaPlayer _mediaPlayer) {
        try {
            int _pos = SSApplication.getPosition();
            SSApplication.setPosition(_pos == cursor.getCount() - 1 ? 0 : _pos++);
            handler.removeMessages(1);
            _mediaPlayer.reset();
            _mediaPlayer.setDataSource(this,
                    Uri.withAppendedPath(
                            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, cursor.getString(0)));
            pre2Play();
            player.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 暂停时
     */
    public void pause() {
        if (player != null) {
            player.pause();
            musicPos = player.getCurrentPosition();
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void log(String _msg) {
        String tag = MusicService.class.getSimpleName();
        Log.w(tag, "log@::::::::::::::::::::::::::::::::[" + tag + "]: " + _msg);
    }
}
