package com.example.android.gplaces;

/**
 * Created by ganga on 8/17/17.
 */

public class Places {


    /** Magnitude of the earthquake */
    private String mName;

    /** Location of the earthquake */
    private String mPlace;

    private String mAddress;

    private double mrating;


    public Places(String name, String id, String address, double rating) {
        mName = name;
        mPlace = id;
        mAddress = address;
        mrating = rating;

    }

    /**
     * Returns the magnitude of the earthquake.
     */
    public String getPlacename() {
        return mName;
    }

    /**
     * Returns the location of the earthquake.
     */
    public String getId() {
        return mPlace;
    }

    public String getmAddress() {
        return mAddress;
    }

    public double getMrating() {
        return mrating;
    }

}
