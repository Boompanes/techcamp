package com.findeds.zagip.entity;

import com.turbomanage.storm.api.Entity;

/**
 * POJO used to create SQLite Account table.
 * <p/>
 * Uses storm-gen, a Lightweight DAO generator for Android SQLite.
 * Please be aware that this requires storm-apt for annotation.
 * See https://code.google.com/p/storm-gen/ for more info.
 *
 * @author Emmanuel Delos Santos
 * @since 3/17/14.
 */
@Entity(name = "Notes")
public class NotesTB {
    private long id;
    private String notes;
    private String imagepath;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getImagepath() {
        return imagepath;
    }

    public void setImagepath(String imagepath) {
        this.imagepath = imagepath;
    }

}
