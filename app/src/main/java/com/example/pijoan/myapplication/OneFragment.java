package com.example.pijoan.myapplication;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;


/**
 * A simple {@link Fragment} subclass.
 */
public class OneFragment extends Fragment implements AdapterView.OnItemClickListener {
    View v;

    Realm realm;
    ListView ListAlbum;

    AlbumAdapter myadapter;

    public OneFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_one, container, false);

        RealmConfiguration.Builder builder = new RealmConfiguration.Builder(getContext());
        builder.schemaVersion(1);
        builder.deleteRealmIfMigrationNeeded();
        RealmConfiguration realmConfiguration = builder.build();
        Realm.setDefaultConfiguration(realmConfiguration);

        realm = Realm.getInstance(realmConfiguration);

        // Inflate the layout for this fragment
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (myadapter == null) {
            List<Album> albums = loadAlbums();

            myadapter = new AlbumAdapter(getContext());
            myadapter.setData(albums);

            ListAlbum = (ListView) v.findViewById(R.id.ListAlbum);
            ListAlbum.setAdapter(myadapter);
            ListAlbum.setOnItemClickListener(OneFragment.this);
            ListAlbum.deferNotifyDataSetChanged();
            ListAlbum.invalidate();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        realm.close();
    }

    private List<Album> loadAlbums() {
        InputStream stream;
        try {
            stream = getContext().getAssets().open("album.json");
        } catch (IOException e) {
            return null;
        }
        Gson gson = new GsonBuilder().create();

        JsonElement json = new JsonParser().parse(new InputStreamReader(stream));
        List<Album> albums = gson.fromJson(json, new TypeToken<List<Album>>() {}.getType());

        realm.beginTransaction();
        Collection<Album> realmalbums = realm.copyToRealm(albums);
        realm.commitTransaction();

        return new ArrayList<Album>(realmalbums);
    }

    public void updateAlbums() {
        RealmResults<Album> albums =realm.where(Album.class).findAll();
        myadapter.setData(albums);
        myadapter.notifyDataSetChanged();
        ListAlbum.invalidate();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Album modifiedAlbum = (Album)myadapter.getItem(position);
        Album album = realm.where(Album.class).equalTo("titre", modifiedAlbum.getTitre()).findFirst();

        realm.beginTransaction();
        realm.commitTransaction();

        updateAlbums();
    }
}
