package com.echangole.findme.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.LocationListener;
import android.location.LocationManager;

import com.echangole.findme.GlobalInformation;
import com.echangole.findme.TrackLocation;

public class RunwithOs extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent)
    {
        if(intent.getAction().equalsIgnoreCase("android.intent.action.BOOT_COMPLETED"))
        {
            GlobalInformation globalInformation=new GlobalInformation(context);
            globalInformation.LoadData();
            if(!TrackLocation.isRunning)
            {
                LocationListener locationListener=new TrackLocation(context);
                LocationManager manager=(LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
                manager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,locationListener);

            }
            if(!MyService.isRunning)
            {
                Intent i=new Intent(context,MyService.class);
                context.startService(i);
            }

        }


    }
}
