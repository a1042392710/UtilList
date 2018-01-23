package com.demo.chenhao.android.com.utillist.util;


import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


/**
 * @Features: UncaughtException处理类, 当程序发生Uncaught异常的时候, 有该类来接管程序, 并记录发送错误报告
 * @author: create by chenhao on 2018/1/22
 */

/*  需要在 Appcation 类中 实例化
 * public class AndroidUtilsApplication extends Application {
 * public void onCreate() {
 * super.onCreate();
 * //崩溃处理
 * CrashHandlerUtil crashHandlerUtil = CrashHandlerUtil.getInstance();
 * crashHandlerUtil.init(this);
 * crashHandlerUtil.setCrashTip("很抱歉，程序出现异常，即将退出！");
 * }
 * }
 */


public class CrashHandlerUtil implements Thread.UncaughtExceptionHandler {

    public static final String TAG = "CrashHandlerUtil";

    //系统默认的UncaughtException处理类
    private Thread.UncaughtExceptionHandler mDefaultHandler;
    //CrashHandler实例
    private static CrashHandlerUtil INSTANCE = new CrashHandlerUtil();
    //程序的Context对象
    private Context mContext;
    //用来存储设备信息和异常信息
    private Map<String, String> infos = new HashMap<>();

    //用于格式化日期,作为日志文件名的一部分
    private DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss", Locale.CHINA);
    private String crashTip = "很抱歉，程序出现异常，即将退出！";
    private HashMap<String, String> map; //存储设备信息
    private StringBuffer sb;

    public String getCrashTip() {
        return crashTip;
    }

    public void setCrashTip(String crashTip) {
        this.crashTip = crashTip;
    }

    /**
     * 保证只有一个CrashHandler实例
     */
    private CrashHandlerUtil() {
    }

    /**
     * 获取CrashHandler实例 ,单例模式
     *
     * @return 单例
     */
    public static CrashHandlerUtil getInstance() {
        return INSTANCE;
    }

    /**
     * 初始化
     *
     * @param context 上下文
     */
    public void init(Context context) {
        mContext = context;
        //获取系统默认的UncaughtException处理器
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        //设置该CrashHandler为程序的默认处理器
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    /**
     * 当UncaughtException发生时会转入该函数来处理
     *
     * @param thread 线程
     * @param ex     异常
     */
    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        if (!handleException(ex) && mDefaultHandler != null) {
            //如果用户没有处理则让系统默认的异常处理器来处理
            mDefaultHandler.uncaughtException(thread, ex);
        } else {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                Log.e(TAG, "error : ", e);
                e.printStackTrace();
            }
            //TODO 退出程序 可以使用MyAppcation 中定义的方法
//            ((MyApplication) mContext).finallyAll();
        }
    }

    /**
     * 自定义错误处理,收集错误信息 发送错误报告等操作均在此完成.
     *
     * @param throwable 异常
     * @return true:如果处理了该异常信息;否则返回false.
     */
    private boolean handleException(final Throwable throwable) {
        if (throwable == null) {
            return false;
        }
        //使用Toast来显示异常信息
        new Thread() {
            @Override
            public void run() {
                Looper.prepare();
                throwable.printStackTrace();
                Toast.makeText(mContext, getCrashTip(), Toast.LENGTH_LONG).show();
                Looper.loop();
            }
        }.start();
        //收集设备参数信息
        collectDeviceInfo(mContext);
        //保存日志文件
        saveException(mContext, throwable);
        //TODO 可以在此处上传错误信息至服务器

        return true;
    }

    /**
     * 收集设备参数信息
     *
     * @param context 上下文
     */
    public HashMap<String, String> collectDeviceInfo(Context context) {

        map = new HashMap<String, String>();
        PackageManager mPackageManager = context.getPackageManager();
        PackageInfo mPackageInfo = null;
        try {
            mPackageInfo = mPackageManager.getPackageInfo(
                    context.getPackageName(), PackageManager.GET_ACTIVITIES);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        map.put("versionName", mPackageInfo.versionName);
        map.put("versionCode", "" + mPackageInfo.versionCode);
        map.put("SDK_INT", "" + Build.VERSION.SDK_INT);
        map.put("RELEASE", "" + Build.VERSION.RELEASE);
        map.put("CODENAME", "" + Build.VERSION.CODENAME);
        map.put("MODEL", "" + Build.MODEL);
        map.put("PRODUCT", "" + Build.PRODUCT);
        map.put("MANUFACTURER", "" + Build.MANUFACTURER);
        map.put("FINGERPRINT", "" + Build.FINGERPRINT);
        return map;


    }

    /**
     * 得到设备信息和Log信息，并存储到StringBuffer中
     *
     * @param context
     * @param ex
     * @return
     */
    private StringBuffer getLog(Context context, Throwable ex) {
        sb = new StringBuffer();
        for (Map.Entry<String, String> entry : collectDeviceInfo(context)
                .entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            // sb.append(key).append(" = ").append(value).append("\n");
        }
        sb.append(obtainExceptionInfo(ex));
        return sb;
    }

    /**
     * 获取系统未捕捉的错误信息
     */
    private String obtainExceptionInfo(Throwable throwable) {
        StringWriter mStringWriter = new StringWriter();
        PrintWriter mPrintWriter = new PrintWriter(mStringWriter);
        throwable.printStackTrace(mPrintWriter);
        mPrintWriter.close();
        return mStringWriter.toString();
    }

    /**
     * 保存log到文件中
     */
    private void saveException(Context context, Throwable ex) {
        StringBuffer sb = getLog(context, ex);
        String filename = FileUtil.createFileName("log", ".log");
        File logFile = new File(FileUtil.getExternalStoragePrivateLogPath(context), filename);
        FileUtil.saveFile(logFile.getPath(), sb.toString());
    }
}