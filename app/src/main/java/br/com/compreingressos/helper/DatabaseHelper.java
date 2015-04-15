package br.com.compreingressos.helper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.io.File;
import java.io.InputStream;
import java.sql.SQLException;

import br.com.compreingressos.model.Espetaculo;
import br.com.compreingressos.model.Ingresso;
import br.com.compreingressos.model.Order;

/**
 * Created by luiszacheu on 14/04/15.
 */
public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

    private static final String LOG_TAG = "DatabaseHelper";

    //  Nome do banco de dados do app
    private static final String DATABASE_NAME = "compreingressos.sqlite";

    // Versão do banco de dados que deve ser incrementada toda vez que houver alguma alteração na estrutura
    private static final int DATABASE_VERSION = 1;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

    }

    public DatabaseHelper(Context context, String databaseName, SQLiteDatabase.CursorFactory factory, int databaseVersion) {
        super(context, databaseName, factory, databaseVersion);
    }

    public DatabaseHelper(Context context, String databaseName, SQLiteDatabase.CursorFactory factory, int databaseVersion, int configFileId) {
        super(context, databaseName, factory, databaseVersion, configFileId);
    }

    public DatabaseHelper(Context context, String databaseName, SQLiteDatabase.CursorFactory factory, int databaseVersion, File configFile) {
        super(context, databaseName, factory, databaseVersion, configFile);
    }

    public DatabaseHelper(Context context, String databaseName, SQLiteDatabase.CursorFactory factory, int databaseVersion, InputStream stream) {
        super(context, databaseName, factory, databaseVersion, stream);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource) {
        try {
            TableUtils.createTable(connectionSource, Order.class);
            TableUtils.createTable(connectionSource, Espetaculo.class);
            TableUtils.createTable(connectionSource, Ingresso.class);
        } catch (SQLException e) {
            Log.e(LOG_TAG, "Não foi possivel criar a tabela");
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        try {
            TableUtils.dropTable(connectionSource, Order.class, true);
            TableUtils.dropTable(connectionSource, Espetaculo.class, true);
            TableUtils.dropTable(connectionSource, Ingresso.class, true);

            onCreate(sqLiteDatabase, connectionSource);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void close() {
        super.close();
    }
}
