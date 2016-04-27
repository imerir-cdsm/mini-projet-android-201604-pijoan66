package com.example.pijoan.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by pijoan on 27/04/16.
 */
public class AlbumAdapter extends BaseAdapter {
    private LayoutInflater inflater;

    private List<Album> albums = null;

    public AlbumAdapter(Context context) {
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setData(List<Album> details) {
        this.albums = details;
    }

    @Override
    public int getCount() {
        if (albums == null) {
            return 0;
        }
        else {
            return albums.size();
        }
    }

    @Override
    public Object getItem(int position) {
        if (albums == null || albums.get(position) == null) {
            return null;
        }
        return albums.get(position);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View currentView, ViewGroup parent) {
        if (currentView == null) {
            currentView = inflater.inflate(R.layout.album_item, parent, false);
        }

        Album album = albums.get(position);

        if (album == null) {
            ((TextView) currentView.findViewById(R.id.TitreAlbum)).setText(album.getTitre());
        }

        return currentView;
    }
}
