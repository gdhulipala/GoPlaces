package com.example.android.gplaces;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.List;

/**
 * Created by ganga on 8/17/17.
 */

public class PlaceAdapter extends ArrayAdapter<Places> {

    /**
     * The part of the location string from the USGS service that we use to determine
     * whether or not there is a location offset present ("5km N of Cairo, Egypt").
     */
    private static final String LOCATION_SEPARATOR = " of ";
    private static final String LOG_TAG = PlaceAdapter.class.getName();


    public PlaceAdapter(Context context, List<Places> earthquakes) {
        super(context, 0, earthquakes);
    }

    /**
     * Returns a list item view that displays information about the earthquake at the given position
     * in the list of earthquakes.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Check if there is an existing list item view (called convertView) that we can reuse,
        // otherwise, if convertView is null, then inflate a new list item layout.
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.place_list_item, parent, false);
        }

        // Find the earthquake at the given position in the list of earthquakes
        Places currentplace = getItem(position);

        // Find the TextView with view ID magnitude
        TextView magnitudeView = (TextView) listItemView.findViewById(R.id.location_offset);
        // Format the magnitude to show 1 decimal place
        String place = currentplace.getPlacename();
        // Display the magnitude of the current earthquake in that TextView
        magnitudeView.setText(place);
        Log.e(LOG_TAG, "THIS METHOD IS CALLED 6");

        TextView locationaddress = (TextView) listItemView.findViewById(R.id.location_address);
        String address = currentplace.getmAddress();
        locationaddress.setText(address);

        // Find the TextView with view ID magnitude
        TextView magnitude = (TextView) listItemView.findViewById(R.id.rating);
        // Format the magnitude to show 1 decimal place

        double checkRating = currentplace.getMrating();

        if(checkRating<1){
            magnitude.setText("O");
        }else{
            String formattedMagnitude = formatMagnitude(currentplace.getMrating());

            // Display the magnitude of the current earthquake in that TextView
            magnitude.setText(formattedMagnitude);
        }
            // Set the proper background color on the magnitude circle.
            // Fetch the background from the TextView, which is a GradientDrawable.
            GradientDrawable magnitudeCircle = (GradientDrawable) magnitude.getBackground();
            // Get the appropriate background color based on the current earthquake magnitude
            int magnitudeColor = getMagnitudeColor(currentplace.getMrating());

            // Set the color on the magnitude circle
            magnitudeCircle.setColor(magnitudeColor);

           // Return the list item view that is now showing the appropriate data
           return listItemView;
    }

    private int getMagnitudeColor(double magnitude) {
        int magnitudeColorResourceId;
        int magnitudeFloor = (int) Math.floor(magnitude);
        switch (magnitudeFloor) {
            case 0:
            case 1:
                magnitudeColorResourceId = R.color.magnitude1;
                break;
            case 2:
                magnitudeColorResourceId = R.color.magnitude2;
                break;
            case 3:
                magnitudeColorResourceId = R.color.magnitude3;
                break;
            case 4:
                magnitudeColorResourceId = R.color.magnitude4;
                break;
            case 5:
                magnitudeColorResourceId = R.color.magnitude5;
                break;
            case 6:
                magnitudeColorResourceId = R.color.magnitude6;
                break;
            case 7:
                magnitudeColorResourceId = R.color.magnitude7;
                break;
            case 8:
                magnitudeColorResourceId = R.color.magnitude8;
                break;
            case 9:
                magnitudeColorResourceId = R.color.magnitude9;
                break;
            default:
                magnitudeColorResourceId = R.color.magnitude10plus;
                break;
        }

        return ContextCompat.getColor(getContext(), magnitudeColorResourceId);
    }

    /**
     * Return the formatted magnitude string showing 1 decimal place (i.e. "3.2")
     * from a decimal magnitude value.
     */
    private String formatMagnitude(double magnitude) {
        DecimalFormat magnitudeFormat = new DecimalFormat("0.0");
        return magnitudeFormat.format(magnitude);
    }
}
