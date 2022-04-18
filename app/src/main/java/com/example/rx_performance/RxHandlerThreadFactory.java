package com.example.rx_performance;

import android.os.Handler;
import android.os.HandlerThread;


public class RxHandlerThreadFactory {
    private static RxHandlerThreadFactory.HandlerThreadWrapper sLoopThread = new RxHandlerThreadFactory.HandlerThreadWrapper("loop");
    private static RxHandlerThreadFactory.HandlerThreadWrapper sWriteLogThread = new RxHandlerThreadFactory.HandlerThreadWrapper("writer");

    private RxHandlerThreadFactory() {
        throw new InstantiationError("Must not instantiate this class");
    }

    public static Handler getTimerThreadHandler() {
        return sLoopThread.getHandler();
    }

    public static Handler getWriteLogThreadHandler() {
        return sWriteLogThread.getHandler();
    }

    private static class HandlerThreadWrapper {
        private Handler handler = null;

        public HandlerThreadWrapper(String threadName) {
            HandlerThread handlerThread = new HandlerThread("BlockCanary-" + threadName);
            handlerThread.start();
            handler = new Handler(handlerThread.getLooper());
        }

        public Handler getHandler() {
            return handler;
        }
    }
}

