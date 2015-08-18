package com.informerly.informer;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;


public class ArticleView extends ActionBarActivity {
    
    WebView webView,zenView;
    Button viewZenButton,viewWebButton;
    String articleUrl, articleTitle, userId, sesionToken, articleid, json, zenArticleContent;
    ProgressBar webViewProgressBar,zenViewProgressBar;
    HttpEntity resEntityGet;
    LinearLayout switchToZenView;

    boolean onZenView = false;
    boolean webViewLoading = false;
    boolean zenViewLoading = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.article_view);
        Intent intent = getIntent();

        // get article params
        articleid = intent.getStringExtra("articleId");
        articleUrl = intent.getStringExtra("articleUrl");
        articleTitle = intent.getStringExtra("articleTitle");
        userId = intent.getStringExtra("userid");
        sesionToken = intent.getStringExtra("token");

        webView = (WebView) findViewById(R.id.webview);
        zenView = (WebView) findViewById(R.id.webviewZen);

        viewWebButton = (Button) findViewById(R.id.web);
        viewZenButton = (Button) findViewById(R.id.zen);

        switchToZenView = (LinearLayout) findViewById(R.id.switchToZenView);

        webViewProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        zenViewProgressBar = (ProgressBar) findViewById(R.id.progressBarZen);

        webView.setBackgroundColor(Color.TRANSPARENT);
        webView.getSettings().setJavaScriptEnabled(true);

        zenView.setBackgroundColor(Color.TRANSPARENT);
        zenView.getSettings().setJavaScriptEnabled(true);

        webView.setWebChromeClient(new MyWebChromeClient());
        zenView.setWebChromeClient(new ZenWebChromeClient());

        webView.setWebViewClient(new MyWebViewClient());
        zenView.setWebViewClient(new ZenWebViewClient());
        

        // Getting user preferences
        SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        Boolean defaultZenPreference = SP.getBoolean("zenViewPreference", false);

        if(defaultZenPreference) {
            executeZen();
            openZenView(this.findViewById(android.R.id.content));
        } else {
            // set html view by default
            webView.loadUrl(articleUrl);
            openWebView(this.findViewById(android.R.id.content));
        }

    }

    public class MyWebViewClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            webViewLoading = true;
            return true;
        }

        public void onPageFinished(WebView view, String url) {
            if(!onZenView && webView.getVisibility() != View.VISIBLE) {
                webView.setVisibility(View.VISIBLE);
            }
            switchToZenView.setVisibility(View.GONE);
            webViewProgressBar.setVisibility(View.GONE);
        }
    }

    public class MyWebChromeClient extends WebChromeClient {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            webViewProgressBar.setProgress(newProgress);

            if(!onZenView && webView.getVisibility() != View.VISIBLE && newProgress >= 90) {
                if(switchToZenView.getVisibility() == View.VISIBLE) {
                    switchToZenView.setVisibility(View.GONE);
                }
                webView.setVisibility(View.VISIBLE);
            }
        }
    }

    public class ZenWebViewClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }

        public void onPageFinished(WebView view, String url) {
            if(onZenView && zenView.getVisibility() != View.VISIBLE) {
                zenView.setVisibility(View.VISIBLE);
            }
            zenViewProgressBar.setVisibility(View.GONE);
        }
    }

    public class ZenWebChromeClient extends WebChromeClient {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            zenViewProgressBar.setProgress(newProgress);

            if(onZenView && zenView.getVisibility() != View.VISIBLE && newProgress >= 50) {
                zenView.setVisibility(View.VISIBLE);
            }
        }
    }

    public void executeZen() {
        new loadZenPageTask().execute("");
    }

    public void back_Me(View v) {
        finish();
    }

    public void share_Me(View v) {
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, articleTitle + " " + articleUrl);
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Subject");
        startActivity(Intent.createChooser(sharingIntent, "Share using"));
    }

    public void openWebView(View v) {

        if(!webViewLoading) {
            webView.loadUrl(articleUrl);
        }

        if(zenView.getVisibility() == View.VISIBLE) {
            zenView.setVisibility(View.GONE);
        }
        if(zenViewProgressBar.getVisibility() == View.VISIBLE) {
            zenViewProgressBar.setVisibility(View.GONE);
        }

        if(webViewProgressBar.getProgress() == 100) {
            webView.setVisibility(View.VISIBLE);
        } else {
            webView.setVisibility(View.GONE);
            webViewProgressBar.setVisibility(View.VISIBLE);
            switchToZenView.setVisibility(View.VISIBLE);
        }

        onZenView = false;
        viewWebButton.setBackgroundColor(Color.parseColor("#FF3B9EFC"));
        viewZenButton.setBackgroundColor(Color.parseColor("#ff000000"));
    }

    public void openZenView(View v) {

        if(!zenViewLoading) {
            executeZen();
        }

        if(webView.getVisibility() == View.VISIBLE) {
            webView.setVisibility(View.GONE);
        }
        if(webViewProgressBar.getVisibility() == View.VISIBLE) {
            webViewProgressBar.setVisibility(View.GONE);
        }

        if(zenViewProgressBar.getProgress() == 100) {
            zenView.setVisibility(View.VISIBLE);
        } else {
            zenView.setVisibility(View.GONE);
            zenViewProgressBar.setVisibility(View.VISIBLE);
        }

        onZenView = true;
        switchToZenView.setVisibility(View.GONE);
        viewWebButton.setBackgroundColor(Color.parseColor("#ff000000"));
        viewZenButton.setBackgroundColor(Color.parseColor("#FF3B9EFC"));
    }

    public void getZenPageForArticle() {

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        try {
            HttpClient client = new DefaultHttpClient();
            client.getParams().setParameter(org.apache.http.params.CoreProtocolPNames.USER_AGENT, System.getProperty("http.agent"));
            String getURL = "https://informerly.com/api/v1/links/" + articleid + "?auth_token=" + sesionToken + "&content=true";

            HttpGet get = new HttpGet(getURL);
            HttpResponse responseGet = client.execute(get);
            resEntityGet = responseGet.getEntity();
            if (resEntityGet != null) {
                json = EntityUtils.toString(resEntityGet);
                JSONObject response = new JSONObject(json);
                zenArticleContent = response.getJSONObject("link").getString("content");
            }
        }
        catch (Exception e) {
            Toast.makeText(ArticleView.this, "Connection error", Toast.LENGTH_SHORT).show();
        }
    }

    private class loadZenPageTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            
            try {
                getZenPageForArticle();
            }
            catch(Exception e)
            {
                Toast.makeText(ArticleView.this,"Connection error",Toast.LENGTH_SHORT).show();
            }
            return "Executed";
        }

        @Override
        protected void onPostExecute(String result) {
//            zenView.loadDataWithBaseURL(null, zenArticleContent, "text/html", "utf-8", null);
            zenView.loadData(zenArticleContent, "text/html", "utf-8");
        }

        @Override
        protected void onPreExecute() {
        }
    }
}
