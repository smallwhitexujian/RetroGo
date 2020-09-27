package com.future.retronet

import com.future.retronet.logger.LogUtil
import io.reactivex.rxjava3.functions.Consumer

class RetroErrorHandler : Consumer<Throwable> {
    @Throws(Exception::class)
    override fun accept(e: Throwable) {
        LogUtil.e("Rx ErrorHandler intercept err :" + e.message)
        e.printStackTrace()
    }
}