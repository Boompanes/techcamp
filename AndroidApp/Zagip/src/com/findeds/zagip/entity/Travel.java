package com.findeds.zagip.entity;

/**
 * POJO used to consolidate SQLite tables to be displayed in History logs.
 *
 * @author Emmanuel Delos Santos
 * @since 1/13/14.
 */
public class Travel {

    private long id;
    private long type_id;
    private long time;
    private String type;
    private String header;
    private String footer;
    private String right;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getType_id() {
        return type_id;
    }

    public void setType_id(long type_id) {
        this.type_id = type_id;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getRight() {
        return right;
    }

    public void setRight(String right) {
        this.right = right;
    }

    public String getFooter() {
        return footer;
    }

    public void setFooter(String footer) {
        this.footer = footer;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
