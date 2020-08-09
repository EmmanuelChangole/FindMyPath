package com.echangole.findme;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPref
{
    SharedPreferences sharedPref;
    public SharedPref(Context context)
    {
      sharedPref=context.getSharedPreferences("MyRef",Context.MODE_PRIVATE);

    }
    public void saveData(String number) {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("number", number);
        editor.commit();
    }

    public String loadData()
    {
      return sharedPref.getString("number"," ");
    }

}
