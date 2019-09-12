package com.example.android.gplaces;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ganga on 8/20/17.
 */

/*
The code below is called when the GetLocation activity code was executed to get the list of places.
Note: GetLocation activity gets the text from the edit text and the latitude, longitude information
from the splash activity
 */

public class UtilsPlaceRadius {
    /** Tag for the log messages */
    public static final String LOG_TAG = UtilsPlaceRadius.class.getSimpleName();


    public static List<Places> fetchPlaceData(String requestUrl) {
        // Create URL object
        URL url = createUrl(requestUrl);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error closing input stream", e);
        }

        // Extract relevant fields from the JSON response and create an {@link Event} object
        List<Places> names = extractFeatureFromJson(jsonResponse);

        // Return the {@link Event}
        return names;
    }

    /**
     * Returns new URL object from the given string URL.
     */
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error with creating URL ", e);

        }
        return url;
    }

    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the earthquake JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }


    private static List<Places> extractFeatureFromJson(String earthquakeJSON) {

        List<Places> names = new ArrayList<>();

        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(earthquakeJSON)) {
            return null;
        }

        try {
            JSONObject baseJsonResponse = new JSONObject(earthquakeJSON);
            JSONArray resultsArray = baseJsonResponse.getJSONArray("results");

            /*The if clause below checks if the results are empty? If empty, the results returned will be null.
            if the results are null, the extraction process quits and in the postexecute method the code enters data=null loop and a message is displayed saying
            no results found.
             */

            if(resultsArray.length()==0){

                return null;
            }
            // For each places in the placeArray, create a place object
            for (int i = 0; i < resultsArray.length(); i++) {

                // Get a single place at position i within the list of places
                JSONObject objName = resultsArray.getJSONObject(i);

                /*
                As most of the places have a name and address, the code below is only checking if there is a rating available for a given places.
                Some places like city names, may not have a rating. In those instances, the code below enters the else loop and gives a rating of 0 by default.
                If the rating is available,then it enters the if loop and gets all the information needed.
                 */

                if (objName.has("rating")) {

                    // Extract the value for the key called "place"
                    String placeName = objName.getString("name");
                    String placeId = objName.getString("place_id");
                    double rating = objName.getDouble("rating");
                    String address = objName.getString("formatted_address");

                    // The following constructor makes place objects

                    Places place = new Places(placeName, placeId, address, rating);

                    names.add(place);

                } else {
                    // Extract the value for the key called "place"
                    String placeName = objName.getString("name");
                    String placeId = objName.getString("place_id");
                    String address = objName.getString("formatted_address");

                    Places place = new Places(placeName, placeId, address, 0);

                    names.add(place);

                }
            }

        } catch (JSONException e) {
            Log.e(LOG_TAG, "Problem parsing the earthquake JSON results", e);
        }
        return names;
    }
}
