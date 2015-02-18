package fr.isen.shazamphoto.ui;

public class TaggedMonument extends MonumentList {

    public static TaggedMonument newInstance() {
        TaggedMonument fragment = new TaggedMonument();
        return fragment;
    }

    public TaggedMonument() {
        super(TaggedMonument.class.getSimpleName());
    }

}
