package com.future.retrogo

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.view.View
import com.future.retronet.RetroNet
import com.future.retronet.logger.LogUtil
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : Activity() {
    @SuppressLint("CheckResult")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val service = RetroNet.create(TestService::class.java)
        findViewById<View>(R.id.btn).setOnClickListener {
            service.gettest()
                .subscribe({ carBeans -> // 处理数据 直接获取到List<JavaBean> carBeans
                    LogUtil.e("============>$carBeans")
                }) { throwable -> // 处理异常
                    LogUtil.e("============>$throwable")
                }
        }

        btn2.setOnClickListener {
            var call: Call<Any>? = service.gettest2()
            call?.enqueue(object :Callback<Any>{
                override fun onFailure(call: Call<Any>, t: Throwable) {
                    LogUtil.e("============>$t")
                }

                override fun onResponse(call: Call<Any>, response: Response<Any>) {
                    LogUtil.e("============>$response")
                }

            })
        }
    }
}