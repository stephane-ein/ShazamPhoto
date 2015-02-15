package fr.isen.shazamphoto.ui;

public class TaggedMonument extends MonumentList {

    public static TaggedMonument newInstance() {
        TaggedMonument fragment = new TaggedMonument();
        return fragment;
    }

    public TaggedMonument() {
        super(TaggedMonument.class.getSimpleName());
    }
/*
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_tagged_monument, container, false);

        FavouriteMonumentDAO favouriteMonumentDAO = new FavouriteMonumentDAO(getActivity());
        favouriteMonumentDAO.open();
        final List<Monument> monumentsList = favouriteMonumentDAO.getAllMonuments();
        favouriteMonumentDAO.close();
        final ArrayList<Monument> monuments = new ArrayList<Monument>();
        for (Monument monument : monumentsList) {
            monuments.add(monument);
        }

        CustomListAdapter adapter = new CustomListAdapter(getActivity(), monuments);
        ListView listview = (ListView) view.findViewById(R.id.listview_favourite_monument);
        listview.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,
                                    int position, long arg3) {

                Intent intent = new Intent(getActivity(), DetailMonument.class);

                intent.putExtra(Monument.NAME_SERIALIZABLE, monuments.get(position));
                startActivity(intent);
            }
        });
        setRetainInstance(true);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,
                                    int position, long arg3) {

                Intent intent = new Intent(getActivity(), DetailMonument.class);

                intent.putExtra(Monument.NAME_SERIALIZABLE, monuments.get(position));
                startActivity(intent);

            }
        });
        setRetainInstance(true);
        return view;
    }
*/
}
