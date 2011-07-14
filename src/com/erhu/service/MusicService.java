package com.erhu.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import com.erhu.R;
import com.erhu.activityGroup.IndexActivityGroup;
import com.erhu.activity.SSApplication;
import com.erhu.util.Constants;
import com.erhu.util.Tools;

import java.io.IOException;

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
        this.addNotification();
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
                                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, SSApplication.getCursor().getString(0)));
                    pre2Play();
                    handlePlayPos();
                    player.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case Constants.PAUSE_OP:
                player.pause();
                musicPos = player.getCurrentPosition();
                Tools.stopNotification(this);
                break;
            case Constants.CONTINUE_OP:
                player.seekTo(musicPos);
                player.start();
                addNotification();
                break;
            case Constants.STOP_OP:
                try {
                    if (player != null) {
                        player.stop();
                        player.prepare();
                        player.seekTo(0);
                        handler.removeMessages(1);
                    }
                    Tools.stopNotification(this);
                } catch (Exception e) {
                    e.printStackTrace();
                }
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
            addNotification();
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

    @Override
    public void onDestroy() {
        log("destroy!");
        if (player != null) {
            player.stop();
            player = null;
        }
        super.onDestroy();
    }

    @Override
    public void onCompletion(final MediaPlayer _mediaPlayer) {
        try {
            int _pos = SSApplication.getPosition();
            SSApplication.moveCursor(_pos == SSApplication.getCursor().getCount() - 1 ? 0 : _pos + 1);
            handler.removeMessages(1);
            _mediaPlayer.reset();
            _mediaPlayer.setDataSource(this,
                    Uri.withAppendedPath(
                            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, SSApplication.getCursor().getString(0)));
            pre2Play();
            player.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addNotification() {
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        String title = SSApplication.getCursor().getString(1);
        String artist = SSApplication.getCursor().getString(3);

        Intent intent = new Intent(this, IndexActivityGroup.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, intent, 0);
        Notification status = new Notification(R.drawable.sonata, title + " - " + artist, System.currentTimeMillis());
        status.flags |= Notification.FLAG_ONGOING_EVENT;
        status.setLatestEventInfo(getApplicationContext(), title, artist, contentIntent);

        //status.contentView = views;
        //status.flags |= Notification.FLAG_ONGOING_EVENT;
        //status.contentIntent = PendingIntent.getActivity(this, 0, new Intent("me.erhu.media.PLAY_VIEWER").addFlags(Intent.FLAG_ACTIVITY_NEW_TASK), 0);
        manager.notify(Constants.NOTIFICATION_ID, status);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void log(String _msg) {
        String tag = MusicService.class.getSimpleName();
        Log.w(tag, "log@:::::[" + tag + "]: " + _msg);
    }
}
