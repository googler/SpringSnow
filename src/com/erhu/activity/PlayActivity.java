package com.erhu.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import com.erhu.R;
import com.erhu.util.Constants;
import com.erhu.util.Tools;

import static com.erhu.activity.SSApplication.*;

public class PlayActivity extends Activity {
    private static final String TAG = PlayActivity.class.getSimpleName();
    // UI
    private SeekBar seekBar;
    private TextView title;
    private TextView timeLeft;
    private TextView artist;
    private Button playBtn;

    //private int position;// 第几首歌?
    private int duration;// 歌曲长度
    private int currentPosition;//当前播放位置

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        log("onCreate");
        super.onCreate(savedInstanceState);
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.play);

        // 半透明效果
        // LinearLayout outer_0 = (LinearLayout) findViewById(R.id.play_layout_top_outer);
        // outer_0.getBackground().setAlpha(0x88);
        title = (TextView) (this.findViewById(R.id.play_title));
        timeLeft = (TextView) this.findViewById(R.id.play_time_left);
        artist = (TextView) (this.findViewById(R.id.play_artist));
        seekBar = (SeekBar) (this.findViewById(R.id.play_seekbar));
        playBtn = (Button) this.findViewById(R.id.play_btn_pause);
        if (SSApplication.playerState != Constants.PLAYING_STATE)
            playBtn.setBackgroundResource(R.drawable.play);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                play(position);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                pause();
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    seekBar_change(progress);
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        log("onStart");
        regReceiver();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {// 一首全新的歌曲，而非查看正在播放的歌曲
            position = bundle.getInt("position");
            seekBar.setProgress(0);
            play(position);
        } else {
            duration = SSApplication.durations[position];
            seekBar.setMax(duration);
        }
        title.setText(titles[position]);
        artist.setText(artists[position]);
    }

    /**
     * register receiver
     */
    private void regReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.CURRENT_ACTION);
        filter.addAction(Constants.DURATION_ACTION);
        filter.addAction(Constants.CONTINUE_ACTION);
        registerReceiver(musicReceiver, filter);
        log("register music receiver success");
    }

    /**
     * 点击播放/暂停按钮
     */
    public void btnPauseClicked(View _e) {
        if (SSApplication.playerState == Constants.PLAYING_STATE)
            pause();
        else
            play(position);
    }

    /**
     * 音乐暂停
     */
    private void pause() {
        SSApplication.playerState = Constants.PAUSED_STATE;
        playBtn.setBackgroundResource(R.drawable.play);
        Intent intent = new Intent();
        intent.setAction(Constants.SERVICE_ACTION);
        intent.putExtra("op", Constants.PAUSE_OP);
        startService(intent);
    }

    /**
     * 下一首
     */
    public void btnNextClicked(View _e) {
        position = (position == ids.length - 1 ? 0 : position + 1);
        SSApplication.playerState = Constants.PLAYING_STATE;
        play(position);
    }

    // 上一首
    public void btnPrevClicked(View _e) {
        position = (position == 0 ? position = ids.length - 1 : position - 1);
        SSApplication.playerState = Constants.PLAYING_STATE;
        play(position);
    }

    /**
     * 音乐播放
     */
    private void play(final int _position) {
        Intent intent = new Intent();
        if (SSApplication.playerState == Constants.PAUSED_STATE)
            intent.putExtra("op", Constants.CONTINUE_OP);
        else {
            intent.putExtra("position", _position);
            intent.putExtra("op", Constants.PLAY_OP);
        }
        intent.setAction(Constants.SERVICE_ACTION);
        playBtn.setBackgroundResource(R.drawable.pause);
        SSApplication.playerState = Constants.PLAYING_STATE;
        startService(intent);
    }

    /**
     * 用户拖动进度条
     */
    private void seekBar_change(int progress) {
        Intent intent = new Intent();
        intent.setAction(Constants.SERVICE_ACTION);
        intent.putExtra("op", Constants.PROCESS_CHANGE_OP);
        intent.putExtra("progress", progress);
        startService(intent);
    }

    /**
     * 定义musicReceiver,接收MusicService发送的广播
     */
    protected BroadcastReceiver musicReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Constants.CURRENT_ACTION)) {
                currentPosition = intent.getExtras().getInt("currentTime");
                seekBar.setProgress(currentPosition);
                timeLeft.setText(Tools.toTime(duration - currentPosition));
            } else if (action.equals(Constants.DURATION_ACTION)) {
                seekBar.setProgress(0);
                position = intent.getExtras().getInt("position");
                duration = durations[position];
                seekBar.setMax(duration);
                title.setText(titles[position]);
                artist.setText(artists[position]);
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        log("onDestroy");
    }

    @Override
    protected void onPause() {
        super.onPause();
        log("onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        log("onStop");
        unregisterReceiver(musicReceiver);
    }

    private void log(String _msg) {
        Log.w(TAG, "log@::::::::::::::::::::::::::::::::[" + TAG + "]: " + _msg);
    }
}
