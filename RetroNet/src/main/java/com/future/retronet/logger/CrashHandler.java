package com.future.retronet.logger;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.os.Process;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;


/**
 * 崩溃日志采集
 */
public class CrashHandler implements Thread.UncaughtExceptionHandler {
    private static CrashHandler INSTANCE;
    private Context mContext;
    private Thread.UncaughtExceptionHandler mDefaultExceptionHandler;

    @Override
    public void uncaughtException(Thread t, Throwable e) {//当发生exception时候会回调该方法
        dumpToSDCard(t, e);//dump trace 信息到sd卡
        //todo 上传服务器
        e.printStackTrace();
        if (mDefaultExceptionHandler != null) { //交给系统的UncaughtExceptionHandler处理
            mDefaultExceptionHandler.uncaughtException(t, e);
        } else {
            Process.killProcess(Process.myPid());
        }
    }

    private CrashHandler() {
    }

    public static CrashHandler getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new CrashHandler();
        }
        return INSTANCE;
    }

    public void init(Context context) {
        this.mContext = context;
        mDefaultExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();//获取当前默认ExceptionHandler，保存在全局对象
        Thread.setDefaultUncaughtExceptionHandler(this);//替换默认对象为当前对象
    }

    private void dumpToSDCard(final Thread t, final Throwable e) {
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            LogUtil.i("no sdcard skip dump ");
            return;
        }

        String mLodPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/crashHandler/";
        File file = new File(mLodPath);
        if (!file.exists()) {
            file.mkdirs();
        }

        @SuppressLint("SimpleDateFormat") String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(System.currentTimeMillis()));
        LogUtil.i(mLodPath + time + ".trace");
        File logFile = new File(mLodPath, time + ".trace");
        try {
            PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(logFile)));
            pw.println(time);//写入时间
            dumpPhoneInfo(pw);//写入头部版本说明
            pw.println();//输入
            e.printStackTrace(pw);//写入异常
            pw.close();

        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    private void dumpPhoneInfo(PrintWriter pw) {
        //应用的版本名称和版本号
        PackageManager pm = mContext.getPackageManager();
        PackageInfo pi = null;
        try {
            pi = pm.getPackageInfo(mContext.getPackageName(), PackageManager.GET_ACTIVITIES);
            if (pi != null) {
                pw.print("App Version: ");
                pw.print(pi.versionName);
                pw.print('_');
                pw.println(pi.versionCode);

                //android版本号
                pw.print("OS Version: ");
                pw.print(Build.VERSION.RELEASE);
                pw.print("_");
                pw.println(Build.VERSION.SDK_INT);

                //手机制造商
                pw.print("Vendor: ");
                pw.println(Build.MANUFACTURER);

                //手机型号
                pw.print("Model: ");
                pw.println(Build.MODEL);

                //cpu架构
                pw.print("CPU ABI: ");
                pw.println(Build.CPU_ABI);
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void zip(String src, String dest) throws IOException { //压缩文件夹，为上传做准备。节省流量。
        ZipOutputStream out = null;
        File outFile = new File(dest);
        File fileOrDirectory = new File(src);
        out = new ZipOutputStream(new FileOutputStream(outFile));
        if (fileOrDirectory.isFile()) {
            zipFileOrDirectory(out, fileOrDirectory, "");
        } else {
            File[] entries = fileOrDirectory.listFiles();
            for (int i = 0; i < entries.length; i++) {
                zipFileOrDirectory(out, entries[i], "");
            }
        }
        if (null != out) {
            out.close();
        }
    }

    private static void zipFileOrDirectory(ZipOutputStream out, File fileOrDirectory, String curPath) throws IOException {
        FileInputStream in = null;
        if (!fileOrDirectory.isDirectory()) {
            byte[] buffer = new byte[4096];
            int bytes_read;
            in = new FileInputStream(fileOrDirectory);
            ZipEntry entry = new ZipEntry(curPath + fileOrDirectory.getName());
            out.putNextEntry(entry);
            while ((bytes_read = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytes_read);
            }
            out.closeEntry();
        } else {
            File[] entries = fileOrDirectory.listFiles();
            for (int i = 0; i < entries.length; i++) {
                zipFileOrDirectory(out, entries[i], curPath + fileOrDirectory.getName() + "/");
            }
        }
        if (null != in) {
            in.close();
        }
    }
}