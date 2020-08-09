package com.echangole.findme;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class AdapterItems
{
    public  String UserName;
    public  String PhoneNumber;
    //for news details
    AdapterItems(  String UserName,String PhoneNumber)
    {
        this. UserName=UserName;
        this. PhoneNumber=PhoneNumber;
    }


}
