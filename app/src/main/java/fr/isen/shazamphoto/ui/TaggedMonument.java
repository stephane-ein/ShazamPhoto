package fr.isen.shazamphoto.ui;

import android.os.Bundle;

import fr.isen.shazamphoto.model.ModelNavigation;

public class TaggedMonument extends MonumentList {

    public static TaggedMonument newInstance(ModelNavigation modelNavigation) {
        TaggedMonument fragment = new TaggedMonument();
        Bundle args = new Bundle();
        args.putSerializable(ModelNavigation.KEY, modelNavigation);
        fragment.setArguments(args);
        return fragment;
    }

    public TaggedMonument() {
        super(TaggedMonument.class.getSimpleName());
    }

}
