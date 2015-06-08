package br.com.compreingressos.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

/**
 * Created by luiszacheu on 19/03/15.
 */
public class Dialogs {
    public static void showDialogLocation(Context ctx1, Activity atc, String message, String title, String btnPositive, String btnNegative, final Intent intent) {
        final Context ctx = ctx1;
        final Activity activity = atc;
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder.setCancelable(true);
        builder.setMessage(message);
        builder.setTitle(title);
        builder.setPositiveButton(btnPositive, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                AndroidUtils.enableLocationSettings(ctx);

                return;
            }
        });
        builder.setNegativeButton(btnNegative, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                activity.startActivity(intent);
                return;
            }
        });
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            public void onCancel(DialogInterface dialog) {
                activity.startActivity(intent);
                return;
            }
        });

        builder.show();
    }
}
