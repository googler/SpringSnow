package com.erhu.activity;

import android.app.ActivityGroup;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.*;
import com.erhu.R;
import com.erhu.util.Constants;

/**
 * life is good:-)
 */
public class IndexActivity extends ActivityGroup {
    private PopupWindow mPop;
    private LinearLayout container;
    private LinearLayout topBar;
    private LinearLayout musicLayout;// 所有音乐
    private LinearLayout albumLayout;// 专辑
    private LinearLayout artistLayout;// 艺术家
    private View musicView;
    private View albumView;
    private View artistView;
    private View currentView;
    private TextView title;// 当前播放的音乐的名称
    private Button pauseButton;
    private TextView music;
    private TextView album;
    private TextView artist;
    /**
     * 接收 MusicService 发送的广播
     */
    private BroadcastReceiver musicReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Constants.DURATION_ACTION))
                title.setText(intent.getExtras().getString("title"));
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        log("onCrate");
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.index);
        initUI();
        initPopMenu();
    }

    /**
     * Nothing could hold a man back from his future!!!
     */
    private void initUI() {
        {
            pauseButton = (Button) findViewById(R.id.index_top_btn);
            title = (TextView) findViewById(R.id.index_top_title);
            music = (TextView) findViewById(R.id.index_bottom_music);
            album = (TextView) findViewById(R.id.index_bottom_album);
            artist = (TextView) findViewById(R.id.index_bottom_artist);
            topBar = (LinearLayout) findViewById(R.id.index_top_bar);
            topBar.getBackground().setAlpha(0x88);
            musicLayout = (LinearLayout) findViewById(R.id.index_layout_bottom_music);
            albumLayout = (LinearLayout) findViewById(R.id.index_layout_bottom_album);
            artistLayout = (LinearLayout) findViewById(R.id.index_layout_bottom_artist);
            musicLayout.setOnClickListener(itemClickListener);
            albumLayout.setOnClickListener(itemClickListener);
            artistLayout.setOnClickListener(itemClickListener);
        }
        musicView = getView(MusicListActivity.class);
        artistView = getView(ArtistListActivity.class);
        albumView = getView(AlbumListActivity.class);
        {
            currentView = musicView;
            container = (LinearLayout) findViewById(R.id.index_container);
            container.removeAllViewsInLayout();
            container.addView(currentView);
        }
    }

    View.OnClickListener itemClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            container.removeAllViewsInLayout();
            if (v == artistLayout) {
                music.setText("音乐");//TODO:以后考虑使用selector实现
                album.setText("专辑");
                artist.setText("[艺术家]");
                currentView = artistView;
            } else if (v == musicLayout) {
                music.setText("[音乐]");
                album.setText("专辑");
                artist.setText("艺术家");
                currentView = musicView;
            } else if (v == albumLayout) {
                music.setText("音乐");
                album.setText("[专辑]");
                artist.setText("艺术家");
                currentView = albumView;
            }
            container.addView(currentView);
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        log("onStart");
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.DURATION_ACTION);
        registerReceiver(musicReceiver, filter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add("menu");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onMenuOpened(int featureId, Menu menu) {
        if (mPop != null) {
            if (mPop.isShowing())
                mPop.dismiss();
            else {
                LinearLayout outerLayout = (LinearLayout) findViewById(R.id.index_outer_layout);
                initPopMenu();
                mPop.showAtLocation(outerLayout, Gravity.CENTER, 0, 0);
            }
        }
        return false;
    }

    private void initPopMenu() {
        View v = getLayoutInflater().inflate(R.layout.popupmenu, null);
        mPop = new PopupWindow(v, AbsListView.LayoutParams.FILL_PARENT, AbsListView.LayoutParams.WRAP_CONTENT);
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
        super.onStop();
        log("onStop");
        unregisterReceiver(musicReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        log("onDestroy");
    }

    private void log(String _msg) {
        String TAG = IndexActivity.class.getSimpleName();
        Log.i(TAG, "log@::::::::::::::::::::::::::::::::[" + TAG + "]: " + _msg);
    }
}
