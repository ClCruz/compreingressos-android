package br.com.compreingressos;

import android.app.Application;

import com.crashlytics.android.Crashlytics;

import io.fabric.sdk.android.Fabric;

/**
 * Created by luiszacheu on 19/03/15.
 */
public class CompreIngressosApplication extends Application{

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
//        Fabric.with(this, new Crashlytics());
    }

    public boolean isDisplayDialogLocation() {
        return isDisplayDialogLocation;
    }

    public void setDisplayDialogLocation(boolean isDisplayDialogLocation) {
        this.isDisplayDialogLocation = isDisplayDialogLocation;
    }


}
