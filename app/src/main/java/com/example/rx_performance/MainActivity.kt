package com.example.rx_performance

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.mylibrary.Text

class MainActivity : AppCompatActivity() {
    lateinit var handler: Handler

    var i: Int = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initPermission()

        initView()

        initData()

        Runnable { }

        Text.get()
    }

    private fun initData() {
        Fps1().startMonitor { Log.d("mmmfps", it.toString() + "fps") }
    }

    private fun initView() {
        var text = findViewById<TextView>(R.id.text_click)
        text.setOnClickListener({ v -> Thread.sleep(3000) })
        handler = Handler()

        handler.post(object : Runnable {
            override fun run() {
                text.setText("ww" + i++)

                handler.postDelayed(this, 1000)
            }
        })
    }


    fun initPermission() {
        //检查权限
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED
        ) {
            Log.d("mmm", "还没有此权限");
            //是否第一次被用户拒绝
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            ) {
                Log.d("mmm", "向用户解释该权限");
            } else {
                //请求权限
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    ),
                    1
                )
            }

        } else {
            Log.d("mmm", "已经有了此权限");
        }
    }

}