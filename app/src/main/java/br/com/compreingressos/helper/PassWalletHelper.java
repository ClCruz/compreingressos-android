package br.com.compreingressos.helper;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import br.com.compreingressos.logger.CrashlyticsLogger;

/**
 * Created by luiszacheu on 27/04/15.
 */
public class PassWalletHelper {

    public static boolean launchPassWallet(Context context, Uri uri, boolean launchGooglePlay){

        if (context != null){
            PackageManager packageManager = context.getPackageManager();

            final String stringPackageName = "com.attidomobile.passwallet";

            Intent intent = new Intent();
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setAction(Intent.ACTION_VIEW);

            Intent passWalletLaunchIntent = packageManager.getLaunchIntentForPackage(stringPackageName);
            if (passWalletLaunchIntent == null){

                if (launchGooglePlay){
                    String strReferrer = "";

                    try {
                        final String strEncodedURL = URLEncoder.encode(uri.toString(), "UTF-8");
                        strReferrer = "&referrer=" + strEncodedURL;

                    }catch (UnsupportedEncodingException e){
                        CrashlyticsLogger.logException(e);
                        CrashlyticsLogger.log(Log.ERROR, PassWalletHelper.class.getSimpleName(), "url encode -> " + uri.toString());
                        e.printStackTrace();
                        strReferrer = "";
                    }

                    try {
                        intent.setData(Uri.parse("market://details?id=" + stringPackageName + strReferrer));
                        context.startActivity(intent);
                    }catch (ActivityNotFoundException e){
                        CrashlyticsLogger.logException(e);
                        intent.setData(Uri.parse("http://play.google.com/store/apps/details?id=" + stringPackageName + strReferrer));
                        context.startActivity(intent);
                    }
                }
            }else{
                final String strClassName = "com.attidomobile.passwallet.activity.TicketDetailActivity";

                intent.setClassName(stringPackageName, strClassName);
                intent.addCategory(Intent.CATEGORY_BROWSABLE);
                intent.setDataAndType(uri, "application/vnd.apple.pkpass");
                context.startActivity(intent);

                return true;
            }
        }
        return false;
    }
}
