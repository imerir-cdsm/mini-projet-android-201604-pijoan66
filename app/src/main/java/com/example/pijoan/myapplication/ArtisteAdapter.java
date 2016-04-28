package com.example.pijoan.myapplication;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by pijoan on 28/04/16.
 */
public class ArtisteAdapter extends BaseAdapter {
    private LayoutInflater inflater;

    private List<Artiste> artistes = null;

    public ArtisteAdapter(Context context) {
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setData(List<Artiste> details) {
        this.artistes = details;
    }

    @Override
    public int getCount() {
        if (artistes != null) {
            return 0;
        }
        else {
            return artistes.size();
        }
    }

    @Override
    public Object getItem(int position) {
        if (artistes == null || artistes.get(position) ==null) {
            return null;
        }
        return artistes.get(position);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View currentView, ViewGroup parent) {
        if (currentView == null) {
            currentView = inflater.inflate(R.layout.artiste_item, parent, false);
        }
        Artiste artiste = artistes.get(position);

        if (artiste != null) {
            ((TextView) currentView.findViewById(R.id.TitreArtiste)).setText(artiste.getName());
            Log.e("Artiste", artiste.getName());
        }

        return currentView;
    }
}
