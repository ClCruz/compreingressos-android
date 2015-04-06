package br.com.compreingressos;

import android.app.Application;

/**
 * Created by luiszacheu on 19/03/15.
 */
public class CompreIngressosAplication extends Application{

    private static CompreIngressosAplication instance;
    public boolean isDisplayDialogLocation = true;

    public static CompreIngressosAplication getInstance() {

        if (instance == null){
            instance = new CompreIngressosAplication();
        }

        return instance;

    }

    @Override
    public void onCreate() {
        super.onCreate();

    }

    public boolean isDisplayDialogLocation() {
        return isDisplayDialogLocation;
    }

    public void setDisplayDialogLocation(boolean isDisplayDialogLocation) {
        this.isDisplayDialogLocation = isDisplayDialogLocation;
    }
}
