package com.echangole.findme;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.echangole.findme.utils.FirebaseMethod;
import com.echangole.findme.utils.MyService;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_ASK_PERMISSIONS =101 ;
    private FirebaseMethod fireBaseMethods;
    DatabaseReference databaseReference;
    private ArrayList<AdapterItems> listnewsData = new ArrayList<AdapterItems>();
    MyCustomAdapter myadapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        intFireBase();
        databaseReference= FirebaseDatabase.getInstance().getReference();
        GlobalInformation globalInformation=new GlobalInformation(this);
        globalInformation.LoadData();
        CheckUserPermsions();
        myadapter=new MyCustomAdapter(listnewsData);

        ListView lsNews=(ListView)findViewById(R.id.listView);
        lsNews.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AdapterItems adapterItems=listnewsData.get(position);
                GlobalInformation.updateInfo(adapterItems.PhoneNumber);
                Intent intent= new Intent(getApplicationContext(),MapsActivity.class);
                intent.putExtra("PhoneNumber",adapterItems.PhoneNumber);
                startActivity(intent);
            }
        });
        lsNews.setAdapter(myadapter);




       // Refresh();
    }

    private void updateLocation()
    {
        final SharedPref sharedPref=new SharedPref(this);
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

    @Override
    protected void onResume() {
        super.onResume();
        Refresh();
    }

    private void Refresh()
    {
        listnewsData.clear();
        /*for (Map.Entry  m:GlobalInformation.myTracker.entrySet()){
            listnewsData.add(new AdapterItems(  m.getValue().toString() , m.getKey().toString()));
        }*/
        SharedPref sharedPref=new SharedPref(this);
        databaseReference.child("Users").child(sharedPref.loadData()).
                child("Finders").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Map<String, Object> td = (HashMap<String, Object>) dataSnapshot.getValue();

                listnewsData.clear();
                if (td == null)  //no one allow you to find him
                {
                    listnewsData.add(new AdapterItems("NoTicket", "no_desc"));
                    myadapter.notifyDataSetChanged();
                    return;
                }
                // List<Object> values = td.values();


                // get all contact to list
                ArrayList<AdapterItems> list_contact = new ArrayList<AdapterItems>();
                Cursor cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
                while (cursor.moveToNext()) {
                    String name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                    String phoneNumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    list_contact.add(new AdapterItems(  name,GlobalInformation.FormatPhoneNumber(phoneNumber)
                    ));


                }


// if the name is save chane his text
                // case who find me
                String tinfo;
                for (  String Numbers : td.keySet()) {
                    for (AdapterItems cs : list_contact) {

                        //IsFound = SettingSaved.WhoIFindIN.get(cs.Detals);  // for case who i could find list
                        if (cs.PhoneNumber.length() > 0)
                            if (Numbers.contains(cs.PhoneNumber)) {
                                listnewsData.add(new AdapterItems(cs.UserName, cs.PhoneNumber));
                                break;
                            }

                    }

                }
                myadapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                // Log.w(TAG, "Failed to read value.", error.toException());
            }
        });




        myadapter.notifyDataSetChanged();

    }

    private void intFireBase()
    {
        fireBaseMethods = new FirebaseMethod(this);
        fireBaseMethods.initFirebase();
    }
    public void onStart() {
        super.onStart();
        fireBaseMethods.onChangeState();

    }

    protected void onStop() {
        super.onStop();
        fireBaseMethods.clearState();


    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.addtracker:
                Intent intent=new Intent(this,MyTracker.class);
                startActivity(intent);
                return true;
            case R.id.help:
                CheckUserPermsions();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    void CheckUserPermsions(){
        if ( Build.VERSION.SDK_INT >= 23){
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                    PackageManager.PERMISSION_GRANTED  ){
                requestPermissions(new String[]{
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                        },
                        REQUEST_CODE_ASK_PERMISSIONS);
                return ;
            }
        }
        updateLocation();
        startServices();


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    updateLocation();
                    startServices();
                } else {
                    // Permission Denied
                    Toast.makeText( this,"your message" , Toast.LENGTH_SHORT)
                            .show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    void startServices()
    {
        if(!TrackLocation.isRunning)
        {
            LocationListener locationListener=new TrackLocation(getApplicationContext());
            LocationManager manager=(LocationManager)getSystemService(Context.LOCATION_SERVICE);
            manager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,locationListener);

        }
        if(!MyService.isRunning)
        {
            Intent intent=new Intent(this,MyService.class);
            startService(intent);
        }
    }
    private class MyCustomAdapter extends BaseAdapter {
        public ArrayList<AdapterItems> listnewsDataAdpater ;

        public MyCustomAdapter(ArrayList<AdapterItems>  listnewsDataAdpater) {
            this.listnewsDataAdpater=listnewsDataAdpater;
        }


        @Override
        public int getCount() {
            return listnewsDataAdpater.size();
        }

        @Override
        public String getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            LayoutInflater mInflater = getLayoutInflater();
            /*View myView = mInflater.inflate(R.layout.single_row_conact, null);*/
            final   AdapterItems s = listnewsDataAdpater.get(position);
            if (s.UserName.equals("NoTicket")) {
                View myView = mInflater.inflate(R.layout.news_ticket_no_news, null);

                return myView;

            } else {
                View myView = mInflater.inflate(R.layout.single_row_conact, null);

                TextView tv_user_name = (TextView) myView.findViewById(R.id.tv_user_name);
                tv_user_name.setText(s.UserName);
                TextView tv_phone = (TextView) myView.findViewById(R.id.tv_phone);
                tv_phone.setText(s.PhoneNumber);

                return myView;
            }


        }

    }

}