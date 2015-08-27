package com.informerly.informer.PushNotification;

import android.app.Application;
import android.util.Log;

import com.parse.Parse;
import com.parse.ParsePush;
import com.parse.SaveCallback;

public class Push extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Parse.initialize(this, "cZuNXGv2vSezrMNI2aHniKwxn2SStYJjOVQCwtgG", "unn7iH2MUeB5G9IErfiYSp5q1KWIc3SbiuFnJa4t");
        ParsePush.subscribeInBackground("", new SaveCallback() {
            @Override
            public void done(com.parse.ParseException e) {
            if (e == null) {
                Log.d("com.parse.push", "successfully subscribed to the broadcast channel.");
            } else {
                Log.e("com.parse.push", "failed to subscribe for push for ", e.getCause());
            }
            }
        });

    }
}

