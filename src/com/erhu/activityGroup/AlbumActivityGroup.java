package com.erhu.activityGroup;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.erhu.R;
import com.erhu.activity.base.BaseActivityGroup;
import com.erhu.activity.list.MusicListActivity;

public class AlbumActivityGroup extends BaseActivityGroup {
    private LinearLayout midContainer;


    private TextView artistAlbum;
    private TextView back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.group_album);
        super.onCreate(savedInstanceState);
        initUI();
    }

    // Nothing could hold a man back from his future!
    private void initUI() {
        back = (TextView) findViewById(R.id.album_bottom_back);
        artistAlbum = (TextView) findViewById(R.id.album_bottom_artist_album);
        midContainer = (LinearLayout) findViewById(R.id.album_mid_container);
        midContainer.removeAllViewsInLayout();
        midContainer.addView(getView(MusicListActivity.class));
    }

    // 根据class获取加载到activityGroup中的视图
    private View getView(Class<?> _activity) {
        Bundle bundle = getIntent().getExtras();
        String artist_name = bundle.getString("artist_name");
        String album_name = bundle.getString("album_name");
        Intent intent = new Intent(this, _activity).setFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        intent.putExtra("artist_name", artist_name);
        intent.putExtra("album_name", album_name);
        String t_txt = artist_name + ":" + album_name;
        if (t_txt.length() > 30)
            t_txt = t_txt.substring(0, 30);
        artistAlbum.setText(t_txt);
        View t_view = getLocalActivityManager().startActivity(_activity.getClass().getSimpleName(), intent).getDecorView();
        return t_view;
    }

    public void backBtnClicked(View _view) {
        finish();
    }
}
