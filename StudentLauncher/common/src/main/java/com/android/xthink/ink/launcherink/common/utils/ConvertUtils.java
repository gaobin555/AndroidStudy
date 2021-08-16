package com.android.xthink.ink.launcherink.common.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import static com.android.xthink.ink.launcherink.common.utils.CommonUtils.getPrettyNumber;

/**
 * 数据类型转换、单位转换
 *
 * @author 李玉江[QQ:1023694760]
 * @since 2014-4-18
 */
public class ConvertUtils {

    public static String toString(InputStream is, String charset) {
        StringBuilder sb = new StringBuilder();
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, charset));
            while (true) {
                String line = reader.readLine();
                if (line == null) {
                    break;
                } else {
                    sb.append(line).append("\n");
                }
            }
            reader.close();
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    public static String toString(InputStream is) {
        return toString(is, "utf-8");
    }

    /**
     * 人民币 分转元
     *
     * @param cnp 分
     * @return 元
     */
    public static String cnPTY(int cnp) {
        if (cnp < 0) {
            return "";
        }

        return getPrettyNumber(cnp / 100.0) + "";
    }
}
