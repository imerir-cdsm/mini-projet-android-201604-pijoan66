package com.example.pijoan.myapplication;

import io.realm.RealmObject;

/**
 * Created by pijoan on 27/04/16.
 */
public class Album extends RealmObject {

    private String titre;

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }
}
