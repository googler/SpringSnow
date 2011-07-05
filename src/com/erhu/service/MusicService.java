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
import com.erhu.util.Constants;

import java.io.IOException;

import static com.erhu.activity.SSApplication.ids;

public class MusicService extends Service implements MediaPlayer.OnCompletionListener {

    private static final String TAG = MusicService.class.getSimpleName();
    private int position;//当前播放第几首歌
    private Handler handler = null;
    private MediaPlayer player;
    public int musicPos = 0;// 某一首歌内部的位置

    @Override
    public void onCreate() {
        super.onCreate();
        log("onCreate()");
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
        log("Start");
        int operation = _intent.getIntExtra("op", -1);
        switch (operation) {
            case Constants.PLAY_OP:
                try {
                    player.reset();
                    int t_position = _intent.getIntExtra("position", -1);
                    if (position != -1) {
                        this.position = t_position;
                        if (ids[position] != -1) {
                            player.setDataSource(this,
                                    Uri.withAppendedPath(
                                            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, "" + ids[position]));
                            pre2Play();
                            handlePlayPos();
                            player.start();
                        }
                    }
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
            intent.putExtra("position", position);
            sendBroadcast(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handlePlayPos() {
        log("Prepare to play -- current");
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
        super.onDestroy();
        log("Service destroy!");
        if (player != null) {
            player.stop();
            player = null;
        }
    }

    /**
     * 歌曲播完了
     */
    @Override
    public void onCompletion(final MediaPlayer _mediaPlayer) {
        try {
            handler.removeMessages(1);
            position = ((ids.length == 1 || position == ids.length - 1) ? 0 : position + 1);
            _mediaPlayer.reset();
            _mediaPlayer.setDataSource(this,
                    Uri.withAppendedPath(
                            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, "" + ids[position]));
            pre2Play();
            player.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 暂时时
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
        Log.i(TAG, "(:-----------------------" + TAG + ":)" + _msg);
    }
}
