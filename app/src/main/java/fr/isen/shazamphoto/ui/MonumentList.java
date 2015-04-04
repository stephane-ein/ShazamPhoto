package fr.isen.shazamphoto.ui;


import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;

import java.util.ArrayList;

import fr.isen.shazamphoto.database.Monument;
import fr.isen.shazamphoto.events.EventDisplayDetailMonument;
import fr.isen.shazamphoto.model.ModelNavigation;

public abstract class MonumentList extends Fragment {

    private AbsListView absListView;
    private BaseAdapter adapter;
    private ModelNavigation modelNavigation;

    protected void setListMonuments(final ArrayList<Monument> monuments, BaseAdapter adapter, AbsListView view) {
        view.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,
                                    int position, long arg3) {
                modelNavigation.changeAppView(new EventDisplayDetailMonument(getActivity(),
                        monuments.get(position), modelNavigation));
            }
        });
    }

    public AbsListView getAbsListView() {
        return absListView;
    }

    public void setAbsListView(AbsListView absListView) {
        this.absListView = absListView;
    }

    public BaseAdapter getAdapter() {
        return adapter;
    }

    public void setAdapter(BaseAdapter adapter) {
        this.adapter = adapter;
    }

    public void setModelNavigation(ModelNavigation modelNavigation) {
        this.modelNavigation = modelNavigation;
    }
}
