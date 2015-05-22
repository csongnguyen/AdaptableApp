package com.adaptableandroid;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;

import java.util.List;
import java.util.Locale;

/**
 * Created by Connie on 5/22/2015.
 */
public class LocationUtils {
    public static Address getAddress(Context context, double latitude, double longitude){
//        StringBuilder result = new StringBuilder();
        try {
            Geocoder geocoder = new Geocoder(context, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if(addresses.size() > 0){
//                System.out.println("Address: " + addresses.get(0).getLocality() + ", zip: " + addresses.get(0).getPostalCode());
                return addresses.get(0);
//                result.append(address.getLocality()).append("\n");     // city
//                result.append(address.getSubAdminArea()).append("\n"); // null
//                result.append(address.getPostalCode()).append("\n");   // zip code
//                result.append(address.getAdminArea()).append("\n");    // state
//                result.append(address.getCountryName());               // country
            }
        } catch(Exception e){
            Log.e("address tag", e.getMessage());
        }
        return null;
//        return result.toString();
    }

    public static Location getLastKnownLocation(LocationManager mLocationManager) {
        List<String> providers = mLocationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            Location l = mLocationManager.getLastKnownLocation(provider);
            if (l == null) {
                continue;
            }
            if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                // Found best last known location: %s", l);
                bestLocation = l;
            }
        }
        return bestLocation;
    }
}
