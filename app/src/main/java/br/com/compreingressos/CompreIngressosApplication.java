package br.com.compreingressos;

import android.app.Application;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.parse.Parse;

import java.util.HashMap;

import br.com.compreingressos.helper.ParseHelper;
import io.fabric.sdk.android.Fabric;

/**
 * Created by luiszacheu on 19/03/15.
 */
public class CompreIngressosApplication extends Application{

    //Google Analytics
    private static final String PROPERTY_ID = "UA-XXXXX-Y";

    public static int GENERAL_TRACKER = 0;

    public enum TrackerName {
        APP_TRACKER, // Tracker used only in this app.
        GLOBAL_TRACKER, // Tracker used by all the apps from a company. eg: roll-up tracking.
        ECOMMERCE_TRACKER, // Tracker used by all ecommerce transactions from a company.
    }

    HashMap<TrackerName, Tracker> mTrackers = new HashMap<TrackerName, Tracker>();

    //Credenciais para o Parse
    public static final String YOUR_APP_ID = "55QlR3PGrXE0YWWnld97UG7kksTlI6j8ioa0FUIN";
    public static final String YOUR_CLIENT_KEY = "PuVqOzx836qG4Ihv9rcy8kZNtsrU6yxTZJmfe4Uo";

    private static CompreIngressosApplication instance;
    public boolean isDisplayDialogLocation = true;

    public static CompreIngressosApplication getInstance() {

        if (instance == null){
            instance = new CompreIngressosApplication();
        }

        return instance;

    }

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());

        Parse.initialize(this, YOUR_APP_ID, YOUR_CLIENT_KEY);
        ParseHelper.setParseChannel(ParseHelper.getParseChannel());


    }

    public boolean isDisplayDialogLocation() {
        return isDisplayDialogLocation;
    }

    public void setDisplayDialogLocation(boolean isDisplayDialogLocation) {
        this.isDisplayDialogLocation = isDisplayDialogLocation;
    }

    synchronized Tracker getTracker(TrackerName trackerId) {
        if (!mTrackers.containsKey(trackerId)) {

            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            Tracker t = (trackerId == TrackerName.APP_TRACKER) ? analytics.newTracker(PROPERTY_ID)
                    : (trackerId == TrackerName.GLOBAL_TRACKER) ? analytics.newTracker(
                    R.xml.global_tracker)
                    : analytics.newTracker(R.xml.ecommerce_tracker);
            t.enableAdvertisingIdCollection(true);
            mTrackers.put(trackerId, t);
        }
        return mTrackers.get(trackerId);
    }


}
