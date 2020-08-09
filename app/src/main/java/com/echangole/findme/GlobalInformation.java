package com.echangole.findme;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class GlobalInformation
{
    public static String phoneNumber="";
    public static Map<String, String> myTracker=new HashMap<>();
    private Context context;
    private SharedPreferences ShredRef;

    public static void updateInfo(String phoneNumber)
    {
        Date date=new Date();
        DateFormat dateFormat=new SimpleDateFormat("yyyy/mm/dd hh:mm:ss");
        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference();
        databaseReference.child("Users").child(phoneNumber).child("Updates").setValue(dateFormat.format(date).toString());
    }

    public static String FormatPhoneNumber(String Oldnmber){
        try{
            String numberOnly= Oldnmber.replaceAll("[^0-9]", "");
            if(Oldnmber.charAt(0)=='+') numberOnly="+" +numberOnly ;
            if (numberOnly.length()>=10)
                numberOnly=numberOnly.substring(numberOnly.length()-10,numberOnly.length());
            return(numberOnly);
        }
        catch (Exception ex){
            return(" ");
        }
    }


    public  GlobalInformation(Context context){
        this.context=context;
        ShredRef=context.getSharedPreferences("myRef",Context.MODE_PRIVATE);
    }

    void SaveData(){
        String MyTrackersList="" ;
        for (Map.Entry  m:GlobalInformation.myTracker.entrySet()){
            if (MyTrackersList.length()==0)
                MyTrackersList=m.getKey() + "%" + m.getValue();
            else
                MyTrackersList+= "%"+m.getKey() +"%" + m.getValue();
        }

        if (MyTrackersList.length()==0)
            MyTrackersList="empty";
        SharedPreferences.Editor editor=ShredRef.edit();
        editor.putString("MyTrackers",MyTrackersList);
        editor.commit();
    }

    public void LoadData(){
        myTracker.clear();
        String MyTrackersList= ShredRef.getString("MyTrackers","empty");
        if (!MyTrackersList.equals("empty")){
            String[] users=MyTrackersList.split("%");
            for (int i=0;i<users.length;i=i+2){
                myTracker.put(users[i],users[i+1]);
            }
        }




    }



}
