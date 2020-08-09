package com.echangole.findme;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.echangole.findme.utils.NetworkCheck;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.hbb20.CountryCodePicker;

public class Login extends AppCompatActivity{
    private String TAG = "ActivityRegister";
    private EditText edPhoneNumber;
    private Button butLogin;
    private CountryCodePicker countryCodePicker;
    private FirebaseAuth mAuth;
    private View contentView;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Log.i(TAG, "onCreate: ");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();
        intWidgets();
        checkNetwork();

    }

    private void checkNetwork()
    {
        NetworkCheck net = new NetworkCheck();

        if (net.isNetworkAvailable(Login.this))
        {
            getItems();

        } else
        {
            Snackbar snackbar=Snackbar.make(contentView,"No network! please enable your data network to continue",Snackbar.LENGTH_LONG);
            snackbar.show();
            butLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    checkNetwork();
                }
            });
        }

    }

    private void getItems()
    {
        butLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getNumber();
            }
        });

    }

    private void getNumber()
    {
        String num2;
        char charNum[]=new char[9];
        StringBuilder builder=new StringBuilder();
        String num = edPhoneNumber.getText().toString();
        if (num.isEmpty() || num.length() != 10)
        {
            edPhoneNumber.setError(getString(R.string.edNumber_error));
            edPhoneNumber.requestFocus();
            return;
        }
          num2=num;
        for(int i=1;i<num.length();i++)
            charNum[i-1]=num.charAt(i);
           num=null;
         for(int j=0;j<charNum.length;j++)
             builder.append(charNum[j]);

         num=builder.toString();

         String number = "+" + countryCodePicker.getSelectedCountryCodeAsInt() + num;
         Intent intent = new Intent(this, ActivityOtp.class);
        intent.putExtra(getString(R.string.intent_number), number);
        intent.putExtra("num2",num2);
        startActivity(intent);

    }

    private void intWidgets()
    {
        edPhoneNumber=(EditText)findViewById(R.id.EDTNumber);
        butLogin=(Button)findViewById(R.id.buNext);
        countryCodePicker = (CountryCodePicker) findViewById(R.id.countryCodeHolder);
        contentView=(View)findViewById(R.id.contentView);

    }



}