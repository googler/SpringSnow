package com.erhu.activity;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import com.erhu.R;
import com.erhu.adapter.MusicListAdapter;

import static com.erhu.activity.SSApplication.cursor;

/**
 * 音乐列表
 */
public class MusicListActivity extends ListActivity {
    private ListView listview;
    private Cursor mCursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        log("create");
        super.onCreate(savedInstanceState);
        listview = getListView();
        listview.setFastScrollEnabled(true);
        listview.setDrawSelectorOnTop(false);
        listview.setDivider(null);
        listview.setScrollingCacheEnabled(false);
        listview.setFadingEdgeLength(0);
        listview.setBackgroundResource(R.drawable.list_bg);
        listview.setOnItemClickListener(new ListItemClickListener());
        listview.setOnItemLongClickListener(new ListItemLongCLickListener());
        this.resetCursor();
    }

    @Override
    protected void onStart() {
        super.onStart();
        log("start");
        // 音乐被编辑过,重新获取Cursor
        if (SSApplication.musicEdit) {
            this.resetCursor();
            SSApplication.musicEdit = false;
        }
    }

    /**
     * 音乐被编辑后，重新设置Cursor
     */
    private void resetCursor() {
        log("reset cursor");
        if (mCursor != null)
            mCursor.close();
        mCursor = this.getContentResolver()
                .query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                        new String[]{
                                MediaStore.Audio.Media._ID,
                                MediaStore.Audio.Media.TITLE,
                                MediaStore.Audio.Media.DURATION,
                                MediaStore.Audio.Media.ARTIST,
                                MediaStore.Audio.Media.ALBUM,
                                MediaStore.Audio.Media.DISPLAY_NAME},
                        null, null, null);
        SSApplication.cursor = mCursor;
        SSApplication.setPosition();
        listview.setAdapter(new MusicListAdapter(this, mCursor));
    }

    @Override
    public void finish() {
        log("finish");
        super.finish();
    }

    @Override
    protected void onStop() {
        log("stop");
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        log("destroy");
        super.onDestroy();
    }

    /**
     * 列表项单击操作监听器类
     */
    class ListItemClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> arg0, View view, int position, long id) {
            cursor = mCursor;
            SSApplication.setPosition(position);
            Intent intent = new Intent(MusicListActivity.this, PlayActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            intent.putExtra("op", "new One");
            startActivity(intent);
        }
    }

    class ListItemLongCLickListener implements AdapterView.OnItemLongClickListener {
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int _position, long id) {
            Intent intent = new Intent(MusicListActivity.this, Mp3ProfileActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            intent.putExtra("pos", _position);
            startActivityForResult(intent, 1);
            return true;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && requestCode == 1) {
            Toast.makeText(this, "保存成功", Toast.LENGTH_SHORT).show();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void log(String _msg) {
        final String TAG = MusicListActivity.class.getSimpleName();
        Log.w(TAG, "log@:::::[" + TAG + "]: " + _msg);
    }
}
