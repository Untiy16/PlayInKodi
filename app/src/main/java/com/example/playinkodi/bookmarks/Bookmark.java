package com.example.playinkodi.bookmarks;

public class Bookmark {
    private int id;
    private String url;
    public Bookmark(int id, String url) {
        this.id = id;
        this.url = url;
    }

    public int getId() {
        return id;
    }

    public String getUrl() {
        return url;
    }
}