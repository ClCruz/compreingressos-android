package br.com.compreingressos.utils;

import android.content.Context;

import br.com.compreingressos.helper.DatabaseHelper;

/**
 * Created by luiszacheu on 14/04/15.
 */
public class DatabaseManager {

    private static DatabaseManager instance;
    private DatabaseHelper helper;

    public static void init(Context ctx) {
        if (null == instance) {
            instance = new DatabaseManager(ctx);
        }
    }

    public static DatabaseManager getInstance() {
        return instance;
    }

    private DatabaseManager(Context ctx) {
        helper = new DatabaseHelper(ctx);
    }

    private DatabaseHelper getHelper() {
        return helper;
    }


}
