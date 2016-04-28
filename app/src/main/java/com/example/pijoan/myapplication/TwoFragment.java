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
    Realm realm;
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

        adaptArtiste = new ArtisteAdapter(getContext());

        listArtiste = (ListView) v.findViewById(R.id.ListArtiste);
        listArtiste.setAdapter(adaptArtiste);
        listArtiste.setOnItemClickListener(this);

        return v;
    }

    @Override
    public void onViewCreated(View view,Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

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

    public void recherche() {
        AsyncTask<String, Void, Void> task = new AsyncTask<String, Void, Void>() {
            @Override
            protected Void doInBackground(String... params) {
                String url = params[0];
                aq = new AQuery(getView());
                aq.ajax(url, JSONArray.class, new AjaxCallback<JSONArray>() {
                    @Override
                    public void callback(String url , JSONArray json, AjaxStatus status) {
                        Realm realm = Realm.getDefaultInstance();

                        if (json != null){
                            String jsonString = json.toString();

                            realm.beginTransaction();
                            realm.deleteAll();
                            for (int i = 0; i<json.length(); i++) {
                                try {
                                    JSONObject artisteRow = json.getJSONObject(i);
                                    artiste = realm.createObject(Artiste.class);
                                    artiste.setFname(artisteRow.getString("fname"));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            realm.commitTransaction();
                        }
                        else {
                            //ajax error, show error code
                            Log.e("Error", "Ajax error" + status.getMessage());
                            Toast.makeText(aq.getContext(), "Error:" + status.getCode(), Toast.LENGTH_LONG).show();
                        }
                        RealmResults<Artiste> artistes = realm.allObjects(Artiste.class);
                        Log.e("AQuery","Nombre d'artiste :" + artistes.size());
                    }
                });
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);

                RealmResults<Artiste> artistes = Realm.getDefaultInstance().allObjects(Artiste.class);
                adaptArtiste.setData(artistes);
                adaptArtiste.notifyDataSetChanged();
                listArtiste.invalidate();
            }
        };

        task.execute("http://mysterious-thicket-90159.herokuapp.com/artists");
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.e("TwoFragment", "item clicked "+position);
    }
}
