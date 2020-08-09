package com.echangole.findme.utils;

import android.app.IntentService;
import android.content.Intent;
import android.location.Location;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.echangole.findme.SharedPref;
import com.echangole.findme.TrackLocation;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MyService extends IntentService
{
    public static boolean isRunning=false;
    DatabaseReference databaseReference;


    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     *
     */
    public MyService() {
        super("MyService");
        isRunning=true;
        databaseReference= FirebaseDatabase.getInstance().getReference();

    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent)
    {
        final SharedPref sharedPref=new SharedPref(getApplicationContext());
        databaseReference.child("Users").child(sharedPref.loadData()).child("Updates").
                addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                    {
                        Date date=new Date();
                        DateFormat dateFormat=new SimpleDateFormat("yyyy/mm/dd hh:mm:ss");
                        try{
                            Location location= TrackLocation.location;
                            databaseReference.child("Users").child(sharedPref.loadData()).
                                    child("Location").child("lat").setValue(TrackLocation.location.getLatitude());
                            databaseReference.child("Users").child(sharedPref.loadData()).
                                    child("Location").child("log").setValue(TrackLocation.location.getLongitude());
                            databaseReference.child("Users").child(sharedPref.loadData()).
                                    child("Location").child("LastOnline").setValue(dateFormat.format(date).toString());

                        }catch (NullPointerException e)
                        {
                            Toast.makeText(getApplicationContext(), "Cannot get location", Toast.LENGTH_SHORT).show();
                        }




                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

    }
}
