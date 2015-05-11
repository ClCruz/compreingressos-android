package br.com.compreingressos;

import android.app.Application;

import com.crashlytics.android.Crashlytics;
import com.parse.Parse;

import br.com.compreingressos.helper.ParseHelper;
import io.fabric.sdk.android.Fabric;

/**
 * Created by luiszacheu on 19/03/15.
 */
public class CompreIngressosApplication extends Application{

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
//        Fabric.with(this, new Crashlytics());

        Parse.initialize(this, YOUR_APP_ID, YOUR_CLIENT_KEY);
        ParseHelper.setParseChannel(ParseHelper.getParseChannel());
    }

    public boolean isDisplayDialogLocation() {
        return isDisplayDialogLocation;
    }

    public void setDisplayDialogLocation(boolean isDisplayDialogLocation) {
        this.isDisplayDialogLocation = isDisplayDialogLocation;
    }


}
