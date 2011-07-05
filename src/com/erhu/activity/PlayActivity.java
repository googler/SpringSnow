package com.erhu.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.*;
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
    private ImageView imageList;
    private ImageView imageMyLove;
    private ImageButton imagePlayFor;

    private int position;// 第几首歌?
    private int duration;// 歌曲长度
    public int playState = Constants.STOPPED_STATE;// 程序启动时播放器出于停止状态
    private int currentPosition;//当前播放位置

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        log("onCreate");
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.play);

        LinearLayout imagea = (LinearLayout) findViewById(R.id.imagea);
        imagea.getBackground().setAlpha(0x88);
        LinearLayout imageb = (LinearLayout) findViewById(R.id.imageb);
        imageb.getBackground().setAlpha(0x88);
        LinearLayout imagec = (LinearLayout) findViewById(R.id.imagec);
        imagec.getBackground().setAlpha(0x88);
        LinearLayout imaged = (LinearLayout) findViewById(R.id.imaged);
        imaged.getBackground().setAlpha(0x88);

        imageList = (ImageView) findViewById(R.id.imageList);
        imageList.setAlpha(0x88);
        title = (TextView) (this.findViewById(R.id.musicTitle));
        timeLeft = (TextView) this.findViewById(R.id.time_left);
        artist = (TextView) (this.findViewById(R.id.artist));
        seekBar = (SeekBar) (this.findViewById(R.id.seekBar));
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
        playBtn = (Button) this.findViewById(R.id.btnPause);
        // 获取从音乐列表传递来的数据(所有音乐信息)
        Bundle bundle = getIntent().getExtras();
        position = bundle.getInt("position");
    }

    @Override
    protected void onStart() {
        super.onStart();
        log("onStart");
        regReceiver();
        // 一首全新的歌曲，而非查看正在播放的歌曲
        Intent intent = getIntent();
        if (intent != null && intent.getExtras().get("op") != null) {
            duration = SSApplication.durations[position];
            seekBar.setMax(duration);
        } else {
            seekBar.setProgress(0);
            play(position);
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
        if (this.playState == Constants.PLAYING_STATE)
            pause();
        else
            play(position);
    }

    /**
     * 音乐暂停
     */
    private void pause() {
        playState = Constants.PAUSED_STATE;
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
        playState = Constants.PLAYING_STATE;
        play(position);
    }

    // 上一首
    public void btnPrevClicked(View _e) {
        position = (position == 0 ? position = ids.length - 1 : position - 1);
        playState = Constants.PLAYING_STATE;
        play(position);
    }

    /**
     * 音乐播放
     */
    private void play(final int _position) {
        Intent intent = new Intent();
        if (playState == Constants.PAUSED_STATE)
            intent.putExtra("op", Constants.CONTINUE_OP);
        else {
            intent.putExtra("position", _position);
            intent.putExtra("op", Constants.PLAY_OP);
        }
        intent.setAction(Constants.SERVICE_ACTION);
        playBtn.setBackgroundResource(R.drawable.pause);
        playState = Constants.PLAYING_STATE;
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
        super.onStop();    //To change body of overridden methods use File | Settings | File Templates.
        log("onStop");
        unregisterReceiver(musicReceiver);
    }

    private void log(String _msg) {
        Log.w(TAG, "log@::::::::::::::::::::::::::::::::[" + TAG + "]: " + _msg);
    }
}
