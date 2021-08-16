package com.android.xthink.ink.launcherink.utils;

import android.content.Context;
import android.text.TextUtils;

import com.android.xthink.ink.launcherink.ui.home.bean.Student;
import com.android.xthink.ink.launcherink.ui.home.bean.Weathers;

public class Utility {

    /**
     *  将返回的数据解析成Weather 实体类
     */
    public static Weathers handleWeatherResponse(String response) {
        Weathers weathers = new Weathers();
        String[] content = response.split(",");
        weathers.city = Tool.unicodeNo0xuToString(content[content.length-1]);
        weathers.weather = Tool.unicodeNo0xuToString(content[2]);
        weathers.weaCode = content[3];;
        weathers.nowTemp = content[4];
        weathers.low = content[5];
        weathers.high = content[6];
        return weathers;
    }

    public static Student handleStudentResponse(Context context, String response) {
        Student student = new Student(context);
        String[] content = response.split(",");

        for (int i = 0; i < content.length; i++) {
            if (i == 0 && !TextUtils.isEmpty(content[i])) {
                student.s_birthday = content[i];
            }
            if (i == 1 && !TextUtils.isEmpty(content[i])) {
                student.s_name = Tool.unicodeNo0xuToString(content[i]);
            }
            if (i == 2 && !TextUtils.isEmpty(content[i])) {
                student.s_school = Tool.unicodeNo0xuToString(content[i]);
            }
            if (i == 3 && !TextUtils.isEmpty(content[i])) {
                student.s_class = Tool.unicodeNo0xuToString(content[i]);
            }
            if (i == 4 && !TextUtils.isEmpty(content[i])) {
                student.s_addr = Tool.unicodeNo0xuToString(content[i]);
            }
            if (i == 5 && !TextUtils.isEmpty(content[i])) {
                student.url_photo = content[i];
            }
        }

        return student;
    }
}
