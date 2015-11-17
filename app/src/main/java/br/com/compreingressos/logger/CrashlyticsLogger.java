package br.com.compreingressos.logger;

import com.crashlytics.android.Crashlytics;

import br.com.compreingressos.BuildConfig;

/**
 * Created by luiszacheu on 30/10/15.
 */
public class CrashlyticsLogger {

    public CrashlyticsLogger() {
        super();
    }

    public static void logException(Throwable throwable){
        if (BuildConfig.ENABLE_CRASHLYTICS){
            Crashlytics.logException(throwable);
        }
    }

    public static void log(String msg){
        if (BuildConfig.ENABLE_CRASHLYTICS){
            Crashlytics.log(msg);
        }
    }

    public static void log(int priority, String tag, String msg){
        if (BuildConfig.ENABLE_CRASHLYTICS){
            Crashlytics.log(priority,tag, msg);
        }
    }
}
