package com.informerly.informer;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import com.informerly.informer.APICalls.MarkRead;

import com.informerly.informer.R;


public class ArticleView extends ActionBarActivity {
    WebView myWebView,myWebViewZen;
    String url = "http://fortune.com/ikea-world-domination/?curator=Informerly";
    Button zeen,weeb;
    HttpEntity resEntityGet;
    boolean forlopp = true;
    String titles,id,token,articleid,json,baseUrlZen;
     static String baseurl = "http://informerly.com/api/v1/links/24203/read";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.article_view);
        Intent intent = getIntent();
        url = intent.getStringExtra("Uurl");
        titles = intent.getStringExtra("Ttitle");
        id = intent.getStringExtra("userid");
        token = intent.getStringExtra("token");
        articleid = intent.getStringExtra("feedid");
        myWebView = (WebView) findViewById(R.id.webview);
        myWebViewZen = (WebView) findViewById(R.id.webviewZen);
        weeb = (Button) findViewById(R.id.web);
        zeen = (Button) findViewById(R.id.zen);
        baseurl = "http://informerly.com/api/v1/links/"+articleid+"/read";
        myWebView.loadUrl(url);
        myWebView.setBackgroundColor(Color.TRANSPARENT);
        myWebView.getSettings().setJavaScriptEnabled(true);
        myWebViewZen.setBackgroundColor(Color.TRANSPARENT);
        myWebViewZen.getSettings().setJavaScriptEnabled(true);
        myWebView.setWebViewClient(new MyWebViewClient());
            }
        });
        myWebViewZen.setWebViewClient(new WebViewClient() {

            }
        });
        APIcallStaticPage();
        APIcallRead();

    }

    public void APIcallRead()
    {
        new MyTask().execute("");
    }
    public void APIcallStaticPage()
    {
        new MyZenTask().execute("");
    }
    public void markRead(){
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        try {
            new MarkRead(token,id,articleid).mark();
        } catch (Exception e) {
            Toast.makeText(ArticleView.this,"Connection error",Toast.LENGTH_SHORT).show();
        }
    }
    public void back_Me(View v) {
    finish();
    }
    public void share_Me(View v) {
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, titles+" " + url);
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Subject");
        startActivity(Intent.createChooser(sharingIntent, "Share using"));
    }

    public void Open_web(View v) {
        weeb.setBackgroundColor(Color.parseColor("#FF3B9EFC"));
        zeen.setBackgroundColor(Color.parseColor("#ff000000"));
        myWebView.setVisibility(View.VISIBLE);
        myWebViewZen.setVisibility(View.GONE);
    }
    public void Open_zen(View v) {
        myWebView.setVisibility(View.GONE);
        myWebViewZen.setVisibility(View.VISIBLE);
        weeb.setBackgroundColor(Color.parseColor("#ff000000"));
        zeen.setBackgroundColor(Color.parseColor("#FF3B9EFC"));
        baar.setVisibility(View.GONE);
    }
    public void staticZenPage() {
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        try {
            HttpClient client = new DefaultHttpClient();
            client.getParams().setParameter(org.apache.http.params.CoreProtocolPNames.USER_AGENT, System.getProperty("http.agent"));
            String getURL = "http://informerly.com/api/v1/feeds?auth_token=" + token + "&content=true";
            HttpGet get = new HttpGet(getURL);
            HttpResponse responseGet = client.execute(get);
            resEntityGet = responseGet.getEntity();
            if (resEntityGet != null) {
                json = EntityUtils.toString(resEntityGet);
                JSONObject jsonResponse = new JSONObject(json);
                JSONArray cast = jsonResponse.getJSONArray("links");
                for(int index=0;index<=19;index++) {
                    if(forlopp) {
                        JSONObject jsonObject = cast.getJSONObject(index);
                        String name = jsonObject.getString("id");
                        baseUrlZen = jsonObject.getString("content");
                        if(name.equals(articleid))
                        {
                            forlopp = false;
                        }
                    }

                }
            }
        }
        catch (Exception e) {
            Toast.makeText(ArticleView.this, "Connection error", Toast.LENGTH_SHORT).show();


        }
    }
    private class MyTask extends AsyncTask<String, Void, String> {
        //ProgressDialog dilog;
        @Override
        protected String doInBackground(String... params) {
            //Put your code here
            try {
                markRead();
            }
            catch(Exception e)
            {

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



    public class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }
    private class MyZenTask extends AsyncTask<String, Void, String> {
        //ProgressDialog dilog;
        @Override
        protected String doInBackground(String... params) {
            //Put your code here
            try {
                staticZenPage();
            }
            catch(Exception e)
            {

            }
            return "Executed";
        }

        @Override
        protected void onPostExecute(String result) {
            myWebViewZen.loadDataWithBaseURL(null, baseUrlZen, "text/html", "utf-8", null);
        }

        @Override
        protected void onPreExecute() {
        }
    }
}
