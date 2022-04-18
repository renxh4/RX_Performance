package com.example.rx_performance;

import android.os.Handler;
import android.os.Looper;
import android.view.Choreographer;

import java.util.ArrayList;


public class Fps1 {

    private boolean isFpsOpen;
    private ArrayList<Integer> listeners;
    private int count;
    private Handler handler;
    private long FPS_INTERVAL_TIME = 1000L;
    private FpsCallback mfpsCallback;
    private MyCallback fpsRunnable;


    public Fps1() {
        handler = new Handler(Looper.getMainLooper());
    }


    public void startMonitor(FpsCallback fpsCallback) {
        // 防止重复开启
        if (!isFpsOpen) {
            isFpsOpen = true;
            this.mfpsCallback = fpsCallback;
            fpsRunnable = new MyCallback();
            handler.postDelayed(fpsRunnable, FPS_INTERVAL_TIME);
            Choreographer.getInstance().postFrameCallback(fpsRunnable);
        }
    }

    public void stopMonitor() {
        count = 0;
        handler.removeCallbacks(fpsRunnable);
        Choreographer.getInstance().removeFrameCallback(fpsRunnable);
        isFpsOpen = false;
    }


    class MyCallback implements Choreographer.FrameCallback, Runnable {

        @Override
        public void doFrame(long frameTimeNanos) {
            count++;
            Choreographer.getInstance().postFrameCallback(this);
        }

        @Override
        public void run() {
            mfpsCallback.fps(count);
            count = 0;
            handler.postDelayed(this, FPS_INTERVAL_TIME);
        }
    }

    interface FpsCallback {
        void fps(int fps);
    }
}
