package com.example.rx_performance;

import android.os.Looper;
import android.util.Log;

import com.github.moduth.blockcanary.BlockCanaryInternals;
import com.github.moduth.blockcanary.LogWriter;
import com.github.moduth.blockcanary.internal.BlockInfo;

import java.util.ArrayList;
import java.util.Iterator;

public class RxBlock {

    private static final RxBlock sRxBlock = new RxBlock();
    public RxStackSampler mRxStackSampler;
    private LoopPrinter loopPrinter;
    private boolean mMonitorStarted;

    private RxBlock() {
    }

    public static RxBlock getInstance() {
        return sRxBlock;
    }

    public RxBlock init(Thread thread) {
        mRxStackSampler = new RxStackSampler(thread, 0);
        loopPrinter = new LoopPrinter(new RxBlockListener() {
            @Override
            public void onBlockEvent(long realTimeStart, long realTimeEnd, long threadTimeStart, long threadTimeEnd) {
                ArrayList<String> threadStackEntries = mRxStackSampler.getThreadStackEntries(realTimeStart, realTimeEnd);
                if (!threadStackEntries.isEmpty()) {
                    RxBlockInfo blockInfo = new RxBlockInfo().setMainThreadTimeCost(realTimeStart, realTimeEnd, threadTimeStart, threadTimeEnd).
                            setThreadStackEntries(threadStackEntries).flushString();
                    Log.i("mmm", "【UI阻塞了】耗时：" + blockInfo.timeCost + "\n" + blockInfo.toString());
                }
            }
        });

        return sRxBlock;
    }

    public void start() {
        if (!mMonitorStarted) {
            mMonitorStarted = true;
            Looper.getMainLooper().setMessageLogging(loopPrinter);
        }
    }


    public void stop() {
        if (mMonitorStarted) {
            mMonitorStarted = false;
            Looper.getMainLooper().setMessageLogging(null);
            mRxStackSampler.stop();
        }
    }


    public interface RxBlockListener {
        void onBlockEvent(long realStartTime,
                          long realTimeEnd,
                          long threadTimeStart,
                          long threadTimeEnd);
    }


}

