package com.findeds.zagip.entity.dao;

import android.content.Context;
import com.turbomanage.storm.DatabaseHelper;
import com.turbomanage.storm.TableHelper;
import com.findeds.zagip.entity.NotesTB;
import com.turbomanage.storm.SQLiteDao;

/**
 * GENERATED CODE
 *
 * @author David M. Chandler
 */
public class NotesTBDao extends SQLiteDao<NotesTB>{

    @Override
	public DatabaseHelper getDbHelper(Context ctx) {
		return com.findeds.zagip.db.DB4Factory.getDatabaseHelper(ctx);
	}

	@Override
	@SuppressWarnings("rawtypes")
	public TableHelper getTableHelper() {
		return new com.findeds.zagip.entity.dao.NotesTable();
	}

	/**
	 * @see SQLiteDao#SQLiteDao(Context)
	 */
	public NotesTBDao(Context ctx) {
		super(ctx);
	}

}