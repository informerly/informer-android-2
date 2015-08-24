package com.informerly.informer;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;

import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import android.preference.PreferenceManager;
import android.content.SharedPreferences;

import android.support.v7.widget.Toolbar;

import android.view.MenuItem;
import android.view.MenuInflater;

import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView.AdapterContextMenuInfo;

import org.apache.http.HttpEntity;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Map;
import java.util.ArrayList;
import com.google.gson.Gson;

import com.informerly.informer.APICalls.HttpGetFeedArticles;
import com.informerly.informer.APICalls.GetFeeds;
import com.informerly.informer.APICalls.HttpLogout;
import com.informerly.informer.Tasks.BookmarkTask;
import com.informerly.informer.Tasks.MarkReadTask;
import com.informerly.informer.Adapters.FeedObjectAdapter;
import com.informerly.informer.Adapters.ArticleObjectAdapter;
import com.informerly.informer.Util.JSONSharedPreferences;

public class FeedView extends ActionBarActivity {

    private String sessionToken, userId, useremail;
    private DrawerLayout drawerAppContent;

    private ListView articlesList, feedsListView;
    private SwipeRefreshLayout articlesRefresher, feedsRefresher;

    private ProgressBar articleProgressBar;

    private ArrayList<Article> allArticlesArray, unreadArticlesArray, savedArticlesArray;
    private ArrayList<Feed> feedItems;
    private RelativeLayout feedsMenu;
    private LinearLayout topButtons;

    private ArrayAdapter<Article> allArticlesAdapters, unreadArticlesAdapters, savedArticlesAdapters;
    private ArrayAdapter<Feed> listFeedsAdapters;

    private static Context mContext;

//  private ActionBarDrawerToggle mDrawerToggle;
//  private EditText feedsend;

    private TextView headerTitle, indicatorText;

    private int actualFeedId;

    private boolean defaultUnreadPreference;

    SharedPreferences sharedpreferences;

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
        indicatorText = (TextView) findViewById(R.id.indicatorText);

        // Setting up application Toolbar
        Toolbar actionBarToolbar = (Toolbar) findViewById(R.id.app_toolbar);
        setSupportActionBar(actionBarToolbar);

        // Left feeds/options menu
        feedsMenu = (RelativeLayout) findViewById(R.id.left_menu);
        feedsListView = (ListView) findViewById(R.id.feedMenuList);

        // Preparing articles list
        articlesList = (ListView)findViewById(R.id.article_list);
        articlesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> listview, View view, int pos, long id) {

                Article selectedArticle = (Article) listview.getAdapter().getItem(pos);

                // reading article actions
                if (!selectedArticle.isRead()) {
                    new MarkReadTask().execute(String.valueOf(selectedArticle.getId()), String.valueOf(selectedArticle.isRead()));

                    if(unreadArticlesArray.contains(selectedArticle)) {
                        unreadArticlesArray.remove(selectedArticle);
                    }

                    if(allArticlesArray.contains(selectedArticle)) {
                        allArticlesArray.get(allArticlesArray.indexOf(selectedArticle)).setRead(true);
                    }

                    if(savedArticlesArray.contains(selectedArticle)) {
                        savedArticlesArray.get(savedArticlesArray.indexOf(selectedArticle)).setRead(true);
                    }
                }

                Intent i = new Intent(FeedView.this, ArticleView.class);
                i.putExtra("articleUrl", selectedArticle.getUrl());
                i.putExtra("articleTitle", selectedArticle.getTitle());
                i.putExtra("articleId", String.valueOf(selectedArticle.getId()));
                i.putExtra("token", sessionToken);
                i.putExtra("userid", userId);
                startActivity(i);
            }
        });

        // Registering contextual menu with in feed actions for each article
        registerForContextMenu(articlesList);

        // Adding articles filters at top of list
        topButtons = (LinearLayout)getLayoutInflater().inflate(R.layout.action_row, null);
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

        // Getting stored content (saved articles feed)
        savedArticlesArray = new ArrayList<Article>();
        try {
            if(JSONSharedPreferences.count(FeedView.this, "savedArticles")>0) {
                Map<String, ?> savedArticlesMap = JSONSharedPreferences.getAll(FeedView.this,"savedArticles");
                for (Map.Entry<String, ?> entry : savedArticlesMap.entrySet()) {
                    Gson gson = new Gson();
                    Article newItem = gson.fromJson((String) entry.getValue(), Article.class);
                    savedArticlesArray.add(newItem);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        savedArticlesAdapters = new ArticleObjectAdapter(FeedView.this,savedArticlesArray);

    }

    @Override
    protected void onResume() {
        super.onResume();
        if(allArticlesAdapters != null && unreadArticlesAdapters != null) {
            unreadArticlesAdapters.notifyDataSetChanged();
            allArticlesAdapters.notifyDataSetChanged();
            savedArticlesAdapters.notifyDataSetChanged();
        }
    }

    @Override
    public void onBackPressed() {

        if(drawerAppContent.isDrawerOpen(feedsMenu)) {
            drawerAppContent.closeDrawer(feedsMenu);
        } else {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            startActivity(intent);
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.feed_items_menu, menu);

        AdapterContextMenuInfo info = (AdapterContextMenuInfo) menuInfo;
        Article selectedArticle = (Article) articlesList.getAdapter().getItem( info.position );

        if(selectedArticle.isRead()) {
            menu.findItem(R.id.menu_read).setTitle("Mark article as unread");
        } else {
            menu.findItem(R.id.menu_read).setTitle("Mark article as read");
        }

        if(selectedArticle.isBookmarked()) {
            menu.findItem(R.id.menu_save).setTitle("Remove from bookmarks");
        } else {
            menu.findItem(R.id.menu_save).setTitle("Store in bookmarks");
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
        Article selectedArticle = (Article) articlesList.getAdapter().getItem(info.position);

        switch (item.getItemId()) {
            case R.id.menu_read:
                // reading article actions
                new MarkReadTask().execute(String.valueOf(selectedArticle.getId()), String.valueOf(selectedArticle.isRead()));

                Boolean read = !selectedArticle.isRead();
                if(read) {
                    if(unreadArticlesArray.contains(selectedArticle)) {
                        unreadArticlesArray.remove(selectedArticle);
                    }
                } else {
                    if(!unreadArticlesArray.contains(selectedArticle)) {
                        unreadArticlesArray.add(selectedArticle);
                    }
                }

                if(allArticlesArray.contains(selectedArticle)) {
                    allArticlesArray.get(allArticlesArray.indexOf(selectedArticle)).setRead(read);
                }

                if(savedArticlesArray.contains(selectedArticle)) {
                    savedArticlesArray.get(savedArticlesArray.indexOf(selectedArticle)).setRead(read);
                }

                unreadArticlesAdapters.notifyDataSetChanged();
                allArticlesAdapters.notifyDataSetChanged();
                savedArticlesAdapters.notifyDataSetChanged();

                return true;

            case R.id.menu_save:
                // bookmarking article actions
                new BookmarkTask().execute(String.valueOf(selectedArticle.getId()), String.valueOf(selectedArticle.isBookmarked()));

                if(!savedArticlesArray.contains(selectedArticle)) {
                    savedArticlesArray.add(selectedArticle);
                    savedArticlesAdapters.notifyDataSetChanged();
                }

//                String jsonA = "nada";
//                try {
//                    jsonA = JSONSharedPreferences.getJSONString(this,"savedArticles", String.valueOf(selectedArticle.getId()));
//                }
//                catch (JSONException e) {
//                    e.printStackTrace();
//                }

                // updating local lists
                selectedArticle.setBookmarked(!selectedArticle.isBookmarked());
                if(unreadArticlesArray.indexOf(selectedArticle) != -1) {
                    unreadArticlesArray.get(unreadArticlesArray.indexOf(selectedArticle)).setBookmarked(selectedArticle.isBookmarked());
                    unreadArticlesAdapters.notifyDataSetChanged();
                }

                if(allArticlesArray.indexOf(selectedArticle)!=-1) {
                    allArticlesArray.get(allArticlesArray.indexOf(selectedArticle)).setBookmarked(selectedArticle.isBookmarked());
                    allArticlesAdapters.notifyDataSetChanged();
                }
                return true;

            case R.id.menu_share:
                // via android sharing dialog
                shareArticle(selectedArticle.getTitle(), selectedArticle.getUrl());
                return true;

            default:
                return super.onContextItemSelected(item);

        }
    }

    public static Context getContext(){
        return mContext;
    }

    public void shareArticle(String articleTitle, String articleUrl) {
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, articleTitle + " " + articleUrl);
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Subject");
        startActivity(Intent.createChooser(sharingIntent, "Share using"));
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

    public void goBookmarks(View v) {
        headerTitle.setText(R.string.menu_bookmarks);
        articlesRefresher.setEnabled(false);

        if(savedArticlesArray.size() > 0) {
            articlesList.setAdapter(savedArticlesAdapters);
            articlesList.removeHeaderView(topButtons);
        } else {
            articlesList.setVisibility(View.GONE);
            indicatorText.setVisibility(View.VISIBLE);
        }

        switchDrawer(feedsMenu);
    }

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
    //      new Logout().execute(sessionToken, userId);
            new LogoutTask().execute("");
        } else {
            Toast.makeText(this, "No Internet Connection", Toast.LENGTH_LONG).show();
        }
    }

    private void logout() {
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        try {
            new HttpLogout(sessionToken,userId).getEntity();
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
            sharedpreferences = getSharedPreferences("user_session", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.clear();
            editor.commit();
            dialog.cancel();

            try {
                Intent i = new Intent(FeedView.this, LogIn.class);
                startActivity(i);
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
            new feedTask().execute(Integer.toString(feedItems.get(position).getId()));
            headerTitle.setText(feedItems.get(position).getName());

            if(articlesList.getVisibility()!=View.VISIBLE) {
                articlesList.setVisibility(View.VISIBLE);
                indicatorText.setVisibility(View.GONE);
            }
            if(articlesList.getHeaderViewsCount() == 0) {
                articlesList.addHeaderView(topButtons);
            }
            if(!articlesRefresher.isEnabled()) {
                articlesRefresher.setEnabled(true);
            }
            switchDrawer(feedsMenu);
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
                        allArticlesArray.add(newItem);

                        if(!newItem.isRead()) {
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
                allArticlesAdapters = new ArticleObjectAdapter(FeedView.this,allArticlesArray);
                unreadArticlesAdapters = new ArticleObjectAdapter(FeedView.this,unreadArticlesArray);

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
}
