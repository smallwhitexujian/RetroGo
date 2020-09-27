package com.future.retronet.logger;

import android.os.Environment;
import android.util.Log;


import com.future.retronet.BuildConfig;

import java.io.File;

import timber.log.Timber;

public class TimberUtil {

    private final static String LOG_FILE_PATH = Environment.getExternalStorageDirectory().getPath() + File.separator + System.currentTimeMillis() + "log.test";

    /**
     * 设置log始终打开，可以在Application的onCreate中设置
     */
    public static void setLogDebug() {
        Timber.plant(new Timber.DebugTree());
    }

    /**
     * 设置log自动在debug打开，在release关闭，可以在Application的onCreate中设置
     */
    public static void setLogAuto() {
        if (BuildConfig.DEBUG) {//debug版本
            Timber.plant(new Timber.DebugTree());
        } else {//release版本
            Timber.plant(new TimberUtil.CrashReportingTree());//打印关，同时gradle中的release的debuggable要设置为false
        }
    }

    /**
     * 设置log自动，并且想在release时仅在测试时有打印，
     * 在release版本时增加判断磁盘目录下是否存在文件 log.test，
     * 测试时让测试人员在磁盘目录下建立这么个文件。
     * 注意，如果读取存储需要权限申请的话，需要先获得权限，才能调用
     */
    public static void setLogAutoEx() {
        if (BuildConfig.DEBUG) {//debug版本
            Timber.plant(new Timber.DebugTree());
        } else {//release版本
            File logFile = new File(LOG_FILE_PATH);
            if (logFile.exists()) {
                Timber.plant(new Timber.DebugTree());//打印开
            } else {
                Timber.plant(new TimberUtil.CrashReportingTree());//打印关，同时gradle中的release的debuggable要设置为false
            }
        }
    }


    private static class CrashReportingTree extends Timber.Tree {
        @Override
        protected void log(int priority, String tag, String message, Throwable t) {
            if (priority == Log.VERBOSE || priority == Log.DEBUG) {
                return;
            }
            //FakeCrashLibrary.log(priority, tag, message);
            if (t != null) {
                if (priority == Log.ERROR) {
                    //FakeCrashLibrary.logError(t);
                } else if (priority == Log.WARN) {
                    // FakeCrashLibrary.logWarning(t);
                } else {

                }
            }
        }
    }

}
