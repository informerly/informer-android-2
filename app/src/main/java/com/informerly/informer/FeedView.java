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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;
import java.util.ArrayList;

import com.informerly.informer.APICalls.HttpGetArticle;
import com.informerly.informer.APICalls.HttpLogout;
import com.informerly.informer.APICalls.MarkRead;

public class FeedView extends Activity {
    String sessionToken, userId, json, check,useremail;
    DrawerLayout drawer, drawe;
    ListView listt;
    ProgressBar baar;
    GridLayout gridlayout;
    Button send;
    SwipeRefreshLayout refresher;
    JSONObject logout;
    TextView helpt, mail, contacts;
    EditText feedsend;
    RelativeLayout menu, men;
    ArrayList<Article> listItems, unreadItems;
    private ListView mainListView;
    private ArrayAdapter<Article> listArticleAdapters, unreadArticleAdapters;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_view);
        Intent intent = getIntent();

        listt = (ListView)findViewById(R.id.list);

        listt.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> parent, View view, int position, long iid) {

                // reading article actions
                new markReadedArticleTask().execute(String.valueOf(listItems.get(position - 1).id));
                unreadItems.remove(listItems.get(position - 1));

                Intent i = new Intent(FeedView.this, ArticleView.class);
                i.putExtra("Uurl", listItems.get(position-1).url);
                i.putExtra("Ttitle", listItems.get(position-1).title);
                i.putExtra("feedid", String.valueOf(listItems.get(position-1).id));

                i.putExtra("token", sessionToken);
                i.putExtra("userid", userId);
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
        sessionToken = intent.getStringExtra("Token");
        userId = intent.getStringExtra("id");
        useremail = intent.getStringExtra("useremail");
        drawe.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        refresher = (SwipeRefreshLayout) findViewById(R.id.swipedown);
        refresher.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new feedTask().execute("");
            }
        });

        new feedTask().execute("");

        LinearLayout topButtons = (LinearLayout)getLayoutInflater().inflate(R.layout.action_row, null);

        // Adding Load More button to lisview at top
        listt.addHeaderView(topButtons);
    }

    public void readArticle(String articleid) {
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        try {
            new MarkRead(sessionToken,userId,articleid).mark();
        } catch (Exception e) {
            Toast.makeText(FeedView.this,"Connection error",Toast.LENGTH_SHORT).show();
        }
    }

    private class markReadedArticleTask extends AsyncTask<String, Void, String> {
        //ProgressDialog dilog;
        @Override
        protected String doInBackground(String... params) {
            try {
                String articleId = params[0];
                readArticle(articleId);
            }
            catch(Exception e)
            {
                Toast.makeText(FeedView.this,"Connection error",Toast.LENGTH_SHORT).show();
            }
            return "Executed";
        }

        @Override
        protected void onPostExecute(String result) {
        }

        @Override
        protected void onPreExecute() {
        }
    }


    public void say_hello(View v) {
        Log.d("Just test","hello there!");
    }

    public void filterAllNews(View v) {
        mainListView.setAdapter(listArticleAdapters);
    }

    public void filterUnreadNews(View v) {
        mainListView.setAdapter(unreadArticleAdapters);
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
                new LogoutTask().execute("");
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
            HttpEntity resEntity =  new HttpLogout(sessionToken,userId).getEntity();
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

    public class Article {
        /* ACTUAL ARTICLE MODEL API RESPONSE
        {
            "id":38685,
            "title":"Web Design - The First 100 Years",
            "description":"This is the expanded version of a talk I gave on September 9, 2014, at the HOW Interactive Design conference in Washington, DC.",
            "reading_time":2,
            "source":"Idlewords",
            "published_at":"2015-07-21T18:38:02.440-04:00",
            "original_date":null,
            "slug":"web-design-the-first-100-years",
            "url":"http://idlewords.com/talks/web_design_first_100_years.htm?curator=Informerly",
            "read":true,
            "bookmarked":false,
            "source_color":"#ae1414"
        }
        */

        int id;
        String title;
        String description;
        int reading_time;
        String source;
        String published_at;
        String original_date;
        String slug;
        String url;
        boolean read;
        boolean bookmarked;
        String source_color;

        public Article(
                int id,
                String title,
                String description,
                int reading_time,
                String source,
                String published_at,
                String original_date,
                String slug,
                String url,
                boolean read,
                boolean bookmarked,
                String source_color
                )
        {

            this.id = id;
            this.title = title;
            this.description = description;
            this.reading_time = reading_time;
            this.source = source;
            this.published_at = published_at;
            this.original_date = original_date;
            this.slug = slug;
            this.url = url;
            this.read = read;
            this.bookmarked = bookmarked;
            this.source_color = source_color;
            
        }
        
        public Article( JSONObject jsonArticle ) {
            try {
                this.id = jsonArticle.getInt("id");
                this.title = jsonArticle.getString("title");
                this.description = jsonArticle.getString("description");
                this.reading_time = jsonArticle.getInt("reading_time");
                this.source = jsonArticle.getString("source");
                this.published_at = jsonArticle.getString("published_at");
                this.original_date = jsonArticle.getString("original_date");
                this.slug = jsonArticle.getString("slug");
                this.url = jsonArticle.getString("url");
                this.read = jsonArticle.getBoolean("read");
                this.bookmarked = jsonArticle.getBoolean("bookmarked");
                this.source_color = jsonArticle.getString("source_color");
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private class LogoutTask extends AsyncTask<String, Void, String> {
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

    public void getArticles() {
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        try {
            HttpEntity resEntityGet = new HttpGetArticle(sessionToken,userId).getArticles();

            listItems = new ArrayList<Article>();
            unreadItems = new ArrayList<Article>();

            if (resEntityGet != null) {
                json = EntityUtils.toString(resEntityGet);

                try {
                    JSONObject jsonResponse = new JSONObject(json);
                    JSONArray jsonArticles = jsonResponse.getJSONArray("links");

                    for(int index = 0; index < jsonArticles.length(); index++) {

                        Article newItem = new Article( jsonArticles.getJSONObject(index) );
                        listItems.add( newItem );

                        if(!newItem.read) {
                            unreadItems.add(newItem);
                        }

                    }
                } catch (Exception e) {
                    Toast.makeText(this,"Failed :Please refresh",Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class feedTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            getArticles();
            return "Executed";
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                listArticleAdapters = new ArticlesObjectAdapter(FeedView.this,listItems);
                unreadArticleAdapters = new ArticlesObjectAdapter(FeedView.this,unreadItems);
                mainListView.setAdapter(listArticleAdapters);

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


    private class ArticlesObjectAdapter extends ArrayAdapter<Article> {

        protected Context mContext;
        protected ArrayList<Article> mItems;

        protected ArrayList<String> original;

        public ArticlesObjectAdapter(Context context, ArrayList<Article> items) {
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

            titleview.setText(mItems.get(position).title);
            sourceview.setText(mItems.get(position).source);
            sourceview.setTextColor(Color.parseColor( mItems.get(position).source_color ));
            sourceview.setTypeface(null, Typeface.BOLD);

            if(mItems.get(position).read)
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
                readview.setText(mItems.get(position).reading_time + " min read");
            }

            return convertView;
        }

    }

}
