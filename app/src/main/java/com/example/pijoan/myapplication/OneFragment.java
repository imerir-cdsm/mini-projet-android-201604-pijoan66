package com.example.pijoan.myapplication;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;

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

    AQuery aq;

    public OneFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_one, container, false);

        // Inflate the layout for this fragment
        return v;
    }

    public void recherche() {
        String url = "http://mysterious-thicket-90159.herokuapp.com/albums";
        aq = new AQuery(v);
        aq.ajax(url, JSONArray.class, new AjaxCallback<JSONArray>() {
            @Override
            public void callback(String url, JSONArray json, AjaxStatus status) {
                if (json != null) {
                    String JsonString = json.toString();
                    Log.e("AQuery", JsonString);
                } else {
                    //ajax error, show error code
                    Log.e("Error", "C'est une grosse erreur " + status.getMessage());
                    Toast.makeText(aq.getContext(), "Error:" + status.getCode(), Toast.LENGTH_LONG).show();
                }

            }
        });
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recherche();

        RealmConfiguration.Builder builder = new RealmConfiguration.Builder(getContext());
        builder.schemaVersion(1);
        builder.deleteRealmIfMigrationNeeded();
        RealmConfiguration realmConfiguration = builder.build();
        Realm.setDefaultConfiguration(realmConfiguration);

        realm = Realm.getInstance(realmConfiguration);

        if (myadapter == null) {
            myadapter = new AlbumAdapter(getContext());
        }
        List<Album> albums = loadAlbums();
        myadapter.setData(albums);

        ListAlbum = (ListView) v.findViewById(R.id.ListAlbum);
        ListAlbum.setAdapter(myadapter);
        ListAlbum.setOnItemClickListener(OneFragment.this);
        myadapter.notifyDataSetChanged();
        ListAlbum.invalidate();

        updateAlbums();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        realm.close();
    }

    private List<Album> loadAlbums() {
        RealmResults<Album> albumsResult = realm.where(Album.class).findAll();
        realm.beginTransaction();
        albumsResult.deleteAllFromRealm();
        realm.commitTransaction();
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
        album.setTitre(album.getTitre());
        realm.commitTransaction();

        updateAlbums();
    }
}
