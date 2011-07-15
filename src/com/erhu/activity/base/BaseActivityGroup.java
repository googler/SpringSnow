package com.erhu.activity.base;

import android.app.ActivityGroup;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.erhu.R;
import com.erhu.activity.PlayActivity;
import com.erhu.activity.SSApplication;
import com.erhu.util.Constants;

import static com.erhu.activity.SSApplication.playerState;

public abstract class BaseActivityGroup extends ActivityGroup {

    private Button playButton;
    private TextView title;// 当前播放的音乐的名称

    // 接收 MusicService 发送的广播
    private BroadcastReceiver musicReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Constants.DURATION_ACTION)) {
                title.setText(SSApplication.getCursor().getString(1));
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        log(this, "create");
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onStart() {
        log(this, "start");
        super.onStart();
        playButton = (Button) findViewById(R.id.playbar_btn);
        title = (TextView) findViewById(R.id.playbar_title);
        title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SSApplication.getCursor() != null && SSApplication.getPosition() != -1) {
                    Intent intent = new Intent();
                    intent.setClass(BaseActivityGroup.this, PlayActivity.class);
                    startActivity(intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY));
                }
            }
        }
        );
        if (playerState != Constants.STOPPED_STATE && SSApplication.getPosition() != -1) {
            //TODO:为什么这里必须setPosition呢？否则cursor会自己跑到一个奇怪的位置
            title.setText(SSApplication.getCursor().getString(1));
        }
        playButton.setBackgroundResource(
                playerState == Constants.PLAYING_STATE ? R.drawable.pause : R.drawable.play);
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.DURATION_ACTION);
        registerReceiver(musicReceiver, filter);
    }

    // click the play button on topbar
    public void playBtnClicked(final View _view) {
        if (SSApplication.getCursor() != null && SSApplication.getPosition() != -1) {
            if (playerState == Constants.PLAYING_STATE)
                pause();
            else
                play();
        }
    }

    // 音乐暂停
    private void pause() {
        playerState = Constants.PAUSED_STATE;
        playButton.setBackgroundResource(R.drawable.play);
        Intent intent = new Intent();
        intent.setAction(Constants.SERVICE_ACTION);
        intent.putExtra("op", Constants.PAUSE_OP);
        startService(intent);
    }

    // 音乐播放
    private void play() {
        Intent intent = new Intent();
        intent.putExtra("op", playerState == Constants.PAUSED_STATE ? Constants.CONTINUE_OP : Constants.PLAY_OP);
        intent.setAction(Constants.SERVICE_ACTION);
        playButton.setBackgroundResource(R.drawable.pause);
        playerState = Constants.PLAYING_STATE;
        startService(intent);
    }

    @Override
    protected void onStop() {
        log(this, "stop");
        unregisterReceiver(musicReceiver);
        super.onStop();
    }

    protected void log(final Context _context, String _msg) {
        String TAG = _context.getClass().getSimpleName();
        Log.w(TAG, "log@:::::[" + TAG + "]: " + _msg);
    }
}
