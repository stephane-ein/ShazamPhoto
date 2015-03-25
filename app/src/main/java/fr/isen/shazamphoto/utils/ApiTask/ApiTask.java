package fr.isen.shazamphoto.utils.ApiTask;

import android.os.AsyncTask;

import org.json.JSONObject;

public class ApiTask extends AsyncTask<String, Void, JSONObject> {
    @Override
    protected JSONObject doInBackground(String... params) {
        return null;
    }
    /*private static final String URL = "http://" + ConfigurationShazam.IP_SERVER + "/shazam/api.php?";
    private HttpClient client;
    private ArrayList<Monument> monuments;
    private JSONObject jsonResponse;
    private SearchableItem searchableItem;
    private String
    public ApiTask(SearchableItem searchableItem, String urlArgument) {
        client = new DefaultHttpClient();
        client.getParams().setParameter(CoreProtocolPNames.USER_AGENT,
                "android");
        monuments = new ArrayList<>();
        this.searchableItem = searchableItem;
        this.url
    }

    // Indicate the latitude, longitude and the delta in the arguments
    public JSONObject doInBackground(String... args) {
        jsonResponse = null;

        if (args.length == 3) {
            setArgument(args[0], args[1], args[2]);
            try {
                HttpGet request = new HttpGet(urlWithArguments);
                request.setHeader("Content-type", "application/json");
                request.setURI(new URI(urlWithArguments));
                HttpResponse response = client.execute(request);
                BufferedReader rd = new BufferedReader(new InputStreamReader(
                        response.getEntity().getContent()));
                StringBuffer result = new StringBuffer();
                String line = "";
                while ((line = rd.readLine()) != null) {
                    result.append(line);
                }
                jsonResponse = new JSONObject(result.toString());
            } catch (Exception e) {
            }
        }

        return jsonResponse;
    }*/

}