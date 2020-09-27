/*
 * Copyright (c) 2016  athou（cai353974361@163.com）.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.future.retronet.logger;

import android.text.TextUtils;

import timber.log.Timber;


/**
 * Log统一管理类
 *
 * @author athou
 */
public class LogUtil {
    //以下为打印级别，级别从低到高
    public static final int LOG_LEVEL_VERBOSE = 1;
    public static final int LOG_LEVEL_DEBUG = 2;
    public static final int LOG_LEVEL_INFO = 3;
    public static final int LOG_LEVEL_WARN = 4;
    public static final int LOG_LEVEL_ERROR = 5;
    public static final int LOG_LEVEL_NOLOG = 6;

    private static String AppName = "";
    private static boolean PrintLine = true;
    private static int LogLevel = LOG_LEVEL_VERBOSE;
    private static boolean isDebug = true;


    /**
     * 可在打印的TAG前添加应用名标识，不设置则不输出
     */
    public static void setAppName(String appName) {
        AppName = appName;
    }

    /**
     * 是否输出打印所在的行数，默认不输出
     */
    public static void setPrintLine(boolean enable) {
        PrintLine = enable;
    }

    /**
     * 设置打印级别，且只有等于或高于该级别的打印才会输出
     */
    public static void setLogLevel(int logLevel) {
        LogLevel = logLevel;
    }

    /**
     * 是否打印日志输出
     *
     * @param enable true 表示输出日志，false表示不输出日志
     */
    public static void setIsDebug(boolean enable) {
        isDebug = enable;
    }

    public static void v() {
        log(LOG_LEVEL_VERBOSE, "");
    }

    public static void d() {
        log(LOG_LEVEL_DEBUG, "");
    }

    public static void i() {
        log(LOG_LEVEL_INFO, "");
    }

    public static void w() {
        log(LOG_LEVEL_WARN, "");
    }

    public static void e() {
        log(LOG_LEVEL_ERROR, "");
    }

    public static void v(String msg) {
        if (LogLevel <= LOG_LEVEL_VERBOSE) {
            log(LOG_LEVEL_VERBOSE, msg);
        }
    }

    public static void d(String msg) {
        if (LogLevel <= LOG_LEVEL_DEBUG) {
            log(LOG_LEVEL_DEBUG, msg);
        }
    }

    public static void i(String msg) {
        if (LogLevel <= LOG_LEVEL_INFO) {
            log(LOG_LEVEL_INFO, msg);
        }
    }

    public static void w(String msg) {
        if (LogLevel <= LOG_LEVEL_WARN) {
            log(LOG_LEVEL_WARN, msg);
        }
    }

    public static void e(String msg) {
        if (LogLevel <= LOG_LEVEL_ERROR) {
            log(LOG_LEVEL_ERROR, msg);
        }
    }

    private static void log(int logLevel, String msg) {
        if (!isDebug) {
            return;
        }
        StackTraceElement caller = Thread.currentThread().getStackTrace()[4];
        String callerClazzName = caller.getClassName();
        if (callerClazzName.contains(".")) {
            callerClazzName = callerClazzName.substring(callerClazzName.lastIndexOf(".") + 1);
        }
        if (callerClazzName.contains("$")) {
            callerClazzName = callerClazzName.substring(0, callerClazzName.indexOf("$"));
        }
        String tag = callerClazzName;
        if (!TextUtils.isEmpty(AppName)) {
            tag = AppName + "_" + tag;
        }
        if (PrintLine) {
            tag += "(Line:%d)";
            tag = String.format(tag, caller.getLineNumber());
        }
        tag = String.format(tag, callerClazzName);
        String message = "---" + caller.getMethodName() + "---" + msg;
        switch (logLevel) {
            case LOG_LEVEL_VERBOSE:
                Timber.tag(tag).v(message);
                break;
            case LOG_LEVEL_DEBUG:
                Timber.tag(tag).d(message);
                break;
            case LOG_LEVEL_INFO:
                Timber.tag(tag).i(message);
                break;
            case LOG_LEVEL_WARN:
                Timber.tag(tag).w(message);
                break;
            case LOG_LEVEL_ERROR:
                Timber.tag(tag).e(message);
                break;
            case LOG_LEVEL_NOLOG:
                break;
        }
    }

}
