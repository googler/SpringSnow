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
    private View musicView;
    private RelativeLayout topBar;
    private View albumView;
    private View artistView;
    private TextView title;// 当前播放的音乐的名称
    private LinearLayout musicLayout;// 所有音乐
    private LinearLayout albumLayout;// 专辑
    private LinearLayout artistLayout;// 艺术家
    private TextView music;
    private TextView album;
    private TextView artist;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.index);

        container = (LinearLayout) findViewById(R.id.index_container);
        title = (TextView) findViewById(R.id.title);
        music = (TextView) findViewById(R.id.index_bottom_music);
        album = (TextView) findViewById(R.id.index_bottom_album);
        artist = (TextView) findViewById(R.id.index_bottom_artist);
        topBar = (RelativeLayout) findViewById(R.id.index_top_bar);
        topBar.getBackground().setAlpha(0x88);
        topBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Intent intent = new Intent();
                //intent.setClass(IndexActivity.this, PlayActivity.class);
                // startActivity(intent);
            }
        });


        artistView = getView(ArtistListActivity.class);
        {
            musicView = getView(MusicListActivity.class);
            container.removeAllViewsInLayout();
            container.addView(musicView);

            musicLayout = (LinearLayout) findViewById(R.id.index_layout_bottom_music);
            musicLayout.setOnClickListener(new android.view.View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    music.setText("[音乐]");
                    album.setText("专辑");
                    artist.setText("艺术家");
                    container.removeAllViewsInLayout();
                    container.addView(musicView);
                }
            });
            albumLayout = (LinearLayout) findViewById(R.id.index_layout_bottom_album);
            albumLayout.setOnClickListener(new android.view.View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (albumView == null)
                        albumView = getView(AlbumListActivity.class);
                    music.setText("音乐");
                    album.setText("[专辑]");
                    artist.setText("艺术家");
                    container.removeAllViewsInLayout();
                    container.addView(albumView);
                }
            });
            artistLayout = (LinearLayout) findViewById(R.id.index_layout_bottom_artist);
            artistLayout.setOnClickListener(new android.view.View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (artistView == null)
                        artistView = getView(AlbumListActivity.class);
                    music.setText("音乐");
                    album.setText("专辑");
                    artist.setText("[艺术家]");
                    container.removeAllViewsInLayout();
                    container.addView(artistView);
                }
            });
        }
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.DURATION_ACTION);
        registerReceiver(musicReceiver, filter);
        initPopMenu();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add("menu");
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * 点击菜单键
     *
     * @param featureId
     * @param menu
     * @return
     */
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

    /**
     * 接收 MusicService 发送的广播
     */
    protected BroadcastReceiver musicReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Constants.DURATION_ACTION)) {
                title.setText("正在播放: " + intent.getExtras().getString("title"));
                //log("广播接收器收到歌曲总长度了:" + intent.getExtras().getInt("duration"));
            } /*else if (action.equals(MUSIC_NEXT)) {
                log("放下一曲");
            } else if (action.equals(MUSIC_UPDATE)) {
                position = intent.getExtras().getInt("position");
                seekBar.setProgress(0);
                title.setText(_titles[position]);
                artist.setText(_artists[position]);
            }*/
        }
    };

    private void log(String _msg) {
        String TAG = IndexActivity.class.getSimpleName();
        Log.i(TAG, "(:-----------------------" + TAG + ":)" + _msg);
    }
}
