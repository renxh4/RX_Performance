package com.example.rx_performance

import android.app.Application
import android.os.Looper
import com.github.moduth.blockcanary.BlockCanary

class App : Application() {

    override fun onCreate() {
        super.onCreate()
//        BlockCanary.install(this, MyBlockCanaryContext()).start();//在主进程初始化调用

        RxBlock.getInstance().init(Looper.getMainLooper().getThread()).start()
    }

}