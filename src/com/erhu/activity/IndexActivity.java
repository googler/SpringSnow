package com.erhu.activity;

import android.app.ActivityGroup;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.erhu.R;
import com.erhu.util.Constants;
import com.erhu.util.Tools;

import static com.erhu.activity.SSApplication.cursor;
import static com.erhu.activity.SSApplication.playerState;

/**
 * life is good:-)
 */
public class IndexActivity extends ActivityGroup {
    private LinearLayout container;
    private LinearLayout musicLayout;// 所有音乐
    private LinearLayout favouriteLayout;// 最爱
    private LinearLayout artistLayout;// 艺术家
    private View musicView;
    private View favouriteView;
    private View artistView;
    private TextView title;// 当前播放的音乐的名称
    private Button playButton;
    private TextView music;
    private TextView favourite;
    private TextView artist;

    /**
     * 接收 MusicService 发送的广播
     */
    private BroadcastReceiver musicReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Constants.DURATION_ACTION)) {
                SSApplication.setPosition();
                title.setText(cursor.getString(1));
            }
        }
    };
    /**
     * 底部导航区域监听器
     */
    View.OnClickListener bottomItemClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View _view) {
            container.removeAllViewsInLayout();
            if (_view == artistLayout) {
                music.setText("音乐");//TODO:以后考虑使用selector实现
                favourite.setText("最爱");
                artist.setText("[艺术家]");
                artistView = getView(ArtistListActivity.class);
                container.addView(artistView);
            } else if (_view == musicLayout) {
                music.setText("[音乐]");
                favourite.setText("最爱");
                artist.setText("艺术家");
                musicView = getView(MusicListActivity.class);
                container.addView(musicView);
            } else if (_view == favouriteLayout) {
                music.setText("音乐");
                favourite.setText("[最爱]");
                artist.setText("艺术家");
                favouriteView = getView(FavouriteListActivity.class);
                container.addView(favouriteView);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        log("create");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.index);
        initUI();
    }

    /**
     * Nothing could hold a man back from his future!!!
     */
    private void initUI() {
        playButton = (Button) findViewById(R.id.index_top_btn);
        title = (TextView) findViewById(R.id.index_top_title);
        music = (TextView) findViewById(R.id.index_bottom_music);
        favourite = (TextView) findViewById(R.id.index_bottom_favourite);
        artist = (TextView) findViewById(R.id.index_bottom_artist);
        musicLayout = (LinearLayout) findViewById(R.id.index_layout_bottom_music);
        favouriteLayout = (LinearLayout) findViewById(R.id.index_layout_bottom_favourite);
        artistLayout = (LinearLayout) findViewById(R.id.index_layout_bottom_artist);
        container = (LinearLayout) findViewById(R.id.index_container);

        musicLayout.setOnClickListener(bottomItemClickListener);
        favouriteLayout.setOnClickListener(bottomItemClickListener);
        artistLayout.setOnClickListener(bottomItemClickListener);

        musicView = getView(MusicListActivity.class);
        container.removeAllViewsInLayout();
        container.addView(musicView);

        title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cursor != null && SSApplication.getPosition() != -1) {
                    Intent intent = new Intent();
                    intent.setClass(IndexActivity.this, PlayActivity.class);
                    startActivity(intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY));
                }
            }
        }
        );
    }

    @Override
    protected void onStart() {
        log("start");
        super.onStart();
        if (playerState != Constants.STOPPED_STATE && SSApplication.getPosition() != -1) {
            SSApplication.setPosition();//TODO:为什么这里必须setPosition呢？否则cursor会自己跑到一个奇怪的位置
            title.setText(cursor.getString(1));
        }
        playButton.setBackgroundResource(playerState == Constants.PLAYING_STATE ?
                R.drawable.pause : R.drawable.play);
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.DURATION_ACTION);
        registerReceiver(musicReceiver, filter);
    }

    /**
     * click the play button on topbar
     *
     * @param _view
     */
    public void playBtnClicked(final View _view) {
        if (cursor != null && SSApplication.getPosition() != -1) {
            if (playerState == Constants.PLAYING_STATE)
                pause();
            else
                play();
        }
    }

    @Override
    public void finish() {
        log("finish");
        super.finish();
    }

    /**
     * 音乐暂停
     */
    private void pause() {
        playerState = Constants.PAUSED_STATE;
        playButton.setBackgroundResource(R.drawable.play);
        Intent intent = new Intent();
        intent.setAction(Constants.SERVICE_ACTION);
        intent.putExtra("op", Constants.PAUSE_OP);
        startService(intent);
    }

    /**
     * 音乐播放
     */
    private void play() {
        Intent intent = new Intent();
        intent.putExtra("op",
                playerState == Constants.PAUSED_STATE ?
                        Constants.CONTINUE_OP : Constants.PLAY_OP);

        intent.setAction(Constants.SERVICE_ACTION);
        playButton.setBackgroundResource(R.drawable.pause);
        playerState = Constants.PLAYING_STATE;
        startService(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.index_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.index_menu_exit:
                stopService(new Intent().setAction(Constants.SERVICE_ACTION));
                Tools.stopNotification(this);
                android.os.Process.killProcess(android.os.Process.myPid());
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    /**
     * 根据class获取加载到activityGroup中的视图
     *
     * @param _activity
     * @return
     */
    private View getView(Class<?> _activity) {
        Intent t_intent = new Intent(this, _activity).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        View t_view = getLocalActivityManager().startActivity(_activity.getClass().getSimpleName(), t_intent).getDecorView();
        return t_view;
    }

    @Override
    protected void onStop() {
        log("stop");
        unregisterReceiver(musicReceiver);
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        log("destroy");
        super.onDestroy();
    }

    private void log(String _msg) {
        String TAG = IndexActivity.class.getSimpleName();
        Log.w(TAG, "log@:::::[" + TAG + "]: " + _msg);
    }
}
