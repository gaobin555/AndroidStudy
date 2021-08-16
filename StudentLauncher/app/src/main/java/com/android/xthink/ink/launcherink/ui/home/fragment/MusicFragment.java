package com.android.xthink.ink.launcherink.ui.home.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.android.xthink.ink.launcherink.R;
import com.android.xthink.ink.launcherink.common.utils.MyLog;
import com.android.xthink.ink.launcherink.ui.home.music.Common;
import com.android.xthink.ink.launcherink.ui.home.music.Music;
import com.android.xthink.ink.launcherink.ui.home.music.MusicListActivity;
import com.android.xthink.ink.launcherink.ui.home.music.Playhelper;

import static com.android.xthink.ink.launcherink.base.mvp.MainBaseActivity.requestPermisson;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MusicFragment extends NativeFragment {
    private static final String TAG = "MusicFragment";
    private static final String TAG_LIFE = "FragmentLife";

    private Activity mLauncherActivity;
    private SeekBar mSeekBar;
    private static ImageButton mBtnPlay;
    private ImageButton mBtnPreSound;
    private ImageButton mBtnNextSound;
    private TextView mTvStart;
    private TextView mTvEnd;
    private TextView mTvName;
    private TextView mTvContent;
    private ImageView mBtnMenu;
    private ImageView mBtnOrder;

    public static MediaPlayer mediaPlayer;
    private int mPosition = 0;
    private boolean isStop = true;
    private boolean mOnThisPage = false;
    private int mButtonWitch = 0;

    private AudioManager mAudioManager;

    private static final int SEEKCHANGE = 0;
    private static final int FOCUSCHANGE = 1;

    // used to track what type of audio focus loss caused the playback to pause
    private boolean mPausedByTransientLossOfFocus = false;

    private MusicThread musicThread = null;

    private final int[] mPlayModeRes = {R.mipmap.btn_knowledge_playlist_cycle_blk, R.mipmap.btn_knowledge_single_cycle_blk, R.mipmap.btn_knowledge_random_blk};

    public static MusicFragment newInstance() {
        MyLog.d(TAG_LIFE, "newInstance" + "MusicFragment");
        return new MusicFragment();
    }
    @Override
    protected View initView(LayoutInflater inflater, @Nullable ViewGroup container) {
        super.initView(inflater, container);
        View fragmentContentView;

        fragmentContentView = inflater.inflate(R.layout.fragment_music, container, false);

        mBtnPlay = (ImageButton) fragmentContentView.findViewById(R.id.btn_play);
        mSeekBar = (SeekBar) fragmentContentView.findViewById(R.id.play_seek_bar);
        mBtnPreSound = (ImageButton) fragmentContentView.findViewById(R.id.btn_reverse);
        mBtnNextSound = (ImageButton) fragmentContentView.findViewById(R.id.btn_forward);
        mTvStart = (TextView) fragmentContentView.findViewById(R.id.tv_start);
        mTvEnd = (TextView) fragmentContentView.findViewById(R.id.tv_end);
        mTvName = (TextView) fragmentContentView.findViewById(R.id.tv_name);
        mTvContent = (TextView) fragmentContentView.findViewById(R.id.tv_content);
        mBtnMenu = (ImageView) fragmentContentView.findViewById(R.id.btn_menu);
        mBtnOrder = (ImageView) fragmentContentView.findViewById(R.id.btn_order);

        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser && mediaPlayer != null) {
                    mediaPlayer.seekTo(progress);
                }
            }
        });

        mSeekBar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (mediaPlayer != null && mediaPlayer.isPlaying())
                    return false;
                else
                    return true;
            }
        });

        mBtnPreSound.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Log.d(TAG, "上一曲");
                if (Common.musicList.size() <= 0) {
                    return;
                }
                if (AudioManager.AUDIOFOCUS_REQUEST_FAILED == mAudioManager.requestAudioFocus(
                        mAudioFocusListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN)) {
                    return;
                }
                mButtonWitch = 1;
                setBtnMode();
            }
        });

        mBtnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "播放/暂停");
                if (AudioManager.AUDIOFOCUS_REQUEST_FAILED == mAudioManager.requestAudioFocus(
                        mAudioFocusListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN)) {
                    return;
                }
                if (mediaPlayer == null) {
                    mediaPlayer = new MediaPlayer();
                }
                if (Common.musicList.size() > 0) {
                    if (mediaPlayer.isPlaying()) {
                        mediaPlayer.pause();
                        mBtnPlay.setImageResource(R.mipmap.btn_knowledge_play_blk);
                    } else {
                        if (isStop) {
                            prevAndnextplaying(Common.musicList.get(mPosition).path);
                        } else {
                            mediaPlayer.start();
                            mBtnPlay.setImageResource(R.mipmap.btn_knowledge_pause_blk);
                            if(musicThread == null) {
                                musicThread = new MusicThread();
                            }
                            new Thread(musicThread).start();
                        }
                    }
                }
            }
        });

        mBtnNextSound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "下一曲");
                if (Common.musicList.size() <= 0) {
                    return;
                }
                if (AudioManager.AUDIOFOCUS_REQUEST_FAILED == mAudioManager.requestAudioFocus(
                        mAudioFocusListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN)) {
                    return;
                }
                mButtonWitch = 2;
                setBtnMode();
            }
        });

        mBtnOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch(Common.playMode) {
                    case 0:
                        Common.playMode = 1;
                        mBtnOrder.setImageResource(mPlayModeRes[1]);
                        break;
                    case 1:
                        Common.playMode = 2;
                        mBtnOrder.setImageResource(mPlayModeRes[2]);
                        break;
                    case 2:
                    default:
                        Common.playMode = 0;
                        mBtnOrder.setImageResource(mPlayModeRes[0]);
                        break;
                }
                Log.d(TAG, "播放模式 = " + Common.playMode);
            }
        });

        mBtnMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "播放列表");
                MusicListActivity.start(mContext);
            }
        });

        return fragmentContentView;
    }

    @Override
    public void onResume() {
        super.onResume();
        // 读取音乐媒体
        if(requestPermisson(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE, 1)) {
            if (!Common.needSwitchMusic || Common.musicList.size() <= 0)
                Playhelper.addMusicList(getActivity());
        }
        MyLog.i(TAG, "MusicFragment onResume");
        if (Common.needSwitchMusic) {
            Common.needSwitchMusic = false;
            mOnThisPage = true;
            mPosition = Common.getPosition();
            prevAndnextplaying(Common.musicList.get(mPosition).path);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @android.annotation.NonNull String[] permissions, @android.annotation.NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1) {
            if (!Common.needSwitchMusic || Common.musicList.size() <= 0)
                Playhelper.addMusicList(getActivity());
        }
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void setListener() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        MyLog.i(TAG, "onCreateView");
        mAudioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
        mRootView = super.onCreateView(inflater, container, savedInstanceState);
        if (mRootView != null) {
            mRootView.getViewTreeObserver().addOnWindowFocusChangeListener(mOnWindowFocusChangeListener);
        }
        return mRootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        MyLog.i(TAG, "onDestroyView");

        if (mRootView != null) {
            mRootView.getViewTreeObserver().removeOnWindowFocusChangeListener(mOnWindowFocusChangeListener);
        }

        mAudioManager.abandonAudioFocus(mAudioFocusListener);
    }

    ViewTreeObserver.OnWindowFocusChangeListener mOnWindowFocusChangeListener = new ViewTreeObserver.OnWindowFocusChangeListener() {
        @Override
        public void onWindowFocusChanged(boolean hasFocus) {
            MyLog.i(TAG, "onWindowFocusChanged :" + hasFocus + ",fragment:" + this);
            mOnThisPage = hasFocus;
            if(mediaPlayer != null && mOnThisPage && mediaPlayer.isPlaying()) {
                if(musicThread == null) {
                    musicThread = new MusicThread();
                }
                new Thread(musicThread).start();
            }
        }
    };

    private AudioManager.OnAudioFocusChangeListener mAudioFocusListener = new AudioManager.OnAudioFocusChangeListener() {
        public void onAudioFocusChange(int focusChange) {
            mHandler.obtainMessage(FOCUSCHANGE, focusChange, 0).sendToTarget();
        }
    };

    private void prevAndnextplaying(String path) {
        if (Common.musicList.size() <= 0) {
            Log.d(TAG, "prevAndnextplaying no music");
            return;
        }
        Log.d(TAG, "prevAndnextplaying path = " + path);
        isStop = false;

        if(musicThread == null) {
            musicThread = new MusicThread();
        }
        new Thread(musicThread).start();

        if (mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
        }else{
            mediaPlayer.reset();
            mSeekBar.setProgress(0);
            mTvStart.setText("00:00");
        }
        for (Music m : Common.musicList) {
            m.isPlaying = false;
        }
        Common.setPosition(mPosition);
        mTvName.setText(Common.musicList.get(mPosition).title);
        mTvContent.setText(Common.musicList.get(mPosition).artist + "--" + Common.musicList.get(mPosition).album);
        mBtnPlay.setImageResource(R.mipmap.btn_knowledge_pause_blk);

        try {
            mediaPlayer.setDataSource(Common.musicList.get(mPosition).path);
            mediaPlayer.prepare();                   // 准备
            mediaPlayer.start();                        // 启动
            Common.musicList.get(mPosition).isPlaying = true;
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                @Override
                public void onCompletion(MediaPlayer mp) {
                    if(!mediaPlayer.isPlaying()){
                        setPlayMode();
                    }

                }
            });
        } catch (IllegalArgumentException | SecurityException | IllegalStateException
                | IOException e) {
            e.printStackTrace();
        }

        mTvEnd.setText(formatTime(Common.musicList.get(mPosition).length));
        mSeekBar.setMax(Common.musicList.get(mPosition).length);
    }

    private void setPlayMode() {
        if (Common.playMode == 0)//全部循环
        {
            if (mPosition == Common.musicList.size() - 1)//默认循环播放
            {
                mPosition = 0;// 第一首
                mediaPlayer.reset();
                prevAndnextplaying(Common.musicList.get(mPosition).path);

            } else {
                mPosition++;
                mediaPlayer.reset();
                prevAndnextplaying(Common.musicList.get(mPosition).path);
            }
        } else if (Common.playMode == 1)//单曲循环
        {
            //position不需要更改
            mediaPlayer.reset();
            prevAndnextplaying(Common.musicList.get(mPosition).path);
        } else if (Common.playMode == 2)//随机
        {
            mPosition = (int) (Math.random() * Common.musicList.size());//随机播放
            mediaPlayer.reset();
            prevAndnextplaying(Common.musicList.get(mPosition).path);
        }
    }

    private void setBtnMode() {
        mBtnPlay.setImageResource(R.mipmap.btn_knowledge_pause_blk);
        if (Common.playMode == 0)//全部循环
        {
            if (mPosition == Common.musicList.size() - 1)//默认循环播放
            {
                if (mButtonWitch == 1) {
                    mPosition--;
                    mediaPlayer.reset();
                    prevAndnextplaying(Common.musicList.get(mPosition).path);
                } else if (mButtonWitch == 2) {
                    mPosition = 0;// 第一首
                    mediaPlayer.reset();
                    prevAndnextplaying(Common.musicList.get(mPosition).path);
                }
            } else if (mPosition == 0) {
                if (mButtonWitch == 1) {
                    mPosition = Common.musicList.size() - 1;
                    mediaPlayer.reset();
                    prevAndnextplaying(Common.musicList.get(mPosition).path);
                } else if (mButtonWitch == 2) {
                    mPosition++;
                    mediaPlayer.reset();
                    prevAndnextplaying(Common.musicList.get(mPosition).path);
                }
            }else {
                if(mButtonWitch ==1){
                    mPosition--;
                    mediaPlayer.reset();
                    prevAndnextplaying(Common.musicList.get(mPosition).path);

                }else if(mButtonWitch ==2){
                    mPosition++;
                    mediaPlayer.reset();
                    prevAndnextplaying(Common.musicList.get(mPosition).path);
                }
            }
        } else if (Common.playMode == 1)//单曲循环
        {
            //position不需要更改
            mediaPlayer.reset();
            prevAndnextplaying(Common.musicList.get(mPosition).path);
        } else if (Common.playMode == 2)//随机
        {
            mPosition = (int) (Math.random() * Common.musicList.size());//随机播放
            mediaPlayer.reset();
            prevAndnextplaying(Common.musicList.get(mPosition).path);
        }
        Common.setPosition(mPosition);
    }

    //格式化数字
    private String formatTime(int length) {
        Date date = new Date(length);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("mm:ss");    //规定固定的格式
        String totaltime = simpleDateFormat.format(date);
        return totaltime;
    }

    private boolean needupdate() {
        return mOnThisPage && !isStop;
    }

    //Handler实现向主线程进行传值
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case FOCUSCHANGE:
                    switch (msg.arg1) {
                        case AudioManager.AUDIOFOCUS_LOSS:
                            MyLog.d(TAG, "AUDIOFOCUS_LOSS");
                            if (mediaPlayer.isPlaying()) {
                                mPausedByTransientLossOfFocus = false;
                                mediaPlayer.pause();
                                mBtnPlay.setImageResource(R.mipmap.btn_knowledge_play_blk);
                            }
                            break;
                        case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                            MyLog.d(TAG, "AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK");
                            break;
                        case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                            MyLog.d(TAG, "AUDIOFOCUS_LOSS_TRANSIENT");
                            if (mediaPlayer.isPlaying()) {
                                mPausedByTransientLossOfFocus = true;
                                mediaPlayer.pause();
                                mBtnPlay.setImageResource(R.mipmap.btn_knowledge_play_blk);
                            }
                            break;
                        case AudioManager.AUDIOFOCUS_GAIN:
                            MyLog.d(TAG, "AUDIOFOCUS_GAIN");
                            if (!mediaPlayer.isPlaying() && mPausedByTransientLossOfFocus) {
                                mPausedByTransientLossOfFocus = false;
                                mediaPlayer.start();
                                mBtnPlay.setImageResource(R.mipmap.btn_knowledge_pause_blk);
                            }
                            break;
                        default:
                            MyLog.d(TAG, "Unknown audio focus change code");
                            break;
                    }
                    break;

                case SEEKCHANGE:
                    mSeekBar.setProgress((int) (msg.arg1));
                    mTvStart.setText(formatTime(msg.arg1));
                    break;

                default:
                    break;
            }
        }
    };

    public static void musicPause() {
        if(mediaPlayer != null) {
            mediaPlayer.pause();
            mBtnPlay.setImageResource(R.mipmap.btn_knowledge_play_blk);
        }
    }

    public static void musicStart() {
        if(mediaPlayer != null) {
            mediaPlayer.start();
            mBtnPlay.setImageResource(R.mipmap.btn_knowledge_pause_blk);
        }
    }

    //创建一个类MusicThread实现Runnable接口，实现多线程
    class MusicThread implements Runnable {
        @Override
        public void run() {
            MyLog.d(TAG, "MusicThread run mOnThisPage = " + mOnThisPage);
            while (needupdate() && Common.musicList.get(mPosition) != null) {
                try {
                    //让线程睡眠1000毫秒
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                //放送给Handler现在的运行到的时间，进行ui更新
                mHandler.obtainMessage(SEEKCHANGE, mediaPlayer.getCurrentPosition(), 0).sendToTarget();
            }
        }
    }
}
