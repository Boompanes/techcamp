package com.findeds.zagip.entity.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.DatabaseUtils.InsertHelper;
import com.turbomanage.storm.query.FilterBuilder;
import com.turbomanage.storm.TableHelper;
import com.findeds.zagip.entity.TravelTB;
import com.turbomanage.storm.types.StringConverter;
import com.turbomanage.storm.types.LongConverter;
import com.turbomanage.storm.types.DoubleConverter;

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
public class TravelTable extends TableHelper<TravelTB> {

	public enum Columns implements TableHelper.Column {
		CAPTION,
		DISTANCE,
		_id,
		LAT1,
		LAT2,
		LON1,
		LON2,
		NAME1,
		NAME2,
		SCHEDULE,
		TIME
	}

	@Override
	public String getTableName() {
		return "Travel";
	}

	@Override
	public Column[] getColumns() {
		return Columns.values();
	}
	
	@Override
	public long getId(TravelTB obj) {
		return obj.getId();
	}
	
	@Override
	public void setId(TravelTB obj, long id) {
		obj.setId(id);
	}

	@Override
	public Column getIdCol() {
		return Columns._id;
	}

	@Override
	public String createSql() {
		return
			"CREATE TABLE IF NOT EXISTS Travel(" +
				"CAPTION TEXT," +
				"DISTANCE TEXT," +
				"_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
				"LAT1 REAL NOT NULL," +
				"LAT2 REAL NOT NULL," +
				"LON1 REAL NOT NULL," +
				"LON2 REAL NOT NULL," +
				"NAME1 TEXT," +
				"NAME2 TEXT," +
				"SCHEDULE INTEGER NOT NULL," +
				"TIME INTEGER NOT NULL" +
			")";
	}

	@Override
	public String dropSql() {
		return "DROP TABLE IF EXISTS Travel";
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
		colIdx = c.getColumnIndex("CAPTION"); values[0] = (colIdx < 0) ? defaultValues[0] : StringConverter.GET.toString(getStringOrNull(c, colIdx));
		colIdx = c.getColumnIndex("DISTANCE"); values[1] = (colIdx < 0) ? defaultValues[1] : StringConverter.GET.toString(getStringOrNull(c, colIdx));
		colIdx = c.getColumnIndex("_id"); values[2] = (colIdx < 0) ? defaultValues[2] : LongConverter.GET.toString(getLongOrNull(c, colIdx));
		colIdx = c.getColumnIndex("LAT1"); values[3] = (colIdx < 0) ? defaultValues[3] : DoubleConverter.GET.toString(getDoubleOrNull(c, colIdx));
		colIdx = c.getColumnIndex("LAT2"); values[4] = (colIdx < 0) ? defaultValues[4] : DoubleConverter.GET.toString(getDoubleOrNull(c, colIdx));
		colIdx = c.getColumnIndex("LON1"); values[5] = (colIdx < 0) ? defaultValues[5] : DoubleConverter.GET.toString(getDoubleOrNull(c, colIdx));
		colIdx = c.getColumnIndex("LON2"); values[6] = (colIdx < 0) ? defaultValues[6] : DoubleConverter.GET.toString(getDoubleOrNull(c, colIdx));
		colIdx = c.getColumnIndex("NAME1"); values[7] = (colIdx < 0) ? defaultValues[7] : StringConverter.GET.toString(getStringOrNull(c, colIdx));
		colIdx = c.getColumnIndex("NAME2"); values[8] = (colIdx < 0) ? defaultValues[8] : StringConverter.GET.toString(getStringOrNull(c, colIdx));
		colIdx = c.getColumnIndex("SCHEDULE"); values[9] = (colIdx < 0) ? defaultValues[9] : LongConverter.GET.toString(getLongOrNull(c, colIdx));
		colIdx = c.getColumnIndex("TIME"); values[10] = (colIdx < 0) ? defaultValues[10] : LongConverter.GET.toString(getLongOrNull(c, colIdx));
		return values;
	}

	@Override
	public void bindRowValues(InsertHelper insHelper, String[] rowValues) {
		if (rowValues[0] == null) insHelper.bindNull(1); else insHelper.bind(1, StringConverter.GET.fromString(rowValues[0]));
		if (rowValues[1] == null) insHelper.bindNull(2); else insHelper.bind(2, StringConverter.GET.fromString(rowValues[1]));
		if (rowValues[2] == null) insHelper.bindNull(3); else insHelper.bind(3, LongConverter.GET.fromString(rowValues[2]));
		if (rowValues[3] == null) insHelper.bindNull(4); else insHelper.bind(4, DoubleConverter.GET.fromString(rowValues[3]));
		if (rowValues[4] == null) insHelper.bindNull(5); else insHelper.bind(5, DoubleConverter.GET.fromString(rowValues[4]));
		if (rowValues[5] == null) insHelper.bindNull(6); else insHelper.bind(6, DoubleConverter.GET.fromString(rowValues[5]));
		if (rowValues[6] == null) insHelper.bindNull(7); else insHelper.bind(7, DoubleConverter.GET.fromString(rowValues[6]));
		if (rowValues[7] == null) insHelper.bindNull(8); else insHelper.bind(8, StringConverter.GET.fromString(rowValues[7]));
		if (rowValues[8] == null) insHelper.bindNull(9); else insHelper.bind(9, StringConverter.GET.fromString(rowValues[8]));
		if (rowValues[9] == null) insHelper.bindNull(10); else insHelper.bind(10, LongConverter.GET.fromString(rowValues[9]));
		if (rowValues[10] == null) insHelper.bindNull(11); else insHelper.bind(11, LongConverter.GET.fromString(rowValues[10]));
	}

	@Override
	public String[] getDefaultValues() {
		String[] values = new String[getColumns().length];
		TravelTB defaultObj = new TravelTB();
		values[0] = StringConverter.GET.toString(StringConverter.GET.toSql(defaultObj.getCaption()));
		values[1] = StringConverter.GET.toString(StringConverter.GET.toSql(defaultObj.getDistance()));
		values[2] = LongConverter.GET.toString(LongConverter.GET.toSql(defaultObj.getId()));
		values[3] = DoubleConverter.GET.toString(DoubleConverter.GET.toSql(defaultObj.getLat1()));
		values[4] = DoubleConverter.GET.toString(DoubleConverter.GET.toSql(defaultObj.getLat2()));
		values[5] = DoubleConverter.GET.toString(DoubleConverter.GET.toSql(defaultObj.getLon1()));
		values[6] = DoubleConverter.GET.toString(DoubleConverter.GET.toSql(defaultObj.getLon2()));
		values[7] = StringConverter.GET.toString(StringConverter.GET.toSql(defaultObj.getName1()));
		values[8] = StringConverter.GET.toString(StringConverter.GET.toSql(defaultObj.getName2()));
		values[9] = LongConverter.GET.toString(LongConverter.GET.toSql(defaultObj.getSchedule()));
		values[10] = LongConverter.GET.toString(LongConverter.GET.toSql(defaultObj.getTime()));
		return values;
	}

	@Override
	public TravelTB newInstance(Cursor c) {
		TravelTB obj = new TravelTB();
		obj.setCaption(c.getString(0));
		obj.setDistance(c.getString(1));
		obj.setId(c.getLong(2));
		obj.setLat1(c.getDouble(3));
		obj.setLat2(c.getDouble(4));
		obj.setLon1(c.getDouble(5));
		obj.setLon2(c.getDouble(6));
		obj.setName1(c.getString(7));
		obj.setName2(c.getString(8));
		obj.setSchedule(c.getLong(9));
		obj.setTime(c.getLong(10));
		return obj;
	}

	@Override
	public ContentValues getEditableValues(TravelTB obj) {
		ContentValues cv = new ContentValues();
		cv.put("CAPTION", obj.getCaption());
		cv.put("DISTANCE", obj.getDistance());
		cv.put("_id", obj.getId());
		cv.put("LAT1", obj.getLat1());
		cv.put("LAT2", obj.getLat2());
		cv.put("LON1", obj.getLon1());
		cv.put("LON2", obj.getLon2());
		cv.put("NAME1", obj.getName1());
		cv.put("NAME2", obj.getName2());
		cv.put("SCHEDULE", obj.getSchedule());
		cv.put("TIME", obj.getTime());
		return cv;
	}

	@Override
	public FilterBuilder buildFilter(FilterBuilder filter, TravelTB obj) {
		TravelTB defaultObj = new TravelTB();
		// Include fields in query if they differ from the default object
		if (obj.getCaption() != defaultObj.getCaption())
			filter = filter.eq(Columns.CAPTION, StringConverter.GET.toSql(obj.getCaption()));
		if (obj.getDistance() != defaultObj.getDistance())
			filter = filter.eq(Columns.DISTANCE, StringConverter.GET.toSql(obj.getDistance()));
		if (obj.getId() != defaultObj.getId())
			filter = filter.eq(Columns._id, LongConverter.GET.toSql(obj.getId()));
		if (obj.getLat1() != defaultObj.getLat1())
			filter = filter.eq(Columns.LAT1, DoubleConverter.GET.toSql(obj.getLat1()));
		if (obj.getLat2() != defaultObj.getLat2())
			filter = filter.eq(Columns.LAT2, DoubleConverter.GET.toSql(obj.getLat2()));
		if (obj.getLon1() != defaultObj.getLon1())
			filter = filter.eq(Columns.LON1, DoubleConverter.GET.toSql(obj.getLon1()));
		if (obj.getLon2() != defaultObj.getLon2())
			filter = filter.eq(Columns.LON2, DoubleConverter.GET.toSql(obj.getLon2()));
		if (obj.getName1() != defaultObj.getName1())
			filter = filter.eq(Columns.NAME1, StringConverter.GET.toSql(obj.getName1()));
		if (obj.getName2() != defaultObj.getName2())
			filter = filter.eq(Columns.NAME2, StringConverter.GET.toSql(obj.getName2()));
		if (obj.getSchedule() != defaultObj.getSchedule())
			filter = filter.eq(Columns.SCHEDULE, LongConverter.GET.toSql(obj.getSchedule()));
		if (obj.getTime() != defaultObj.getTime())
			filter = filter.eq(Columns.TIME, LongConverter.GET.toSql(obj.getTime()));
		return filter;
	}

}