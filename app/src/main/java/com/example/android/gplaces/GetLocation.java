package com.example.android.gplaces;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.List;

/*
The following activity is called as soon the user hits the search button in the splash activity with all the required information
 */

public class GetLocation extends AppCompatActivity {

    private static final String LOG_TAG = GetLocation.class.getName();

    String USGS_REQUEST_URL = "https://maps.googleapis.com";
    // ListView lv;
    ArrayAdapter<Places> adapter;
    ProgressBar search;
    ImageView look;
    int rad = 5000;
    String editText;
    ListView placeListView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request);

        // The following line presents the back arrow button on the action bar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Find a reference to the {@link ListView} in the layout
        placeListView = (ListView) findViewById(R.id.list);


        // Create a new adapter that takes an empty list of places as input
        adapter = new PlaceAdapter(this, new ArrayList<Places>());

                // Set the adapter on the {@link ListView}
                // so the list can be populated in the user interface
                placeListView.setAdapter(adapter);

        // Set an item click listener on the ListView, which sends an intent to a web browser
        // to open a website with more information about the selected place.
        placeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // Find the current earthquake that was clicked on
                Places currentPlace = adapter.getItem(position);
                String nameId = currentPlace.getId();
                /*The following method takes the place ID from the JSON response obtained from the above query and passes
                to the nearestplaceactivity to perform an other query to the link to the url
                 */
                send(nameId);
            }
        });



        // The following code gets the string value of the entered distance
        String distance = String.valueOf(rad);

        //The following code gets the values using intent. But for this purpose, I used shared preferences to get the details

        /*Intent intent = getIntent();
        String editText = intent.getStringExtra("message");
        String lat = intent.getStringExtra("long");
        String lon = intent.getStringExtra("lat");
        StringBuilder lat_long = new StringBuilder();
        lat_long.append(lon);
        lat_long.append(',');
        lat_long.append(lat);**/

        /*The following code gets access to the shared preferences and retrieves the values that were stored
        when the user hits the search button in the splash activity
        */

        SharedPreferences sharedpref = getSharedPreferences("userInformation", Context.MODE_PRIVATE);
        editText = sharedpref.getString("message", "");
        String lon = sharedpref.getString("longs", "");
        String lat = sharedpref.getString("lats", "");
        StringBuilder lat_long = new StringBuilder();
        lat_long.append(lat);
        lat_long.append(',');
        lat_long.append(lon);

        //The following code snippet builds the api once the query values are retrieved using the shared preferences code above

        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https")
                .authority("maps.googleapis.com")
                .appendPath("maps")
                .appendPath("api")
                .appendPath("place")
                .appendPath("textsearch")
                .appendPath("json")
                .appendQueryParameter("query", editText)
                .appendQueryParameter("location",lat_long.toString())
                //.appendQueryParameter("location",lat)
                .appendQueryParameter("radius", distance)
                .appendQueryParameter("key", BuildConfig.YOU_API_KEY)
                .appendQueryParameter("next_page_token", BuildConfig.YOU_API_TOKEN);
        USGS_REQUEST_URL = builder.build().toString();
        Log.e(LOG_TAG, USGS_REQUEST_URL);
        GetLocation.RequestAsyncTask task = new GetLocation.RequestAsyncTask();
        task.execute(USGS_REQUEST_URL);
        Log.e(LOG_TAG, "THIS METHOD IS CALLED 1");
    }

    /*The following code is linked to getsupport action bar. The code below defines the behavior of the back button on the action bar.
    In this case, an intent is being called to splash activity with an intent extra. I want to populate the edit text field
    with the text he entered before coming to this actvity. Hence, I am using an intent extra to send the received message back
    so that the user can see what he entered before hitting the search button.
     */

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(GetLocation.this, SplashActivity.class);
                intent.putExtra("sentback", editText);
                startActivity(intent);
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    /*
     * {@link AsyncTask} to perform the network request on a background thread, and then
     * update the UI with the first place in the response.
     */
   private class RequestAsyncTask extends AsyncTask<String, Void, List<Places>> {


        protected List<Places> doInBackground(String... urls) {
            // Don't perform the request if there are no URLs, or the first URL is null.
            if (urls.length < 1 || urls[0] == null) {
                return null;
            }

            List<Places> result = UtilsPlaceRadius.fetchPlaceData(urls[0]);
            return result;
        }


        @Override
        protected void onPostExecute(List<Places> data) {

            search = (ProgressBar) findViewById(R.id.bar);
            search.setVisibility(View.GONE);

            look = (ImageView) findViewById(R.id.looking);
            look.setVisibility(View.GONE);

            // Clear the adapter of previous earthquake data
            adapter.clear();

            // If there is a valid list of {@link Earthquake}s, then add them to the adapter's
            // data set. This will trigger the ListView to update.
            if (data != null && !data.isEmpty()) {
                adapter.addAll(data);

            } if(data==null){

                /*The following code sets an empty text view only when the list view is empty.
                The code automatically recognizes if the list view is empty
                 */

                placeListView.setEmptyView(findViewById(R.id.empty_text_view));
            }
        }
    }

      /*The following method takes the place ID from the JSON response obtained from the above query and passes
        to the nearestplaceactivity to perform an other query to the link to the url
       **/

    public void send(String id) {

        Intent intent = new Intent(this, NearestPlacesActivity.class);
        intent.putExtra("message", id);
        startActivity(intent);
        finish();

    }

    /*The code below defines the behavior of the back button on the device.
    In this case, an intent is being called to splash activity with an intent extra. I want to populate the edit text field
    with the text he entered before coming to this actvity. Hence, I am using an intent extra to send the received message back
    so that the user can see what he entered before hitting the search button.
    **/

    public void onBackPressed() {

        Intent i = new Intent(GetLocation.this, SplashActivity.class);
        i.putExtra("sentback", editText);
        startActivity(i);
    }

}