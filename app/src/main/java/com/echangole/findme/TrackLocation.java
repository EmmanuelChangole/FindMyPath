package com.echangole.findme;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.Toast;

public class TrackLocation implements LocationListener
{
    public static Location location;
    public static boolean isRunning=false;
    public static GPSTracker gpsTracker;

    public TrackLocation(Context context)
    {
        isRunning=true;
        LocationManager lm=(LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
        location=lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        try
        {
            location.setLatitude(location.getLatitude());
            location.setLongitude(location.getLongitude());
        } catch (NullPointerException e)
        {
            Toast.makeText(context, "Cannot get location", Toast.LENGTH_SHORT).show();
        }





       /* location.setLatitude(0);
        location.setLongitude(0);*/
    }

    @Override
    public void onLocationChanged(Location location)
    {
        this.location=location;
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras)
    {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
