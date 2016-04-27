package com.example.pijoan.myapplication;

import io.realm.Realm;

/**
 * Created by pijoan on 27/04/16.
 */
public class TestRealm {

    Realm realm;

    public TestRealm () {
        realm = Realm.getDefaultInstance();
    }
}
