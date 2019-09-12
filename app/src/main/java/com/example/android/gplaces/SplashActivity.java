/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.gplaces;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
This screen displays the primary screen in the app where the user can enter the query for the point of interest
 */
public class SplashActivity extends AppCompatActivity {

    private static final String LOG_TAG = MainActivity.class.getName();

    private static final int REQUEST_LOCATION = 1;
    LocationManager locationManager;
    String lattitude, longitude;
    EditText text;
    NetworkInfo networkInfo;
    SharedPreferences sharedprefs;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        //The following line of code identifies the edit text field where the user enters the query

        text = (EditText) findViewById(R.id.editText);

        // The following code takes care of hiding the key pad when the user hits the enter button on the soft key board

        text.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId,
                                          KeyEvent event) {
                if (event != null&& (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    in.hideSoftInputFromWindow(v.getWindowToken(),
                            InputMethodManager.HIDE_NOT_ALWAYS);
                    return true;

                }
                return false;
            }
        });

        /*The code below checks if the intent is null, i.e. it checks if the page is loaded for the first time
        or loaded because the user pressed the back button from the other activity. If the app is loaded and the
        user is coming back from the other activity then the edit text needs to be populated with what the user
        entered before leaving this activity
         **/


        if(getIntent().getExtras()!=null) {

            if (getIntent().getExtras().containsKey("sentback")) {
                String preview = getIntent().getExtras().getString("sentback");
                text.setText(preview);

            }
        }


        // Get a reference to the ConnectivityManager to check state of network connectivity
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        // Get details on the currently active default data network
        networkInfo = connMgr.getActiveNetworkInfo();

        // If there is a network connection, fetch data
        if (networkInfo != null && networkInfo.isConnected()) {

            TextView nointernet = (TextView) findViewById(R.id.nonet);
            nointernet.setVisibility(View.GONE);

        } else {

            TextView nointernet = (TextView) findViewById(R.id.nonet);
            nointernet.setVisibility(View.VISIBLE);
        }

        ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);


    }

    /*The following code is initiated when the user enters the text and hits the search button
    The code gets access to location manager and checks if the user allowed the location manager
    to access the current location. If yes, the get location method is triggered and the latitude
    and longitude is collected.
     */


    public void buttonClick2(View v) {

            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

            if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                buildAlertMessageNoGps();

            } else if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                getLocation();
            }
        }


    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(SplashActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission
                (SplashActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(SplashActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);

        } else {
            // if the location manager has the access, the following code gets the latitude and longitude
            Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

            if (location != null) {
                double latti = location.getLatitude();
                double longi = location.getLongitude();
                lattitude = String.valueOf(latti);
                longitude = String.valueOf(longi);
                Log.e(LOG_TAG, lattitude);
                //Once the latitude and longitude is retrieved, the sendDetails method is called
                sendDetails();

            } else {

                Toast.makeText(this, "Unable to Trace your location", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // The following code requests the user to turn on the location services

    protected void buildAlertMessageNoGps() {

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Please Turn ON your GPS Connection")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    /*
    Send details method send the latitude, longitude and the text entered in the edit text field once the
    user hits the search button. At this point I can send the information either by using an intent or by
    using shared preferences. In this scenario, I choose to go with shared preferences.
     */

    public void sendDetails() {

        /*text = (EditText) findViewById(R.id.editText);
        str = text.getText().toString();

        Intent intent = new Intent(this, GetLocation.class);
        intent.putExtra("message", str);
        intent.putExtra("lat", lattitude);
        intent.putExtra("long",longitude);
        startActivity(intent);
        finish();**/

        SharedPreferences sharedprefs = getSharedPreferences("userInformation", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedprefs.edit();
        editor.putString("message", text.getText().toString());
        editor.putString("lats", lattitude);
        editor.putString("longs", longitude);
        editor.apply();

        //The following converts the entered text into a string

        String enteredText = text.getText().toString();

        /*The following if clause checks if the user entered any text before hitting the search button.
        If the hits the search button with out entering data in the edit text field than a toast message
        id displayed with instructions to enter the data.
         */

        if (enteredText.matches("")) {
            Toast.makeText(this, "Please Type in Your Search", Toast.LENGTH_SHORT).show();
        } else {

            Intent intent = new Intent(SplashActivity.this, GetLocation.class);
            startActivity(intent);

        }
    }

/*
The following handles any touch even that is performed by the user any where on the screen. In the current scenario, once the
user taps any where on the screen, the soft key board disappears.
 */
    public boolean onTouchEvent(MotionEvent event) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.
                INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        return true;
    }

    /*
    The following code handles the instructions on what to do when the user hits the back button on the current activity.
    Based on the current code it goes to the home screen of the device.
     */

    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        startActivity(intent);
    }

}












