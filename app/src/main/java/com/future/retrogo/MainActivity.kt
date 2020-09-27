package com.future.retrogo

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.view.View
import com.future.retronet.RetroNet.create
import com.future.retronet.logger.LogUtil

class MainActivity : Activity() {
    @SuppressLint("CheckResult")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val service = create(TestService::class.java)
        findViewById<View>(R.id.btn).setOnClickListener {
            service.gettest()
                .subscribe({ carBeans -> // 处理数据 直接获取到List<JavaBean> carBeans
                    LogUtil.e("============>$carBeans")
                }) { throwable -> // 处理异常
                    LogUtil.e("============>$throwable")
                }
        }
    }
}