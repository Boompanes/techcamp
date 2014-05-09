package com.findeds.zagip.db;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import com.findeds.zagip.MainActivity;
import com.findeds.zagip.Session;
import com.findeds.zagip.entity.NotesTB;
import com.findeds.zagip.entity.TravelTB;
import com.findeds.zagip.entity.dao.NotesTBDao;
import com.findeds.zagip.entity.dao.NotesTable;
import com.findeds.zagip.entity.dao.TravelTBDao;
import com.findeds.zagip.entity.dao.TravelTable;

/**
 * Collection of DAO queries
 * Uses storm-gen, a Lightweight DAO generator for Android SQLite.
 * Please be aware that this requires storm-apt for annotation.
 * See https://code.google.com/p/storm-gen/ for more info.
 *
 * @author Emmanuel Delos Santos
 * @since 3/16/14.
 */
public class Storm {
    public static TravelTB travelTB;
    public static TravelTBDao travelTBDao;
    public static NotesTB notesTB;
    public static NotesTBDao notesTBDao;
    private static Storm instance;
    private static Context context;
    private static Cursor cursor;

    public Storm(Context context) {
        if (this.context == null) {
            this.context = context;

            travelTBDao = new TravelTBDao(context);
            notesTBDao = new NotesTBDao(context);
        }
    }

    public static Storm getInstance(Context context) {
        if (instance == null) {
            instance = new Storm(context);
        }
        return instance;
    }

    public boolean removeTravel(long id) {
        boolean isDeleted = travelTBDao.delete(id) != 0;
        notesTBDao.delete(id);
        MainActivity.scan_travel_list();
        return isDeleted;
    }

    public long setTravel() {
        travelTB = new TravelTB();

        //optional -> travelTB.setId();
        travelTB.setLat1(Session.lat1);
        travelTB.setLon1(Session.lon1);
        travelTB.setLat2(Session.lat2);
        travelTB.setLon2(Session.lon2);
        travelTB.setName1(Session.name1);
        travelTB.setName2(Session.name2);
        travelTB.setDistance(Session.distance);
        travelTB.setTime(Session.time);

        String caption = Session.caption;
        boolean caption_set = false;
        int n = 2;
        do {
            cursor = travelTBDao.filter().eq(TravelTable.Columns.CAPTION, caption).exec();
            if (cursor.isAfterLast()) {
                travelTB.setCaption(caption);
                caption_set = true;
            } else {
                caption = Session.caption + " " + n++;
            }
        } while (!caption_set);

        long id = travelTBDao.insert(travelTB);
        MainActivity.scan_travel_list();
        return id;
    }

    public boolean saveNotes() {
        String notes = Session.notes;
        if (notes == null || notes.trim().length() == 0)
            return false;
        boolean status;
        cursor = notesTBDao.filter().eq(NotesTable.Columns._id, Session.id).exec();
        notesTB = new NotesTB();
        notesTB.setId(Session.id);
        notesTB.setNotes(notes);
        notesTB.setImagepath(Session.image_path);
        if (cursor.isAfterLast()) {
            Log.e(null, "inserting notes");
            status = notesTBDao.insert(notesTB) > 0;
        } else {
            Log.e(null, "updating notes");
            status = notesTBDao.update(notesTB) > 0;
        }
        Log.e(null, "saveNotes | " + " notes: " + Session.notes + ", imagepath: " + Session.image_path);
        return status;
    }

    public String getNotes() {
        notesTB = new NotesTB();
        cursor = notesTBDao.filter().eq(NotesTable.Columns._id, Session.id).exec();
        if (!cursor.isAfterLast()){
            cursor.moveToFirst();
            return cursor.getString(cursor.getColumnIndex(NotesTable.Columns.NOTES.name()));
        }
        else
            return null;
    }

    public boolean saveImagePath(){
        cursor = notesTBDao.filter().eq(NotesTable.Columns._id, Session.id).exec();
        notesTB = new NotesTB();
        notesTB.setId(Session.id);
        notesTB.setNotes(Session.notes);
        notesTB.setImagepath(Session.image_path);
        boolean status;
        if (cursor.isAfterLast()) {
            Log.e(null, "inserting image");
            status = notesTBDao.insert(notesTB) > 0;
        } else {
            Log.e(null, "updating image");
            status = notesTBDao.update(notesTB) > 0;
        }
        Log.e(null, "saveImagePath | " + " notes: " + Session.notes + ", imagepath: " + Session.image_path);
        return status;
    }

    public String getImagePath(){
        notesTB = new NotesTB();
        cursor = notesTBDao.filter().eq(NotesTable.Columns._id, Session.id).exec();
        if (!cursor.isAfterLast()){
            cursor.moveToFirst();
            return cursor.getString(cursor.getColumnIndex(NotesTable.Columns.IMAGEPATH.name()));
        }
        else
            return null;
    }
}
