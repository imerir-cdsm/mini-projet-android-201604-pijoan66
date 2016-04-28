package com.example.pijoan.myapplication;

import io.realm.RealmObject;

/**
 * Created by pijoan on 27/04/16.
 */
public class Artiste extends RealmObject {

    private String fname;

    public String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }
}
