package com.echangole.findme;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Map;

public class MyTracker extends AppCompatActivity
{
    private FirebaseMethod fireBaseMethods;
    private ArrayList<AdapterItems>  listnewsData = new ArrayList<AdapterItems>();
    final private int REQUEST_CODE_ASK_PERMISSIONS = 123;
    private MyCustomAdapter myadapter;
    private static final int PICK_CONTACT=1;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_tracker);
        intFireBase();
        myadapter=new MyCustomAdapter(listnewsData);
        ListView lsNews=(ListView)findViewById(R.id.listView);
        /*listnewsData.add(new AdapterItems("Manu"," +254702073189"));*/


        lsNews.setAdapter(myadapter);
        lsNews.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                GlobalInformation.myTracker.remove(listnewsData.get(position).PhoneNumber);
                DatabaseReference mDatabase= FirebaseDatabase.getInstance().getReference();
                mDatabase.child("Users").child(listnewsData.get(position).PhoneNumber).child("Finders")
                        .child(GlobalInformation.phoneNumber).removeValue();
                GlobalInformation globalInfo= new GlobalInformation(getApplicationContext());
                globalInfo.SaveData();
                Refresh();
            }
        });
        Refresh();

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_contact_list, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.goback:
                GlobalInformation globalInfo= new GlobalInformation(this);
                globalInfo.SaveData();
                Intent intent=new Intent(this,MainActivity.class);
                startActivity(intent);
                return true;
            case R.id.add:
                CheckUserPermsions();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
    void CheckUserPermsions(){
        if ( Build.VERSION.SDK_INT >= 23){
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) !=
                    PackageManager.PERMISSION_GRANTED  ){
                requestPermissions(new String[]{
                                Manifest.permission.READ_CONTACTS},
                        REQUEST_CODE_ASK_PERMISSIONS);
                return ;
            }
        }

        PickContact();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    PickContact();
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

    void PickContact(){
        Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        startActivityForResult(intent, PICK_CONTACT);
    }

    @Override
    public void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);

        switch (reqCode) {
            case (PICK_CONTACT) :
                if (resultCode == Activity.RESULT_OK) {

                    Uri contactData = data.getData();
                    Cursor c =  getContentResolver().query(contactData, null, null, null, null);
                    if (c.moveToFirst()) {
                        String id =c.getString(c.getColumnIndexOrThrow(ContactsContract.Contacts._ID));
                        String hasPhone =c.getString(c.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
                        String cNumber="No number";
                        if (hasPhone.equalsIgnoreCase("1")) {
                            Cursor phones = getContentResolver().query(
                                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,
                                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = "+ id,
                                    null, null);
                            phones.moveToFirst();
                            cNumber =GlobalInformation.FormatPhoneNumber (phones.getString(phones.getColumnIndex("data1")));
                            System.out.println("number is:"+cNumber);
                        }
                        String name = c.getString(c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

                        GlobalInformation.myTracker.put(cNumber,name);
                        SharedPref pref=new SharedPref(this);
                        DatabaseReference mDatabase= FirebaseDatabase.getInstance().getReference();
                        mDatabase.child("Users").child(cNumber).child("Finders")
                                .child(pref.loadData()).setValue(true);
                        GlobalInformation globalInfo= new GlobalInformation(this);
                        globalInfo.SaveData();
                        Refresh();
                        //update firebase and
                        //update list
                        //update database
                    }
                }
                break;
        }
    }

    private void Refresh()
    {
        listnewsData.clear();
        for (Map.Entry  m:GlobalInformation.myTracker.entrySet()){
            listnewsData.add(new AdapterItems(  m.getValue().toString() , m.getKey().toString()));
        }
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
            View myView = mInflater.inflate(R.layout.single_row_conact, null);

            final   AdapterItems s = listnewsDataAdpater.get(position);

            TextView tv_user_name=( TextView)myView.findViewById(R.id.tv_user_name);
            tv_user_name.setText(s.UserName);
            TextView tv_phone=( TextView)myView.findViewById(R.id.tv_phone);
            tv_phone.setText(s.PhoneNumber);

            return myView;
        }

    }
}