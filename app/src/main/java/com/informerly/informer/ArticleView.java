package com.informerly.informer;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.Toast;

import com.informerly.informer.APICalls.GetZenContent;
import com.informerly.informer.Util.JSONSharedPreferences;

import org.apache.http.HttpEntity;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import android.util.Log;

public class ArticleView extends ActionBarActivity {
    
    WebView webView,zenView;
    RadioButton viewZenButton,viewWebButton;
    String articleUrl, articleTitle, userId, sesionToken, useremail, articleid, json, zenArticleContent;
    ProgressBar webViewProgressBar,zenViewProgressBar;
    LinearLayout switchToZenView;
    boolean onZenView = false;
    SharedPreferences sharedpreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.article_view);
        Intent intent = getIntent();

        sharedpreferences = this.getSharedPreferences("user_session", Context.MODE_PRIVATE);
        sesionToken = sharedpreferences.getString("token", null);
        userId = sharedpreferences.getString("userid", null);
        useremail = sharedpreferences.getString("useremail", null);

        // get article params
        articleid = intent.getStringExtra("articleId");
        articleUrl = intent.getStringExtra("articleUrl");
        articleTitle = intent.getStringExtra("articleTitle");

        webView = (WebView) findViewById(R.id.webview);
        zenView = (WebView) findViewById(R.id.webviewZen);

        viewWebButton = (RadioButton) findViewById(R.id.web);
        viewZenButton = (RadioButton) findViewById(R.id.zen);

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

        // loading article
        executeZen();
        webView.loadUrl(articleUrl);

        // Getting user preferences
        SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        Boolean defaultZenPreference = SP.getBoolean("zenViewPreference", false);

        if(defaultZenPreference) {
            openZenView(this.findViewById(android.R.id.content));
        } else {
            openWebView(this.findViewById(android.R.id.content));
        }

    }

    public class MyWebViewClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
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

        try {
            JSONObject article = JSONSharedPreferences.getJSONObject(ArticleView.this, "savedArticles", String.valueOf(articleid));
            if(article.has("content")) {
                zenArticleContent = article.getString("content");
                zenView.loadData(zenArticleContent, "text/html", "utf-8");
            } else {
                new loadZenPageTask().execute(articleid);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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
    }

    public void openZenView(View v) {

        viewZenButton.setChecked(true);

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
    }

    private class loadZenPageTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String articleId = params[0];

            try {
                HttpEntity resEntityGet = new GetZenContent(sesionToken, articleId).getContent();

                if (resEntityGet != null) {
                    json = EntityUtils.toString(resEntityGet);
                    JSONObject response = new JSONObject(json);
                    zenArticleContent = response.getJSONObject("link").getString("content");
                }
            }
            catch(IOException | JSONException e) {
                e.printStackTrace();
                Toast.makeText(ArticleView.this,"Connection error",Toast.LENGTH_SHORT).show();
            }

            return "Executed";
        }

        @Override
        protected void onPostExecute(String result) {
            zenView.loadData(zenArticleContent, "text/html", "utf-8");
        }

        @Override
        protected void onPreExecute() {
        }
    }
}
