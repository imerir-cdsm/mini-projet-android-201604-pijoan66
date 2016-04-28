package com.example.pijoan.myapplication;

import io.realm.RealmObject;

/**
 * Created by pijoan on 27/04/16.
 */
public class Album extends RealmObject {

    private String title;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
