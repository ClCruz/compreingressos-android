package br.com.compreingressos;

import android.app.Application;

/**
 * Created by luiszacheu on 19/03/15.
 */
public class QMCompreIngressosAplication extends Application{

    private static QMCompreIngressosAplication instance;
    public boolean isDisplayDialogLocation = true;

    public static QMCompreIngressosAplication getInstance() {

        if (instance == null){
            instance = new QMCompreIngressosAplication();
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
