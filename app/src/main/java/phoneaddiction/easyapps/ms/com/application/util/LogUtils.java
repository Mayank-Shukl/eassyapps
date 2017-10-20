package phoneaddiction.easyapps.ms.com.application.util;

import android.util.Log;

import com.crashlytics.android.Crashlytics;

import phoneaddiction.easyapps.ms.com.application.BuildConfig;

/**
 * Created by MMT5762 on 02-07-2017.
 */

public class LogUtils {

    private static final boolean IS_DEBUG = BuildConfig.DEBUG;

    public static void error(String tag ,String msg , Throwable e){
        if(IS_DEBUG){
            Log.e(tag,msg,e);
        }
        Crashlytics.logException(e);
    }

    public static void info(String tag , String info){
        if(IS_DEBUG){
            Log.i(tag,info);
        }
    }

    public static void warning(String tag , String warn){
        if(IS_DEBUG){
            Log.w(tag,warn);
        }
    }

}
