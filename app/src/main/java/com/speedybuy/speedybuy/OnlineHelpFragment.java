package com.speedybuy.speedybuy;

import android.os.Bundle;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;


public class OnlineHelpFragment extends Fragment {



    public OnlineHelpFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_online_help, container, false);

        //initialising the layout
        TextView mobile = (TextView) rootView.findViewById(R.id.mobile);
        //TextView google = (TextView) rootView.findViewById(R.id.google);
        TextView email = (TextView) rootView.findViewById(R.id.email);
        // setting the email
        email.setText("sbspeedybuy@gmail.com");

        // setting the mobile number
        mobile.setText("+917089164460");

        // setting the web url to visit
        //google.setText("www.google.com");

        // linking all type of text
        Linkify.addLinks(email, Linkify.ALL);
        Linkify.addLinks(mobile, Linkify.ALL);
        //Linkify.addLinks(google, Linkify.ALL);

        return rootView;
    }
}
