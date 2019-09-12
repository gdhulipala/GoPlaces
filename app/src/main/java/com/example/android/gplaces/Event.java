package com.example.android.gplaces;

import android.support.v7.app.AppCompatActivity;

/**
 * Created by ganga on 8/18/17.
 */

public class Event extends AppCompatActivity{


    /** Location of the earthquake */
    private String mPlace;


    public Event(String name) {
        mPlace = name;

    }

    /**
     * Returns the location of the earthquake.
     */
    public String getmPlace() {
        return mPlace;
    }
}
