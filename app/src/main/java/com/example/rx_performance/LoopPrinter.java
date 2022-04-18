package com.example.rx_performance;

import android.os.Debug;
import android.os.SystemClock;
import android.util.Log;
import android.util.Printer;

import com.github.moduth.blockcanary.BlockCanaryInternals;


public class LoopPrinter implements Printer {
    private final RxBlock.RxBlockListener mRxBlockListener;
    private boolean mPrintingStarted = false;
    private long mStartTimestamp;
    private long mStartThreadTimestamp;
    private static final int DEFAULT_BLOCK_THRESHOLD_MILLIS = 3000;
    private long mBlockThresholdMillis = DEFAULT_BLOCK_THRESHOLD_MILLIS;

    public LoopPrinter(RxBlock.RxBlockListener rxBlockListener) {
        this.mRxBlockListener = rxBlockListener;

    }

    @Override
    public void println(String x) {
        if (!mPrintingStarted) {
            mStartTimestamp = System.currentTimeMillis();
            mStartThreadTimestamp = SystemClock.currentThreadTimeMillis();
            mPrintingStarted = true;
            startDump();
        } else {
            final long endTime = System.currentTimeMillis();
            mPrintingStarted = false;
            if (isBlock(endTime)) {
                notifyBlockEvent(endTime);
            }
            stopDump();
        }
    }

    private void notifyBlockEvent(long endTime) {
        final long startTime = mStartTimestamp;
        final long startThreadTime = mStartThreadTimestamp;
        final long endThreadTime = SystemClock.currentThreadTimeMillis();
        RxHandlerThreadFactory.getWriteLogThreadHandler().post(new Runnable() {
            @Override
            public void run() {
                mRxBlockListener.onBlockEvent(startTime, endTime, startThreadTime, endThreadTime);
            }
        });
    }

    private void stopDump() {
        if (RxBlock.getInstance().mRxStackSampler != null) {
            RxBlock.getInstance().mRxStackSampler.stop();
        }
    }

    private boolean isBlock(long endTime) {
        return endTime - mStartTimestamp > mBlockThresholdMillis;
    }


    private void startDump() {
        if (RxBlock.getInstance().mRxStackSampler != null) {
            RxBlock.getInstance().mRxStackSampler.start();
        }
    }
}
