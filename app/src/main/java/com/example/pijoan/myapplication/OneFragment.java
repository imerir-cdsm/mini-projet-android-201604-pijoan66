package com.example.pijoan.myapplication;


import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
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
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;


/**
 * A simple {@link Fragment} subclass.
 */
public class OneFragment extends Fragment implements AdapterView.OnItemClickListener {
    ListView listAlbum;
    AlbumAdapter adaptAlbum;
    Album album;
    AQuery aq;

    public OneFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_one, container, false);
        //Définition de l'adapter
        adaptAlbum = new AlbumAdapter(getContext());
        //Liaison de la ListView à la List du fichier XML
        listAlbum = (ListView) v.findViewById(R.id.ListAlbum);
        //Ajout de l'adapter à la List
        listAlbum.setAdapter(adaptAlbum);
        listAlbum.setOnItemClickListener(this);

        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //updateAlbums();
    }

    //onResume() fonctionne à chaque chargement de la page
    @Override
    public void onResume() {
        super.onResume();

        recherche();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                Realm realm = Realm.getDefaultInstance();
                RealmResults<Album> albums = realm.allObjects(Album.class);
                adaptAlbum.setData(albums);
                adaptAlbum.notifyDataSetChanged();
                listAlbum.invalidate();
            }
        }, 200);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private List<Album> loadAlbums() {
        Realm realm = Realm.getDefaultInstance();
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

    //Fonction qui va utiliser AQuery pour chercher dans la base de donnée la liste des albums
    public void recherche() {
        AsyncTask<String, Void, Void> task = new AsyncTask<String, Void, Void>() {
            @Override
            protected Void doInBackground(String... params) {
                String url = params[0];
                aq = new AQuery(getView()); //Déclaration du Aquery
                aq.ajax(url, JSONArray.class, new AjaxCallback<JSONArray>() {
                    @Override
                    public void callback(String url, JSONArray json, AjaxStatus status) {
                        Realm realm = Realm.getDefaultInstance(); //Config de Realm

                        if (json != null) {
                            //Debut d'écriture dans Realm
                            realm.beginTransaction();
                            //Suppression de tous ceux contient le Realm
                            realm.deleteAll();
                            for (int i = 0; i < json.length(); i++) {
                                try {
                                    JSONObject albumRow = json.getJSONObject(i);
                                    //Création d'un object de la classe Album dans le Realm
                                    album = realm.createObject(Album.class);
                                    //Attribution du nom à l'objet album
                                    album.setTitle(albumRow.getString("title"));
                                } catch (JSONException je) {
                                    je.printStackTrace();
                                }
                            }
                            //Envoie des données ecrite juste avant dans le Realm
                            realm.commitTransaction();
                        } else {
                            //Message d'erreur lors d'erreur d'Ajax
                            Log.e("Error", "Ajax error :" + status.getMessage());
                            Toast.makeText(aq.getContext(), "Error:" + status.getCode(), Toast.LENGTH_LONG).show();
                        }
                        //Récupération des données des objest de la classe Album pour le RealmResult
                        RealmResults<Album> albums = realm.allObjects(Album.class);
                        //Affichage du nombre d'artiste
                        Log.e("AQuery", "Nombre albums : " + albums.size());
                    }
                });
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);

                RealmResults<Album> albums = Realm.getDefaultInstance().allObjects(Album.class);
                adaptAlbum.setData(albums);
                adaptAlbum.notifyDataSetChanged();
                listAlbum.invalidate();
            }
        };
        //Url pour récuperer la liste des albums
        task.execute("http://mysterious-thicket-90159.herokuapp.com/albums");
    }

    //Fonction pour mettre à jour la liste des albums
    public void updateAlbums() {
        Realm realm = Realm.getDefaultInstance();
        RealmResults<Album> albums =realm.where(Album.class).findAll();
        adaptAlbum.setData(albums);
        adaptAlbum.notifyDataSetChanged();
        listAlbum.invalidate();
    }

    //Fonction lors d'un click sur un item de la list
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //Affiche la position du click
        Log.e("OneFragment", "item clicked "+position);
    }
}
