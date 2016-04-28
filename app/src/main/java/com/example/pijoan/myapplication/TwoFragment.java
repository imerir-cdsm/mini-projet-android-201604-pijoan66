package com.example.pijoan.myapplication;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.androidquery.AQuery;

import io.realm.Realm;


/**
 * A simple {@link Fragment} subclass.
 */
public class TwoFragment extends Fragment {
    View v;

    Realm realm;
    ListView ListArtiste;

    ArtisteAdapter myadapter;

    AQuery aq;

    public TwoFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v =inflater.inflate(R.layout.fragment_two, container, false);

        return v;
    }

}
