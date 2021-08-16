package com.android.xthink.ink.launcherink.ui.home.music;


import java.util.ArrayList;
import java.util.List;

/**
 * Created by shaoyangyang on 2017/12/20.
 */

public class Common {
    private static int position = 0;
    public static int playMode = 0;
    public static List<Music> musicList = new ArrayList<>();
    public static boolean needSwitchMusic = false;

    public static int getPosition() {
        if (musicList.size() <= 0) {
            position = -1;
        }

        if (musicList.size() < position) {
            position = 0;
        }
        return position;
    }

    public static void setPosition(int position) {
        Common.position = position;
    }
}
