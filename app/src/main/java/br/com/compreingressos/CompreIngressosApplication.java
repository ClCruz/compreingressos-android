package br.com.compreingressos;

import android.app.Application;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
//import com.parse.Parse;

//import br.com.compreingressos.helper.ParseHelper;
import io.fabric.sdk.android.Fabric;

/**
 * Created by luiszacheu on 19/03/15.
 */
public class CompreIngressosApplication extends Application{

    //Google Analytics
    private static final String PROPERTY_ID = "UA-16656615-2";
    private Tracker mTracker;

    //Credenciais para o Parse
    public static final String YOUR_APP_ID = "55QlR3PGrXE0YWWnld97UG7kksTlI6j8ioa0FUIN";
    public static final String YOUR_CLIENT_KEY = "PuVqOzx836qG4Ihv9rcy8kZNtsrU6yxTZJmfe4Uo";
    public static final Boolean RUNMODE_DEVELOPMENT = false;

    private static CompreIngressosApplication instance;

    public static CompreIngressosApplication getInstance() {

        if (instance == null){
            instance = new CompreIngressosApplication();
        }

        return instance;

    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (BuildConfig.ENABLE_CRASHLYTICS){
            Fabric.with(this, new Crashlytics());
        }
//        Parse.initialize(this, YOUR_APP_ID, YOUR_CLIENT_KEY);
//
//        if (!ParseHelper.getIsClient(this)){
//            ParseHelper.setSubscribeParseChannel("prospect");
//        }
    }

    public static boolean isRunnigOnEnvironmentDevelopment(){
        return RUNMODE_DEVELOPMENT;
    }

    synchronized public Tracker getTracker() {
        if (mTracker == null) {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
//            analytics.getLogger().setLogLevel(Logger.LogLevel.VERBOSE);
            // To enable debug logging use: adb shell setprop log.tag.GAv4 DEBUG
            mTracker = analytics.newTracker(R.xml.global_tracker);
        }
        return mTracker;
    }



}
