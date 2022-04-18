package com.example.rx_performance;


import com.github.moduth.blockcanary.internal.BlockInfo;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

public class RxStackSampler {

    //要检测的线程
    private final Thread mCurrentThread;
    //map最大数据
    private final int mMaxEntryCount;
    //map默认存储最大数据
    private static final int DEFAULT_MAX_ENTRY_COUNT = 100;
    //默认延迟时间
    private static final int DEFAULT_SAMPLE_INTERVAL = 300;
    //原子类
    protected AtomicBoolean mShouldSample = new AtomicBoolean(false);
    protected long mSampleInterval;

    private static final LinkedHashMap<Long, String> sStackMap = new LinkedHashMap<>();


    public RxStackSampler(Thread thread, long sampleIntervalMillis) {
        this(thread, DEFAULT_MAX_ENTRY_COUNT, sampleIntervalMillis);
    }

    public RxStackSampler(Thread thread, int maxEntryCount, long sampleIntervalMillis) {
        mCurrentThread = thread;
        mMaxEntryCount = maxEntryCount;
        if (0 == sampleIntervalMillis) {
            sampleIntervalMillis = DEFAULT_SAMPLE_INTERVAL;
        }
        mSampleInterval = sampleIntervalMillis;
    }


    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            doSample();

            if (mShouldSample.get()) {
                RxHandlerThreadFactory.getTimerThreadHandler()
                        .postDelayed(mRunnable, mSampleInterval);
            }
        }
    };

    public void start() {
        if (mShouldSample.get()) {
            return;
        }
        mShouldSample.set(true);

        RxHandlerThreadFactory.getTimerThreadHandler().removeCallbacks(mRunnable);
        RxHandlerThreadFactory.getTimerThreadHandler().postDelayed(mRunnable,
                (long) (500 * 0.8f));
    }

    public void stop() {
        if (!mShouldSample.get()) {
            return;
        }
        mShouldSample.set(false);
        RxHandlerThreadFactory.getTimerThreadHandler().removeCallbacks(mRunnable);
    }

    private void doSample() {
        StringBuilder stringBuilder = new StringBuilder();

        for (StackTraceElement stackTraceElement : mCurrentThread.getStackTrace()) {
            stringBuilder
                    .append(stackTraceElement.toString())
                    .append(BlockInfo.SEPARATOR);
        }

        synchronized (sStackMap) {
            if (sStackMap.size() == mMaxEntryCount && mMaxEntryCount > 0) {
                sStackMap.remove(sStackMap.keySet().iterator().next());
            }
            sStackMap.put(System.currentTimeMillis(), stringBuilder.toString());
        }
    }

    public ArrayList<String> getThreadStackEntries(long startTime, long endTime) {
        ArrayList<String> result = new ArrayList<>();
        synchronized (sStackMap) {
            for (Long entryTime : sStackMap.keySet()) {
                if (startTime < entryTime && entryTime < endTime) {
                    result.add(sStackMap.get(entryTime));
                }
            }
        }
        return result;
    }
}
