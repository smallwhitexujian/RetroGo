package com.future.retronet


/**
 * @JvmField 消除了变量的getter与setter方法
 * @JvmField 修饰的变量不能是private属性的
 * @JvmStatic 只能在object类或者伴生对象companion object中使用，而@JvmField没有这些限制
 * @JvmStatic 一般用于修饰方法，使方法变成真正的静态方法；如果修饰变量不会消除变量的getter与setter方法，但会使getter与setter方法和变量都变成静态
 */
object RetroCode {
    /**
     * 请求成功
     */
    @JvmField
    var CODE_SUCCESS = 0x000

    /**
     * 解析错误
     */
    @JvmField
    var CODE_PARSE_ERR = 0x001

    /**
     * 请求取消
     */
    @JvmField
    var CODE_REQUEST_CANCEL = 0x002

    /**
     * 下载失败
     */
    @JvmField
    var CODE_DOWNLOAD_ERR = 0x003

    @JvmField
    var CODE_ERR = -1

    @JvmField
    var CODE_ERR_IO = -2

    @JvmField
    var CODE_ERR_UNKOWN = -3
}