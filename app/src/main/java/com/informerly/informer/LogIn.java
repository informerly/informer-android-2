package com.informerly.informer;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.view.inputmethod.InputMethodManager;

import com.parse.ParseInstallation;

import org.apache.http.HttpEntity;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import com.informerly.informer.APICalls.HttpLogin;

import com.parse.ParsePush;
import com.parse.SaveCallback;


public class LogIn extends ActionBarActivity {
    EditText emailId;
    EditText pasword;
    ProgressBar baar;
    String check ;
    TextView signn,forget;
    JSONObject person;
    String token,json;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        emailId = (EditText)findViewById(R.id.emails);
        pasword = (EditText)findViewById(R.id.passwords);
        signn = (TextView)findViewById(R.id.buttonSign);
        baar = (ProgressBar) findViewById(R.id.bar);
        forget = (TextView)findViewById(R.id.forgot);
        forget.setOnTouchListener(new View.OnTouchListener() {
            float startX;
            float startY;
            @Override
            public boolean onTouch(View v, MotionEvent event) {


                int action = MotionEventCompat.getActionMasked(event);

                switch (action) {
                    case (MotionEvent.ACTION_DOWN):
                        forget.setTextColor(Color.parseColor("#ff185da2"));
                        startX = event.getX();
                        startY = event.getY();
                        break;
                    case (MotionEvent.ACTION_UP):
                        forget.setTextColor(Color.parseColor("#ff479cfa"));
                        float endX = event.getX();
                        float endY = event.getY();
                        if (isAClick(startX, endX, startY, endY)) {
                            Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("http://informerly.com/users/password/new"));
                            startActivity(i);
                        }
                        break;
                }
                return false;
            }
        });
        signn.setOnTouchListener(new View.OnTouchListener() {
            float startX;
            float startY;
            @Override
            public boolean onTouch(View v, MotionEvent event) {


                int action = MotionEventCompat.getActionMasked(event);

                switch (action) {
                    case (MotionEvent.ACTION_DOWN):
                        signn.setTextColor(Color.parseColor("#ff2b85ff"));
                        startX = event.getX();
                        startY = event.getY();
                        break;
                    case (MotionEvent.ACTION_UP):
                        signn.setTextColor(Color.parseColor("#ffffffff"));
                        float endX = event.getX();
                        float endY = event.getY();
                        if (isAClick(startX, endX, startY, endY)) {
                        boolean iff = isNetworkAvailable();
                        if(iff) {
                            hideKeyboard();
                            signn.setEnabled(false);
                            emailId.setEnabled(false);
                            pasword.setEnabled(false);
                            forget.setEnabled(false);
                            new makeLogin().execute("");
                        }
                        else
                        {
                            pasword.setText("");
                            hideKeyboard();
                            Toast.makeText(LogIn.this,"No Internet Connection",Toast.LENGTH_LONG).show();
                        }
                    }
                        break;
                }
                return false;
            }
        });
    }
    private boolean isAClick(float startX, float endX, float startY, float endY) {
        float differenceX = Math.abs(startX - endX);
        float differenceY = Math.abs(startY - endY);
        if (differenceX > 6 || differenceY > 6) {
            return false;
        }
        return true;
    }
    @Override
    public void onBackPressed() {
        System.exit(1);
    }
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    void  login(){
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        try {
            HttpEntity resEntity = new HttpLogin(emailId,pasword).getEntity();
            if (resEntity != null) {
                json = EntityUtils.toString(resEntity);
                try {
                    person = (new JSONObject(json));
                    check  = person.getString("success");
                }
                catch (Exception e)
                {

                }

            }
        } catch (Exception e) {
        }

    }
    public void showAlert(){
    Toast.makeText(LogIn.this,"Wrong Credential info",Toast.LENGTH_LONG).show();
        signn.setEnabled(true);
        emailId.setEnabled(true);
        pasword.setEnabled(true);
        forget.setEnabled(true);
        baar.setVisibility(View.GONE);
        hideKeyboard();
                        }

    public void signIn(View v){
        boolean iff = isNetworkAvailable();
        if(iff) {
            hideKeyboard();
            new makeLogin().execute("");
        }
        else
        {
            pasword.setText("");
            hideKeyboard();
            Toast.makeText(this,"No Internet Connection",Toast.LENGTH_LONG).show();
        }
        //login();

    }

    public void forgotPass(View v)
    {
        Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("http://informerly.com/users/password/new"));
        startActivity(i);
    }
    public void hideMe(View v)
    {
        hideKeyboard();
    }
    private void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager inputManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    SharedPreferences sharedpreferences;

    private class makeLogin extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            login();
            return "Executed";
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                if (check.equals("true")) {
                    token = person.getString("auth_token");
                    JSONObject user = (new JSONObject(json)).getJSONObject("user");
                    String id = user.getString("id");
                    //dilog.cancel();
                    Intent i = new Intent(LogIn.this, FeedView.class);
                    i.putExtra("Token",token);
                    i.putExtra("id",id);
                    String useremail = emailId.getText().toString();
                    i.putExtra("useremail", useremail);
                    baar.setVisibility(View.GONE);

                    sharedpreferences = getSharedPreferences("user_session", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    editor.putString("token", token);
                    editor.putString("userid", id);
                    editor.putString("useremail", useremail);
                    editor.commit();

                    ParseInstallation deviceParseInstallation = ParseInstallation.getCurrentInstallation();
                    deviceParseInstallation.put("username", useremail);
                    deviceParseInstallation.saveInBackground();

                    ParsePush.subscribeInBackground("user_"+id, new SaveCallback() {
                        @Override
                        public void done(com.parse.ParseException e) {
                        if (e == null) {
                            Log.d("com.parse.push", "Successfully subscribed to your own user channel.");
                        } else {
                            Log.e("com.parse.push", "Failed to subscribe for push for own Channel " + e.getMessage(), e.getCause());
                        }
                        }
                    });

                    startActivity(i);
                    emailId.setText("");
                    pasword.setText("");
                    signn.setEnabled(true);
                    emailId.setEnabled(true);
                    pasword.setEnabled(true);
                    forget.setEnabled(true);
                } else {
                    showAlert();
                    pasword.setText("");
                }
            }
            catch (Exception r)
            {
                r.printStackTrace();
            }

        }

        @Override
        protected void onPreExecute() {
            baar.setVisibility(View.VISIBLE);
        }
    }
}
