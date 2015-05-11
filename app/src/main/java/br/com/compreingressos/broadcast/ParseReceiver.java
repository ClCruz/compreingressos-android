package br.com.compreingressos.broadcast;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.parse.ParsePushBroadcastReceiver;

import br.com.compreingressos.MainActivity;

/**
 * Created by zaca on 5/11/15.
 */
public class ParseReceiver extends ParsePushBroadcastReceiver {

    public ParseReceiver() {
        // TODO Auto-generated constructor stub
    }

    @Override
    public void onPushOpen(Context context, Intent intent) {
        Log.e("Push", "Clicked");
        Intent i = new Intent(context, MainActivity.class);
        i.putExtras(intent.getExtras());
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
    }

}
