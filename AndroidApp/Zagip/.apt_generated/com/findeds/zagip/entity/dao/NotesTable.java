package com.findeds.zagip.entity.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.DatabaseUtils.InsertHelper;
import com.turbomanage.storm.query.FilterBuilder;
import com.turbomanage.storm.TableHelper;
import com.findeds.zagip.entity.NotesTB;
import com.turbomanage.storm.types.LongConverter;
import com.turbomanage.storm.types.StringConverter;

/**
 * GENERATED CODE
 *
 * This class contains the SQL DDL for the named entity / table.
 * These methods are not included in the EntityDao class because
 * they must be executed before the Dao can be instantiated, and
 * they are instance methods vs. static so that they can be
 * overridden in a typesafe manner.
 *
 * @author David M. Chandler
 */
public class NotesTable extends TableHelper<NotesTB> {

	public enum Columns implements TableHelper.Column {
		_id,
		IMAGEPATH,
		NOTES
	}

	@Override
	public String getTableName() {
		return "Notes";
	}

	@Override
	public Column[] getColumns() {
		return Columns.values();
	}
	
	@Override
	public long getId(NotesTB obj) {
		return obj.getId();
	}
	
	@Override
	public void setId(NotesTB obj, long id) {
		obj.setId(id);
	}

	@Override
	public Column getIdCol() {
		return Columns._id;
	}

	@Override
	public String createSql() {
		return
			"CREATE TABLE IF NOT EXISTS Notes(" +
				"_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
				"IMAGEPATH TEXT," +
				"NOTES TEXT" +
			")";
	}

	@Override
	public String dropSql() {
		return "DROP TABLE IF EXISTS Notes";
	}

	@Override
	public String upgradeSql(int oldVersion, int newVersion) {
		return null;
	}

	@Override
	public String[] getRowValues(Cursor c) {
		String[] values = new String[this.getColumns().length];
		String[] defaultValues = getDefaultValues();
		int colIdx; // entity field's position in the cursor
		colIdx = c.getColumnIndex("_id"); values[0] = (colIdx < 0) ? defaultValues[0] : LongConverter.GET.toString(getLongOrNull(c, colIdx));
		colIdx = c.getColumnIndex("IMAGEPATH"); values[1] = (colIdx < 0) ? defaultValues[1] : StringConverter.GET.toString(getStringOrNull(c, colIdx));
		colIdx = c.getColumnIndex("NOTES"); values[2] = (colIdx < 0) ? defaultValues[2] : StringConverter.GET.toString(getStringOrNull(c, colIdx));
		return values;
	}

	@Override
	public void bindRowValues(InsertHelper insHelper, String[] rowValues) {
		if (rowValues[0] == null) insHelper.bindNull(1); else insHelper.bind(1, LongConverter.GET.fromString(rowValues[0]));
		if (rowValues[1] == null) insHelper.bindNull(2); else insHelper.bind(2, StringConverter.GET.fromString(rowValues[1]));
		if (rowValues[2] == null) insHelper.bindNull(3); else insHelper.bind(3, StringConverter.GET.fromString(rowValues[2]));
	}

	@Override
	public String[] getDefaultValues() {
		String[] values = new String[getColumns().length];
		NotesTB defaultObj = new NotesTB();
		values[0] = LongConverter.GET.toString(LongConverter.GET.toSql(defaultObj.getId()));
		values[1] = StringConverter.GET.toString(StringConverter.GET.toSql(defaultObj.getImagepath()));
		values[2] = StringConverter.GET.toString(StringConverter.GET.toSql(defaultObj.getNotes()));
		return values;
	}

	@Override
	public NotesTB newInstance(Cursor c) {
		NotesTB obj = new NotesTB();
		obj.setId(c.getLong(0));
		obj.setImagepath(c.getString(1));
		obj.setNotes(c.getString(2));
		return obj;
	}

	@Override
	public ContentValues getEditableValues(NotesTB obj) {
		ContentValues cv = new ContentValues();
		cv.put("_id", obj.getId());
		cv.put("IMAGEPATH", obj.getImagepath());
		cv.put("NOTES", obj.getNotes());
		return cv;
	}

	@Override
	public FilterBuilder buildFilter(FilterBuilder filter, NotesTB obj) {
		NotesTB defaultObj = new NotesTB();
		// Include fields in query if they differ from the default object
		if (obj.getId() != defaultObj.getId())
			filter = filter.eq(Columns._id, LongConverter.GET.toSql(obj.getId()));
		if (obj.getImagepath() != defaultObj.getImagepath())
			filter = filter.eq(Columns.IMAGEPATH, StringConverter.GET.toSql(obj.getImagepath()));
		if (obj.getNotes() != defaultObj.getNotes())
			filter = filter.eq(Columns.NOTES, StringConverter.GET.toSql(obj.getNotes()));
		return filter;
	}

}