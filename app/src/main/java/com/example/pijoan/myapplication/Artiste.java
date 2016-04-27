package com.example.pijoan.myapplication;

import io.realm.RealmObject;

/**
 * Created by pijoan on 27/04/16.
 */
public class Artiste extends RealmObject {

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
