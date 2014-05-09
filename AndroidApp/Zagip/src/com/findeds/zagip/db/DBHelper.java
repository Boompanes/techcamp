package com.findeds.zagip.db;

import android.content.Context;
import com.turbomanage.storm.DatabaseHelper;
import com.turbomanage.storm.api.Database;
import com.turbomanage.storm.api.DatabaseFactory;

/**
 * Extends {@link com.turbomanage.storm.SQLiteDao}, mainly supplies the necessary type parameter to the base class.
 *
 * @author Emmanuel Delos Santos
 * @since 3/16/14.
 */
@Database(name = DBHelper.DB_NAME, version = DBHelper.DB_VERSION)
public class DBHelper extends DatabaseHelper {
    public DBHelper(Context context, DatabaseFactory databaseFactory) {
        super(context, databaseFactory);
    }

    @Override
    public UpgradeStrategy getUpgradeStrategy() {
        return UpgradeStrategy.DROP_CREATE;
    }

    public static final String DB_NAME = "DB5";
    public static final int DB_VERSION = 1;
}