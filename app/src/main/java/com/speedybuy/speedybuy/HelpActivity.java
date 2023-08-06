package com.speedybuy.speedybuy;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.text.util.Linkify;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class HelpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        //initialising the layout
        TextView mobile = (TextView) findViewById(R.id.mobile);
      //  TextView google = (TextView) findViewById(R.id.google);
        TextView email = (TextView) findViewById(R.id.email);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        // setting the email
        email.setText("sbspeedybuy@gmail.com");
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        // setting the mobile number
        mobile.setText("+917089164460");

        // setting the web url to visit
        //google.setText("www.google.com");

        // linking all type of text
        Linkify.addLinks(email, Linkify.ALL);
        Linkify.addLinks(mobile, Linkify.ALL);
       // Linkify.addLinks(google, Linkify.ALL);
    }
}