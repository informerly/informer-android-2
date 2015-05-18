package com.informerly.informer;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;

import com.informerly.informer.APICalls.HttpGetArticle;
import com.informerly.informer.APICalls.HttpLogout;

import com.informerly.informer.R;

public class FeedView extends Activity {
    String token, id, json, check,useremail;
    DrawerLayout drawer, drawe;
    ListView listt;
    ProgressBar baar;
    GridLayout gridlayout;
    Button send;
    SwipeRefreshLayout refresher;
    private ListView mainListView;
    private ArrayAdapter<String> listAdapters;
    JSONObject logout;
    TextView helpt, mail, contacts;
    ArrayList<String> source,feedid,title,time,color,url,isread,slug,shortlink,feedbookmark,description;
    EditText feedsend;
    RelativeLayout menu, men;
    private CharSequence mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_view);
        Intent intent = getIntent();
        listt = (ListView)findViewById(R.id.list);
        listt.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,int position, long iid) {
                Intent i = new Intent(FeedView.this, ArticleView.class);
                i.putExtra("Uurl",url.get(position));
                i.putExtra("Ttitle",title.get(position));
                i.putExtra("feedid",feedid.get(position));
                i.putExtra("token",token);
                i.putExtra("userid",id);
                startActivity(i);
            }
        });
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        baar = (ProgressBar) findViewById(R.id.bar);
        menu = (RelativeLayout) findViewById(R.id.list_slidermenu);
        drawe = (DrawerLayout) findViewById(R.id.drawer_layou);
        men = (RelativeLayout) findViewById(R.id.list_slidermen);
        gridlayout = (GridLayout) findViewById(R.id.gridLayout);
        send = (Button) findViewById(R.id.send);
        mainListView = (ListView) findViewById(R.id.list);
        feedsend = (EditText) findViewById(R.id.feedSend);
        helpt = (TextView) findViewById(R.id.helpText);
        contacts = (TextView) findViewById(R.id.contact);
        mail = (TextView) findViewById(R.id.mailid);
        token = intent.getStringExtra("Token");
        id = intent.getStringExtra("id");
        useremail = intent.getStringExtra("useremail");
        drawe.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        source = new ArrayList<String>();
        title = new ArrayList<String>();
        isread = new ArrayList<String>();
        time = new ArrayList<String>();
        color = new ArrayList<String>();
        feedid = new ArrayList<String>();
        url = new ArrayList<String>();
        refresher = (SwipeRefreshLayout) findViewById(R.id.swipedown);
        refresher.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new feedings().execute("");
            }
        });
        new feedings().execute("");
    }

    public void close_Me(View v) {
        if (!drawe.isDrawerVisible(men)) {
            gridlayout.setVisibility(View.VISIBLE);
            drawer.closeDrawer(menu);
        }
    }

    public void close_Me2(View v) {
        hideKeyboard();
        drawe.closeDrawer(men);
    }

    private void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager inputManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    public void help_Me(View v) {
        helpt.setText("Help");
        contacts.setVisibility(View.VISIBLE);
        mail.setVisibility(View.VISIBLE);
        feedsend.setVisibility(View.GONE);
        send.setVisibility(View.GONE);
        drawe.openDrawer(men);
    }

    public void feed_Me(View v) {
        if (!drawe.isDrawerVisible(men)) {
            contacts.setVisibility(View.GONE);
            mail.setVisibility(View.GONE);
            feedsend.setVisibility(View.VISIBLE);
            feedsend.setText("");
            send.setVisibility(View.VISIBLE);
            helpt.setText("Feedback");
            drawe.openDrawer(men);
        }
    }

    public void sendMe(View v) {
        hideKeyboard();
        Intent Email = new Intent(Intent.ACTION_SEND);
        Email.setType("text/email");
        Email.putExtra(Intent.EXTRA_EMAIL, new String[]{"info@informerly.com"});
        Email.putExtra(Intent.EXTRA_SUBJECT, "App Feedback from "+useremail);
        Email.putExtra(Intent.EXTRA_TEXT, feedsend.getText().toString());
        startActivity(Email);
    }

    public void logout_Me(View v) {
        if (!drawe.isDrawerVisible(men)) {
            boolean iff = isNetworkAvailable();
            if (iff) {
                new MyTask().execute("");
            } else {
                Toast.makeText(this, "No Internet Connection", Toast.LENGTH_LONG).show();
            }
        }
    }

    void logout() {
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        try {
            HttpEntity resEntity =  new HttpLogout(token,id).getEntity();
            if (resEntity != null) {
                json = EntityUtils.toString(resEntity);
                try {
                    logout = (new JSONObject(json));
                    check = logout.getString("success");
                } catch (Exception e) {

                }


            }
        } catch (Exception e) {
        }

    }

    public void open_Me(View v) {

        gridlayout.setVisibility(View.GONE);
        drawer.openDrawer(menu);
    }


    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private class MyTask extends AsyncTask<String, Void, String> {
        ProgressDialog dialog;

        @Override
        protected String doInBackground(String... params) {
            logout();
            return "Executed";
        }

        @Override
        protected void onPostExecute(String result) {
            dialog.cancel();
            try {
                if (check.equals("true")) {
                    Toast.makeText(FeedView.this, "You have been logged out.", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(FeedView.this, "Error logging out, Try Again", Toast.LENGTH_LONG).show();
                }
            } catch (Exception r) {
                r.printStackTrace();
            }
        }

        @Override
        protected void onPreExecute() {
            dialog = new ProgressDialog(FeedView.this);
            dialog.setMessage(" please wait");
            dialog.setTitle("Logging Out");
            dialog.show();
            dialog.setCancelable(false);
        }
    }

    public void getFeeds() {
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        try {
            HttpEntity resEntityGet = new HttpGetArticle(token,id).getArticles();
            if (resEntityGet != null) {
                json = EntityUtils.toString(resEntityGet);
                try {
                    JSONObject jsonResponse = new JSONObject(json);
                    JSONArray cast = jsonResponse.getJSONArray("links");
                    source.clear();
                    title.clear();
                    time.clear();
                    isread.clear();
                    color.clear();
                    url.clear();
                    feedid.clear();
                    for(int index = 0; index < cast.length(); index++) {
                        JSONObject jsonObject = cast.getJSONObject(index);

                        String name = jsonObject.getString("source");
                        source.add(name);
                        String tit = jsonObject.getString("title");
                        title.add(tit);
                        String tim = jsonObject.getString("reading_time");
                        time.add(tim);
                        String isred = jsonObject.getString("read");
                        isread.add(isred);
                        String colr = jsonObject.getString("source_color");
                        color.add(colr);
                        String fedid = jsonObject.getString("id");
                        feedid.add(fedid);
                        String urll = jsonObject.getString("url");
                        url.add(urll);
                    }
                } catch (Exception e) {
                    Toast.makeText(this,"Failed :Please refresh",Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
                Log.i("GET RESPONSE", json);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class feedings extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            getFeeds();
            return "Executed";
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                listAdapters = new CustomAdapter(FeedView.this,source);
                mainListView.setAdapter(listAdapters);
                baar.setVisibility(View.GONE);
                refresher.setRefreshing(false);
            } catch (Exception r) {
                r.printStackTrace();
            }
        }

        @Override
        protected void onPreExecute() {
        }
    }
    private class CustomAdapter extends ArrayAdapter<String> {

        protected Context mContext;
        protected ArrayList<String> mItems;

        public CustomAdapter(Context context, ArrayList<String> items) {
            super(context, R.layout.row, items);
            mContext = context;
            mItems = items;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView == null){
                convertView = LayoutInflater.from(FeedView.this).inflate(R.layout.row,null);
            }
            TextView readview = ((TextView) convertView.findViewById(R.id.read));
            TextView titleview = ((TextView) convertView.findViewById(R.id.title));
            ImageView clockview =((ImageView) convertView.findViewById(R.id.clock));
            TextView sourceview = ((TextView) convertView.findViewById(R.id.source));

            titleview.setText(title.set(position,title.get(position)));
            sourceview.setText(source.set(position, source.get(position)));
            sourceview.setTextColor(Color.parseColor(color.get(position)));
            sourceview.setTypeface(null, Typeface.BOLD);
            if(isread.get(position)== "true")
            {
                readview.setText("Read");
                titleview.setTypeface(null, Typeface.NORMAL);
                titleview.setTextColor(Color.GRAY);
                clockview.setImageResource(R.drawable.tick);
            }
            else
            {
                titleview.setTextColor(Color.BLACK);
                titleview.setTypeface(null, Typeface.BOLD);
                clockview.setImageResource(R.drawable.clock);
                readview.setText(time.set(position, time.get(position)) + " min read");
            }
            return convertView;
        }
    }
}
