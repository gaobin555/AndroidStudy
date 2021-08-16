package com.android.xthink.ink.launcherink.ui.home.music;

import android.Manifest;
import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.android.xthink.ink.launcherink.R;
import com.android.xthink.ink.launcherink.utils.InkDeviceUtils;
import com.eink.swtcon.SwtconControl;

import static com.android.xthink.ink.launcherink.base.mvp.MainBaseActivity.requestPermisson;

public class MusicListActivity extends AppCompatActivity {
    private String TAG = "MusicListActivity";
    private ListView mListView;
    private MusicAdapter mAdapter;

    public static void start(Context context) {
        Intent starter = new Intent(context, MusicListActivity.class);
        starter.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        InkDeviceUtils.setUpdateModeforActivity(SwtconControl.WF_MODE_DU2);
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_list);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @android.annotation.NonNull String[] permissions, @android.annotation.NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1) {
            Playhelper.addMusicList(this);
        }
        //sendKeyCode(KeyEvent.KEYCODE_DPAD_DOWN);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        if (requestPermisson(this, Manifest.permission.READ_EXTERNAL_STORAGE, 1)) {
            Playhelper.addMusicList(this);
        }
        //对Listview进行监听
        mListView = (ListView) findViewById(R.id.logic_lv);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {             //将listView的每一个item实现监听
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                for (Music m : Common.musicList) {
                    m.isPlaying = false;
                }
                Common.setPosition(position);
                Common.needSwitchMusic = true;
                //更新界面
                mAdapter.notifyDataSetChanged();
                Log.d(TAG, "onItemClick position = " + position);
                finish();
            }
        });
        mAdapter = new MusicAdapter(this, Common.musicList);                //创建MusicAdapter的对象，实现自定义适配器的创建
        mListView.setAdapter(mAdapter);                                                 //listView绑定适配器
    }

}
