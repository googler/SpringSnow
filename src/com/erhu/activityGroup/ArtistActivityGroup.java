package com.erhu.activityGroup;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.erhu.R;
import com.erhu.activity.base.BaseActivityGroup;
import com.erhu.activity.list.AlbumListActivity;
import com.erhu.activity.list.MusicListActivity;
import com.erhu.util.Constants;
import com.erhu.util.Tools;

// 某艺术家的所有音乐列表界面
public class ArtistActivityGroup extends BaseActivityGroup {
    private LinearLayout midContainer;

    private LinearLayout allMusicLayout;// 最爱
    private LinearLayout albumsLayout;// 艺术家

    private TextView artist;
    private TextView allMusic;
    private TextView albums;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        log(this, "create");
        setContentView(R.layout.group_artist);
        super.onCreate(savedInstanceState);
        initUI();
    }

    // Nothing could hold a man back from his future!
    private void initUI() {
        allMusic = (TextView) findViewById(R.id.artist_bottom_music);
        albums = (TextView) findViewById(R.id.artist_bottom_albums);
        artist = (TextView) findViewById(R.id.artist_bottom_artist);

        allMusicLayout = (LinearLayout) findViewById(R.id.artist_layout_bottom_music);
        albumsLayout = (LinearLayout) findViewById(R.id.artist_layout_bottom_albums);
        allMusicLayout.setOnClickListener(bottomItemClickListener);
        albumsLayout.setOnClickListener(bottomItemClickListener);

        midContainer = (LinearLayout) findViewById(R.id.artist_mid_container);
        midContainer.removeAllViewsInLayout();
        midContainer.addView(getView(MusicListActivity.class));
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

    // 根据class获取加载到activityGroup中的视图
    private View getView(Class<?> _activity) {
        Bundle bundle = getIntent().getExtras();
        String artist_name = bundle.getString("artist_name");
        Intent t_intent = new Intent(this, _activity).setFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        t_intent.putExtra("artist_name", artist_name);
        artist.setText(artist_name + "->");
        View t_view = getLocalActivityManager().startActivity(_activity.getClass().getSimpleName(), t_intent).getDecorView();
        return t_view;
    }

    // 底部导航区域监听器
    View.OnClickListener bottomItemClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View _view) {
            midContainer.removeAllViewsInLayout();
            if (_view == albumsLayout) {
                allMusic.setText("音乐");//TODO:以后考虑使用selector实现
                albums.setText("[唱片]");
                midContainer.addView(getView(AlbumListActivity.class));
            } else if (_view == allMusicLayout) {
                allMusic.setText("[音乐]");
                albums.setText("唱片");
                midContainer.addView(getView(MusicListActivity.class));
            }
        }
    };
}
