package com.thinkrace.watchservice.model.location;


import com.thinkrace.watchservice.model.FlagFormat;
import com.thinkrace.watchservice.orderlibrary.utils.LogUtils;

/**
 * @author mare
 * @Description:TODO 终端状态解析类(前面状态，后面是报警动作)
 * @csdnblog http://blog.csdn.net/mare_blue
 * @date 2017/9/4
 * @time 16:03
 */
public class TerminalState {

    public static final int BIT_COUNT = 8;//八位
    private int batteryLow;
    private int outFence;//出围栏状态
    private int inFence;
    private int takenOff;//手环戴上取下状态

    private int still;//手表运行静止状态

    private int alSos;//SOS 报警
    private int alBatteryLow;//低电报警
    private int alFenceOut;//出围栏报警
    private int alFenceIn;//进围栏报警

    private int alTakenOff;//手环拆除报警
    private int alFall;//跌倒报警

    public void setBatteryLow(int batteryLow) {
        this.batteryLow = batteryLow << 0;
    }

    public void setBatteryLow(boolean flag) {
        int curFlag = FlagFormat.boolean2Int(flag);
        setBatteryLow(curFlag);
    }

    public void setOutFence(int outFence) {
        this.outFence = outFence << 1;
    }

    public void setOutFence(boolean flag) {
        int curFlag = FlagFormat.boolean2Int(flag);
        setOutFence(curFlag);
    }

    public void setInFence(int inFence) {
        this.inFence = inFence << 2;
    }

    public void setInFence(boolean flag) {
        int curFlag = FlagFormat.boolean2Int(flag);
        setInFence(curFlag);
    }

    public void setTakenOff(int takenOff) {
        this.takenOff = takenOff << 3;
    }

    public void setTakenOff(boolean flag) {
        int curFlag = FlagFormat.boolean2Int(flag);
        setTakenOff(curFlag);
    }

    //==============================
    public void setStill(int still) {
        this.still = still << 0;
    }

    public void setStill(boolean flag) {
        int curFlag = FlagFormat.boolean2Int(flag);
        setStill(curFlag);
    }

    //==============================
    public void setAlSOS(int switchSOS) {
        this.alSos = alSos << 0;
    }

    public void setAlSOS(boolean flag) {
        int curFlag = FlagFormat.boolean2Int(flag);
        setAlSOS(curFlag);
    }

    public void setAlLow(int alLow) {
        this.alBatteryLow = alLow << 1;
    }

    public void setAlLow(boolean flag) {
        int curFlag = FlagFormat.boolean2Int(flag);
        setAlLow(curFlag);
    }

    public void setAlFenceOut(int alFenceOut) {
        this.alFenceOut = alFenceOut << 2;
    }

    public void setAlFenceOut(boolean flag) {
        int curFlag = FlagFormat.boolean2Int(flag);
        setAlFenceOut(curFlag);
    }

    public void setAlFenceIn(int alFenceIn) {
        this.alFenceIn = alFenceIn << 3;
    }

    public void setAlFenceIn(boolean flag) {
        int curFlag = FlagFormat.boolean2Int(flag);
        setAlFenceIn(curFlag);
    }

    //==============================
    public void setAlTakenOff(int alTakenOff) {
        this.alTakenOff = alTakenOff << 0;
    }

    public void setAlTakenOff(boolean flag) {
        int curFlag = FlagFormat.boolean2Int(flag);
        setAlTakenOff(curFlag);
    }

    public void setAlFall(int alFall) {
        this.alFall = alFall << 1;
    }

    public void setAlFall(boolean flag) {
        int curFlag = FlagFormat.boolean2Int(flag);
        setAlFall(curFlag);
    }

    public String format() {
        String[] bytes = new String[BIT_COUNT];
        int tmp = 0;
        for (int i = 0; i < BIT_COUNT; i++) {
            switch (i) {
                case 0:
                    tmp = batteryLow | outFence | inFence | takenOff;
                    break;
                case 1:
                    tmp = still;
                    break;
                case 4:
                    tmp = alSos | alBatteryLow | alFenceOut | alFenceIn;
                    break;
                case 5:
                    tmp = alTakenOff | alFall;
                    break;
                default:
                    tmp = 0;
                    break;
            }
            bytes[i] = Integer.toHexString(tmp);
            LogUtils.e("bytes[i] " + bytes[i]);
        }
        String formatStr = contactArray(bytes);
        LogUtils.e("terminalState formatStr " + formatStr);
        return formatStr;
    }

    private String contactArray(String[] segemnt) {
        if (null == segemnt) {
            return String.valueOf(0);
        }
        int len = segemnt.length;
        StringBuffer sb = new StringBuffer();
        for (int i = len - 1; i >= 0; i--) {
            sb.append(segemnt[i]);
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        return "TerminalState{" +
                "batteryLow=" + batteryLow +
                ", outFence=" + outFence +
                ", inFence=" + inFence +
                ", takenOff=" + takenOff +
                ", still=" + still +
                ", alSos=" + alSos +
                ", alBatteryLow=" + alBatteryLow +
                ", alFenceOut=" + alFenceOut +
                ", alFenceIn=" + alFenceIn +
                ", alTakenOff=" + alTakenOff +
                ", alFall=" + alFall +
                '}';
    }
}
