package com.informerly.informer;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.content.SharedPreferences;

public class Splashscreen extends ActionBarActivity {

    SharedPreferences sharedpreferences;
    String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splashscreen);
        sharedpreferences = getSharedPreferences("user_session", Context.MODE_PRIVATE);
        token = sharedpreferences.getString("token", null);

        int myTimer = 2000;
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {

                if(token == null) {
                    Intent i = new Intent(Splashscreen.this, LogIn.class);
                    startActivity(i);
                } else {
                    String userid = sharedpreferences.getString("userid", null);
                    String email = sharedpreferences.getString("useremail", null);
                    Intent i = new Intent(Splashscreen.this, FeedView.class);
                    i.putExtra("Token",token);
                    i.putExtra("id",userid);
                    i.putExtra("useremail", email);
                    startActivity(i);
                }

                finish(); // close this activity
            }
        }, myTimer);
    }
}
