package com.findeds.zagip.db;

import android.content.Context;
import android.database.sqlite.SQLiteOpenHelper;
import com.turbomanage.storm.DatabaseHelper;
import com.turbomanage.storm.TableHelper;
import com.turbomanage.storm.api.DatabaseFactory;

/**
 * GENERATED CODE
 *
 * Provides a singleton instance of the {@link DatabaseHelper} and
 * holds the properties obtained from @Database and @Entity annotations
 * (name, version, and associated tables).
 *
 * @author David M. Chandler
 */
public class DB4Factory implements DatabaseFactory {

	private static final String DB_NAME = "DB4";
	private static final int DB_VERSION = 1;
	private static final TableHelper[] TABLE_HELPERS = new TableHelper[] {
		new com.findeds.zagip.entity.dao.TravelTable(),
		new com.findeds.zagip.entity.dao.NotesTable()
	};
	private static DatabaseHelper mInstance;

	/**
	 * Provides a singleton instance of the DatabaseHelper per application
	 * to prevent threading issues. See
	 * https://github.com/commonsguy/cwac-loaderex#notes-on-threading
	 *
	 * @param ctx Application context
	 * @return {@link SQLiteOpenHelper} instance
	 */
	public static DatabaseHelper getDatabaseHelper(Context ctx) {
		if (mInstance==null) {
			// in case this is called from an AsyncTask or other thread
		    synchronized(DB4Factory.class) {
		    		if (mInstance == null)
					mInstance = new com.findeds.zagip.db.DBHelper(
									ctx.getApplicationContext(),
									new DB4Factory());
			}
		}
		return mInstance;
	}

	public String getName() {
		return DB_NAME;
	}

	public int getVersion() {
		return DB_VERSION;
	}

	public TableHelper[] getTableHelpers() {
		return TABLE_HELPERS;
	}

	private DB4Factory() {
		// non-instantiable
	}

}
