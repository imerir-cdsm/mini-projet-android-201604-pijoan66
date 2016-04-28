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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import io.realm.Realm;
import io.realm.RealmResults;


/**
 * A simple {@link Fragment} subclass.
 */
public class TwoFragment extends Fragment implements AdapterView.OnItemClickListener{
    ListView listArtiste;
    Artiste artiste;
    ArtisteAdapter adaptArtiste;

    AQuery aq;

    public TwoFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =inflater.inflate(R.layout.fragment_two, container, false);
        //Définition de l'adapter
        adaptArtiste = new ArtisteAdapter(getContext());
        //Liaison de la ListView à la liste du fichier XML
        listArtiste = (ListView) v.findViewById(R.id.ListArtiste);
        //Ajout de l'adapter à la ListView
        listArtiste.setAdapter(adaptArtiste);
        listArtiste.setOnItemClickListener(this);

        return v;
    }

    @Override
    public void onViewCreated(View view,Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
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
                RealmResults<Artiste> artistes = realm.allObjects(Artiste.class);
                adaptArtiste.setData(artistes);
                adaptArtiste.notifyDataSetChanged();
                listArtiste.invalidate();
            }
        }, 200);


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    //Fonction qui va utiliser AQuery pour chercher dans la base de donnée la liste des artistes
    public void recherche() {
        AsyncTask<String, Void, Void> task = new AsyncTask<String, Void, Void>() {
            @Override
            protected Void doInBackground(String... params) {
                String url = params[0];
                aq = new AQuery(getView()); //Déclaration du Aquery
                aq.ajax(url, JSONArray.class, new AjaxCallback<JSONArray>() {
                    @Override
                    public void callback(String url , JSONArray json, AjaxStatus status) {
                        Realm realm = Realm.getDefaultInstance(); //Config de Realm

                        if (json != null){
                            //Debut d'écriture dans Realm
                            realm.beginTransaction();
                            //Suppression de tous ceux contient le Realm
                            realm.deleteAll();
                            for (int i = 0; i<json.length(); i++) {
                                try {
                                    JSONObject artisteRow = json.getJSONObject(i);
                                    //Création d'un object de la classe Artiste dans le Realm
                                    artiste = realm.createObject(Artiste.class);
                                    //Attribution du nom à l'objet artiste
                                    artiste.setFname(artisteRow.getString("fname"));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            //Envoie des données ecrite juste avant dans le Realm
                            realm.commitTransaction();
                        }
                        else {
                            //Message d'erreur lors d'erreur d'Ajax
                            Log.e("Error", "Ajax error" + status.getMessage());
                            Toast.makeText(aq.getContext(), "Error:" + status.getCode(), Toast.LENGTH_LONG).show();
                        }
                        //Récupération des données des objest de la classe Artiste pour le RealmResult
                        RealmResults<Artiste> artistes = realm.allObjects(Artiste.class);
                        //Affichage du nombre d'artiste
                        Log.e("AQuery","Nombre d'artiste :" + artistes.size());
                    }
                });
                return null;
            }
            //Fonction qui s'execute apres la fonction doInBackground()
            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                //Récupération des données des objest de la classe Artiste pour le RealmResult
                RealmResults<Artiste> artistes = Realm.getDefaultInstance().allObjects(Artiste.class);
                //Changement des données pour l'adapter
                adaptArtiste.setData(artistes);
                adaptArtiste.notifyDataSetChanged();
                listArtiste.invalidate();
            }
        };
        //Url pour récuperer la liste des artistes
        task.execute("http://mysterious-thicket-90159.herokuapp.com/artists");
    }
    //Fonction lors d'un click sur un item de la list
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //Affiche la position du click
        Log.e("TwoFragment", "item clicked "+position);
    }
}
