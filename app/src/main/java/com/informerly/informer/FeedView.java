package com.informerly.informer;

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
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import android.preference.PreferenceManager;
import android.content.SharedPreferences;

import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;

import org.apache.http.HttpEntity;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import com.informerly.informer.APICalls.HttpGetFeedArticles;
import com.informerly.informer.APICalls.GetFeeds;
import com.informerly.informer.APICalls.HttpLogout;
import com.informerly.informer.APICalls.MarkRead;


public class FeedView extends ActionBarActivity {

    private String sessionToken, userId,useremail;
    private DrawerLayout drawerAppContent;

    private ListView articlesList, feedsListView;
    private SwipeRefreshLayout articlesRefresher, feedsRefresher;

    private ProgressBar articleProgressBar;

    private ArrayList<Article> allArticlesArray, unreadArticlesArray;
    private ArrayList<Feed> feedItems;
    private RelativeLayout feedsMenu;

    private ArrayAdapter<Article> allArticlesAdapters, unreadArticlesAdapters;
    private ArrayAdapter<Feed> listFeedsAdapters;

    private static Context mContext;

//  private ActionBarDrawerToggle mDrawerToggle;
//  private EditText feedsend;

    private TextView headerTitle;

    private int actualFeedId;

    private boolean defaultUnreadPreference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_view);
        Intent intent = getIntent();
        this.mContext = this;

        // Getting user data
        sessionToken = intent.getStringExtra("Token");
        userId = intent.getStringExtra("id");
        useremail = intent.getStringExtra("useremail");

        // Preparing main Layout
        drawerAppContent = (DrawerLayout) findViewById(R.id.drawer_app_content);
        drawerAppContent.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

        // get the progress bar
        articleProgressBar = (ProgressBar) findViewById(R.id.articleProgressBar);

        // get the app header tittle
        headerTitle = (TextView) findViewById(R.id.app_header_title);
        
        // Setting up application Toolbar
        Toolbar actionBarToolbar = (Toolbar) findViewById(R.id.app_toolbar);
        setSupportActionBar(actionBarToolbar);

        // Left feeds/options menu
        feedsMenu = (RelativeLayout) findViewById(R.id.left_menu);
        feedsListView = (ListView) findViewById(R.id.feedMenuList);

        // Preparing articles list
        articlesList = (ListView)findViewById(R.id.article_list);
        articlesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> parent, View view, int position, long iid) {

                Article selectedArticle = (Article) parent.getAdapter().getItem(position);

                // reading article actions
                new markReadedArticleTask().execute(String.valueOf(selectedArticle.id));
                unreadArticlesArray.remove(selectedArticle);
                allArticlesArray.get(allArticlesArray.indexOf(selectedArticle)).read = true;

                Intent i = new Intent(FeedView.this, ArticleView.class);
                i.putExtra("articleUrl", selectedArticle.url);
                i.putExtra("articleTitle", selectedArticle.title);
                i.putExtra("articleId", String.valueOf(selectedArticle.id));
                i.putExtra("token", sessionToken);
                i.putExtra("userid", userId);
                startActivity(i);
            }
        });

        // Adding articles filters at top of list
        LinearLayout topButtons = (LinearLayout)getLayoutInflater().inflate(R.layout.action_row, null);
        articlesList.addHeaderView(topButtons);

        // Articles refresher list action
        articlesRefresher = (SwipeRefreshLayout) findViewById(R.id.swipeArticlesRefresher);
        articlesRefresher.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new feedTask().execute(Integer.toString(actualFeedId));
            }
        });
        
        // Feeds refresher list action
        feedsRefresher = (SwipeRefreshLayout) findViewById(R.id.swipeFeedsRefresher);
        feedsRefresher.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new userFeedsTask().execute("");
            }
        });
        

//        mDrawerToggle = new ActionBarDrawerToggle(
//                this,                  /* host Activity */
//                drawerAppContent,         /* DrawerLayout object */
//                actionBarToolbar,  /* nav drawer icon to replace 'Up' caret */
//                R.string.drawer_open,  /* "open drawer" description */
//                R.string.drawer_close  /* "close drawer" description */
//        ) {
//
//            /** Called when a drawer has settled in a completely closed state. */
//            public void onDrawerClosed(View view) {
//                super.onDrawerClosed(view);
////                getActionBar().setTitle(mTitle);
//                Log.d("debug","Menu Closed");
//            }
//
//            /** Called when a drawer has settled in a completely open state. */
//            public void onDrawerOpened(View drawerView) {
//                super.onDrawerOpened(drawerView);
////                getActionBar().setTitle(mDrawerTitle);
//                Log.d("debug","Menu Opened");
//            }
//        };
        

        // Starting app, getting default feed
        new feedTask().execute("0");
        new userFeedsTask().execute("");

        // Getting user preferences
        SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        defaultUnreadPreference = SP.getBoolean("filterUnreadPreference", false);

    }

    @Override
    protected void onResume() {
        super.onResume();
        if(allArticlesAdapters != null && unreadArticlesAdapters != null) {
            unreadArticlesAdapters.notifyDataSetChanged();
            allArticlesAdapters.notifyDataSetChanged();
        }
    }

    public static Context getContext(){
        return mContext;
    }

    public void readArticle(String articleid) {
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        try {
            HttpEntity resEntityGet = new MarkRead(sessionToken,userId,articleid).mark();

            if (resEntityGet != null) {
                String json = EntityUtils.toString(resEntityGet);
            } else {
                Toast.makeText(FeedView.this,"Connection non response",Toast.LENGTH_SHORT).show();
            }
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
        Log.d("Just test", "hello there!");
    }

    public void switchDrawer(View v) {

        if(drawerAppContent.isDrawerOpen(feedsMenu)) {
            drawerAppContent.closeDrawer(feedsMenu);
        } else {
            drawerAppContent.openDrawer(feedsMenu);
        }
    }

    public void filterAllNews(View v) {
        articlesList.setAdapter(allArticlesAdapters);
    }

    public void filterUnreadNews(View v) {
        articlesList.setAdapter(unreadArticlesAdapters);
    }


//    Deprecated methods
//
//    public void close_Me2(View v) {
//        hideKeyboard();
//        drawe.closeDrawer(men);
//    }
//    private void hideKeyboard() {
//        View view = this.getCurrentFocus();
//        if (view != null) {
//            InputMethodManager inputManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
//            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
//        }
//    }
//    public void sendMe(View v) {
//        hideKeyboard();
//        Intent Email = new Intent(Intent.ACTION_SEND);
//        Email.setType("text/email");
//        Email.putExtra(Intent.EXTRA_EMAIL, new String[]{"info@informerly.com"});
//        Email.putExtra(Intent.EXTRA_SUBJECT, "App Feedback from "+useremail);
//        Email.putExtra(Intent.EXTRA_TEXT, feedsend.getText().toString());
//        startActivity(Email);
//    }

    public void goSettings(View v) {
        Intent i = new Intent(this, Preferences.class);
        startActivity(i);
    }

    public void goHelp(View v) {
        Log.d("Debug", "Help me please!");
    }

    public void goLogout(View v) {

        boolean iff = isNetworkAvailable();
        if (iff) {
    //      new LogoutTask2().execute(sessionToken, userId);
            new LogoutTask().execute("");
        } else {
            Toast.makeText(this, "No Internet Connection", Toast.LENGTH_LONG).show();
        }
    }

    void logout() {
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        try {
            HttpEntity resEntity =  new HttpLogout(sessionToken,userId).getEntity();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
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
                finish();
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

    public void getUserFeeds() {
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        try {
            HttpEntity resEntityGet = new GetFeeds(sessionToken,userId).getUserFeeds();

            feedItems = new ArrayList<Feed>();

            if (resEntityGet != null) {
                String json = EntityUtils.toString(resEntityGet);

                try {
                    JSONObject response = new JSONObject(json);
                    JSONArray feeds = response.getJSONArray("feeds");

                    // adding first default user feed
                    Feed defaultFeed = new Feed( 0, "Your Feed", "MAGIC", true, Integer.parseInt(userId) );
                    feedItems.add(defaultFeed);

                    for (int i = 0; i < feeds.length(); ++i) {
                        JSONObject feed = feeds.getJSONObject(i);
                        Feed menuFeed = new Feed( feed );
                        feedItems.add(menuFeed);
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


    private class userFeedsTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            getUserFeeds();
            return "Executed";
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                listFeedsAdapters = new FeedObjectAdapter(FeedView.this,feedItems);
                feedsListView.setAdapter(listFeedsAdapters);

                feedsListView.setOnItemClickListener(new feedItemClickListener());

                feedsRefresher.setRefreshing(false);
            } catch (Exception r) {
                r.printStackTrace();
            }
        }

        @Override
        protected void onPreExecute() {
        }
    }

    private class feedItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView parent, View view, int position, long id) {
            new feedTask().execute(Integer.toString(feedItems.get(position).id));
            headerTitle.setText(feedItems.get(position).name);
            switchDrawer(feedsMenu);
        }
    }

    private class FeedObjectAdapter extends ArrayAdapter<Feed> {

        protected Context mContext;
        protected ArrayList<Feed> mItems;

        protected ArrayList<String> original;

        public FeedObjectAdapter(Context context, ArrayList<Feed> feeds) {
            super(context, R.layout.menu_row, feeds);
            mContext = context;
            mItems = feeds;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView == null){
                convertView = LayoutInflater.from(FeedView.this).inflate(R.layout.menu_row,null);
            }
            TextView feedNameview = ((TextView) convertView.findViewById(R.id.feedTitle));
            ImageView feedIconview =((ImageView) convertView.findViewById(R.id.feedIcon));

            feedNameview.setText(mItems.get(position).name);
            if(mItems.get(position).name.equals("Your Feed")) {
                feedIconview.setImageResource(R.drawable.home);
            } else {
                feedIconview.setImageResource(R.drawable.folder);
            }

            return convertView;
        }

    }

    public void getArticles(int feedId) {
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        try {
            HttpEntity resEntityGet = new HttpGetFeedArticles(sessionToken,userId).getArticles(feedId);

            allArticlesArray = new ArrayList<Article>();
            unreadArticlesArray = new ArrayList<Article>();

            if (resEntityGet != null) {
                String json = EntityUtils.toString(resEntityGet);

                try {
                    JSONObject jsonResponse = new JSONObject(json);
                    JSONArray jsonArticles = jsonResponse.getJSONArray("links");

                    for(int index = 0; index < jsonArticles.length(); index++) {

                        Article newItem = new Article( jsonArticles.getJSONObject(index) );
                        allArticlesArray.add( newItem );

                        if(!newItem.read) {
                            unreadArticlesArray.add(newItem);
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
            String feedId = params[0];
            getArticles( Integer.parseInt(feedId) );
            actualFeedId = Integer.parseInt(feedId);
            return "Executed";
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                allArticlesAdapters = new ArticlesObjectAdapter(FeedView.this,allArticlesArray);
                unreadArticlesAdapters = new ArticlesObjectAdapter(FeedView.this,unreadArticlesArray);

                if(defaultUnreadPreference) {
                    RadioButton unreadFilterBtn = (RadioButton) findViewById(R.id.filterUnreadRadioButton);
                    unreadFilterBtn.setChecked(true);
                    articlesList.setAdapter(unreadArticlesAdapters);
                } else {
                    articlesList.setAdapter(allArticlesAdapters);
                }

                articleProgressBar.setVisibility(View.GONE);
                articlesRefresher.setRefreshing(false);
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
