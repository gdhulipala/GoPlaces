package com.example.android.gplaces;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

/*
This activity is called when the user click on a certain item on the list view. Behind the scenes, once the user clicks
on a particular item on the list, the place ID associated with that list item is sent from GetLocation activity to nearest
place activity using an Intent.
 */

public class NearestPlacesActivity extends AppCompatActivity {


    String USGS_REQUEST_URL = "https://maps.googleapis.com";
    private static final String LOG_TAG = NearestPlacesActivity.class.getName();
    String result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_details);

            // As soon as the activity is launched the UI is set to the same one as getlocation
            Intent intents = new Intent(NearestPlacesActivity.this, GetLocation.class);
            startActivity(intents);

            // Below is the code that gets the information about the place ID of a particular list item that the user clicked
            Intent intent = getIntent();
            String editText = intent.getStringExtra("message");


            // Below is the code that builds the uri, based on the place ID that is retrieved from the above code
            Uri.Builder builder = new Uri.Builder();
            builder.scheme("https")
                    .authority("maps.googleapis.com")
                    .appendPath("maps")
                    .appendPath("api")
                    .appendPath("place")
                    .appendPath("details")
                    .appendPath("json")
                    .appendQueryParameter("placeid", editText)
                    .appendQueryParameter("key", BuildConfig.YOU_API_KEYS);
            USGS_REQUEST_URL = builder.build().toString();
            Log.e(LOG_TAG, USGS_REQUEST_URL);
            NearestPlacesActivity.PlacedetailsAsyncTask task = new NearestPlacesActivity.PlacedetailsAsyncTask();
            task.execute(USGS_REQUEST_URL);
            Log.e(LOG_TAG, "THIS METHOD IS CALLED 1");

        }

        /**
         * {@link AsyncTask} to perform the network request on a background thread, and then
         * update the UI with the first earthquake in the response.
         */
        private class PlacedetailsAsyncTask extends AsyncTask<String, Void, String> {


            protected String doInBackground(String... urls) {

                Log.e(LOG_TAG, "THIS METHOD IS CALLED 2");
                // Don't perform the request if there are no URLs, or the first URL is null.
                if (urls.length < 1 || urls[0] == null) {
                    return null;
                }
                Log.e(LOG_TAG, "Do in the background");
                result = UtilsPlaceDetails.fetchPlaceData(urls[0]);
                Log.e(LOG_TAG, "Do in the background finished");
                return result;

            }

            protected void onPostExecute(String data) {

                View loadingIndicator = findViewById(R.id.loading_indicator);
                loadingIndicator.setVisibility(View.VISIBLE);

                // Convert the String URL into a URI object (to pass into the Intent constructor)
                Uri earthquakeUri = Uri.parse(data);

                // Create a new intent to view the earthquake URI
                Intent websiteIntent = new Intent(Intent.ACTION_VIEW, earthquakeUri);

                // Send the intent to launch a new activity
                startActivity(websiteIntent);

            }


        }

    //This code handles the behaviour of the back button in the current activity

    public void onBackPressed() {

        Intent intent = new Intent(NearestPlacesActivity.this, GetLocation.class);
        startActivity(intent);

    }
    }

