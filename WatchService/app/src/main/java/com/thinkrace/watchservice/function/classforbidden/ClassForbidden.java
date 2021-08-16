package com.thinkrace.watchservice.function.classforbidden;

import java.util.List;

public class ClassForbidden {
    // 名称
    public String name;
    // 时间
    public String[] amStart;
    public String[] amStop;
    public String[] pmStart;
    public String[] pmStop;
    public String[] nightStart;
    public String[] nightStop;

    public List<ForbiddenTime> forbiddenTimes;

    // 重复
    public String repeat;

}
