package com.future.retronet

import java.io.Serializable

class RetroBean<T> : Serializable {
    val code: Int = 0
    val message: String? = null
    val extra: String? = null
    val data: T? = null

    val isSuccess: Boolean
        get() = RetroCode.CODE_SUCCESS == code
}