package com.example.rx_performance;



import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class RxBlockInfo {

    public long timeCost;
    private long threadTimeCost;
    public static final SimpleDateFormat TIME_FORMATTER =
            new SimpleDateFormat("MM-dd HH:mm:ss.SSS", Locale.US);
    private String timeStart;
    private String timeEnd;
    private ArrayList<String> threadStackEntries;

    private StringBuilder timeSb = new StringBuilder();
    private StringBuilder stackSb = new StringBuilder();


    public static final String KEY_TIME_COST = "time";
    public static final String KEY_THREAD_TIME_COST = "thread-time";
    public static final String KEY_TIME_COST_START = "time-start";
    public static final String KEY_TIME_COST_END = "time-end";

    public static final String KV = " = ";

    public static final String KEY_STACK = "stack";


    public static final String SEPARATOR = "\r\n";

    public RxBlockInfo setMainThreadTimeCost(long realTimeStart, long realTimeEnd, long threadTimeStart, long threadTimeEnd) {
        timeCost = realTimeEnd - realTimeStart;
        threadTimeCost = threadTimeEnd - threadTimeStart;
        timeStart = TIME_FORMATTER.format(realTimeStart);
        timeEnd = TIME_FORMATTER.format(realTimeEnd);
        return this;
    }


    public RxBlockInfo setThreadStackEntries(ArrayList<String> threadStackEntries) {
        this.threadStackEntries = threadStackEntries;
        return this;
    }

    public RxBlockInfo flushString() {
        String separator = SEPARATOR;


        timeSb.append(KEY_TIME_COST).append(KV).append(timeCost).append(separator);
        timeSb.append(KEY_THREAD_TIME_COST).append(KV).append(threadTimeCost).append(separator);
        timeSb.append(KEY_TIME_COST_START).append(KV).append(timeStart).append(separator);
        timeSb.append(KEY_TIME_COST_END).append(KV).append(timeEnd).append(separator);

        if (threadStackEntries != null && !threadStackEntries.isEmpty()) {
            StringBuilder temp = new StringBuilder();
            for (String s : threadStackEntries) {
                temp.append(s);
                temp.append(separator);
            }
            stackSb.append(KEY_STACK).append(KV).append(temp.toString()).append(separator);
        }
        return this;
    }

    public String toString() {
        return String.valueOf(timeSb) + stackSb;
    }


}
