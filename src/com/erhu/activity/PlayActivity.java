package com.erhu.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import com.erhu.R;
import com.erhu.util.Constants;
import com.erhu.util.Tools;

public class PlayActivity extends Activity {
    // UI
    private SeekBar seekBar;
    private TextView title;
    private TextView timeLeft;
    private TextView artist;
    private Button playBtn;
    private int currentPosition;//当前播放位置

    /**
     * 定义musicReceiver,接收MusicService发送的广播
     */
    protected BroadcastReceiver musicReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Cursor cur = SSApplication.getCursor();
            String action = intent.getAction();
            if (action.equals(Constants.CURRENT_ACTION)) {
                currentPosition = intent.getExtras().getInt("currentTime");
                seekBar.setProgress(currentPosition);
                timeLeft.setText(Tools.toTime(cur.getInt(2) - currentPosition));
            } else if (action.equals(Constants.DURATION_ACTION)) {
                seekBar.setProgress(0);
                seekBar.setMax(cur.getInt(2));
                title.setText(cur.getString(1));
                artist.setText(cur.getString(3));
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        log("create");
        super.onCreate(savedInstanceState);
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
                play();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                pause();
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser)
                    seekbarChange(progress);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        log("start");
        regReceiver();
        Cursor cur = SSApplication.getCursor();//TODO:为什么这里必须setPosition呢？否则cursor会自己跑到一个奇怪的位置
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {// 一首全新的歌曲，而非查看正在播放的歌曲
            seekBar.setProgress(0);
            play();
        } else
            seekBar.setMax(cur.getInt(2));
        title.setText(cur.getString(1));
        artist.setText(cur.getString(3));
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
            play();
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
        int position = SSApplication.getPosition();
        SSApplication.moveCursor(position == SSApplication.getCursor().getCount() - 1 ? 0 : position + 1);
        SSApplication.playerState = Constants.PLAYING_STATE;
        play();
    }

    // 上一首
    public void btnPrevClicked(View _e) {
        int position = SSApplication.getPosition();
        SSApplication.moveCursor(position == 0 ? SSApplication.getCursor().getCount() - 1 : position - 1);
        SSApplication.playerState = Constants.PLAYING_STATE;
        play();
    }

    /**
     * 音乐播放
     */
    private void play() {
        Intent intent = new Intent();
        intent.putExtra("op",
                SSApplication.playerState == Constants.PAUSED_STATE
                        ? Constants.CONTINUE_OP : Constants.PLAY_OP);
        intent.setAction(Constants.SERVICE_ACTION);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        playBtn.setBackgroundResource(R.drawable.pause);
        SSApplication.playerState = Constants.PLAYING_STATE;
        startService(intent);
    }

    /**
     * 用户拖动进度条
     */
    private void seekbarChange(int progress) {
        Intent intent = new Intent();
        intent.setAction(Constants.SERVICE_ACTION);
        intent.putExtra("op", Constants.PROCESS_CHANGE_OP);
        intent.putExtra("progress", progress);
        startService(intent);
    }

    /**
     * 回到列表界面
     *
     * @param _view
     */
    public void listImgClicked(View _view) {
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && requestCode == 1) {
            Toast.makeText(this, "保存成功", Toast.LENGTH_SHORT).show();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        log("destroy");
    }

    @Override
    protected void onPause() {
        super.onPause();
        log("pause");
    }

    @Override
    protected void onStop() {
        log("stop");
        unregisterReceiver(musicReceiver);
        super.onStop();
    }

    @Override
    public void finish() {
        log("finish");
        super.finish();
    }

    private void log(String _msg) {
        final String tag = PlayActivity.class.getSimpleName();
        Log.w(tag, "log@:::::[" + tag + "]: " + _msg);
    }
}
