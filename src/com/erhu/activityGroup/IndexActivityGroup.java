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
import com.erhu.activity.AllMusicListActivity;
import com.erhu.activity.ArtistListActivity;
import com.erhu.activity.FavouriteListActivity;
import com.erhu.activity.base.BaseActivityGroup;
import com.erhu.util.Constants;
import com.erhu.util.Tools;

// life is good:-)
public class IndexActivityGroup extends BaseActivityGroup {

    private LinearLayout midContainer;

    private LinearLayout musicLayout;// 所有音乐
    private LinearLayout favouriteLayout;// 最爱
    private LinearLayout artistLayout;// 艺术家

    private TextView music;
    private TextView favourite;
    private TextView artist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        log(this, "create");
        setContentView(R.layout.index);
        super.onCreate(savedInstanceState);
        initUI();
    }

    // Nothing could hold a man back from his future!
    private void initUI() {
        music = (TextView) findViewById(R.id.index_bottom_music);
        favourite = (TextView) findViewById(R.id.index_bottom_favourite);
        artist = (TextView) findViewById(R.id.index_bottom_artist);
        musicLayout = (LinearLayout) findViewById(R.id.index_layout_bottom_music);
        favouriteLayout = (LinearLayout) findViewById(R.id.index_layout_bottom_favourite);
        artistLayout = (LinearLayout) findViewById(R.id.index_layout_bottom_artist);
        midContainer = (LinearLayout) findViewById(R.id.index_mid_container);

        musicLayout.setOnClickListener(bottomItemClickListener);
        favouriteLayout.setOnClickListener(bottomItemClickListener);
        artistLayout.setOnClickListener(bottomItemClickListener);

        midContainer.removeAllViewsInLayout();
        midContainer.addView(getView(AllMusicListActivity.class));
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
        Intent t_intent = new Intent(this, _activity).setFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        View t_view = getLocalActivityManager().startActivity(_activity.getClass().getSimpleName(), t_intent).getDecorView();
        return t_view;
    }

    // 底部导航区域监听器
    View.OnClickListener bottomItemClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View _view) {
            midContainer.removeAllViewsInLayout();
            if (_view == artistLayout) {
                music.setText("音乐");//TODO:以后考虑使用selector实现
                favourite.setText("最爱");
                artist.setText("[艺术家]");
                midContainer.addView(getView(ArtistListActivity.class));
            } else if (_view == musicLayout) {
                music.setText("[音乐]");
                favourite.setText("最爱");
                artist.setText("艺术家");
                midContainer.addView(getView(AllMusicListActivity.class));
            } else if (_view == favouriteLayout) {
                music.setText("音乐");
                favourite.setText("[最爱]");
                artist.setText("艺术家");
                midContainer.addView(getView(FavouriteListActivity.class));
            }
        }
    };
}
