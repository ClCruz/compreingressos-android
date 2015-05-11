package br.com.compreingressos.helper;

import android.util.Log;

import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParsePush;
import com.parse.SaveCallback;

/**
 * Created by zaca on 5/11/15.
 */
public class ParseHelper {

    public ParseHelper() {
        // TODO Auto-generated constructor stub
    }

    public static void setParseChannel(final String parseChannel) {
        ParsePush.subscribeInBackground(parseChannel, new SaveCallback() {

            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Log.e("com.parse.push", "successfully subscribed to the broadcast channel." + parseChannel);
                } else {
                    Log.e("com.parse.push", "failed to subscribe for push", e);
                }

            }
        });

        ParseInstallation.getCurrentInstallation().saveInBackground();
    }

    public static String getParseChannel() {

        return "customer_"+ParseInstallation.getCurrentInstallation().getInstallationId();
    }

}

