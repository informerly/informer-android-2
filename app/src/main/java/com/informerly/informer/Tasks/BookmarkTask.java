package com.informerly.informer.Tasks;

import android.os.AsyncTask;
import android.os.StrictMode;
import android.widget.Toast;

import com.informerly.informer.APICalls.Bookmark;
import com.informerly.informer.APICalls.GetZenContent;
import com.informerly.informer.FeedView;
import com.informerly.informer.Util.JSONSharedPreferences;

import org.apache.http.HttpEntity;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

public class BookmarkTask extends AsyncTask<String, Void, String> {

    @Override
    protected String doInBackground(String... params) {
        try {
            String articleId = params[0];
            Boolean isBookmarked = Boolean.valueOf(params[1]);
            bookmarkArticle(articleId, isBookmarked);
        }
        catch(Exception e)
        {
            Toast.makeText(FeedView.getContext(), "Connection error", Toast.LENGTH_SHORT).show();
        }

        return "Executed";
    }

    @Override
    protected void onPostExecute(String result) {
    }

    @Override
    protected void onPreExecute() {
    }

    public void bookmarkArticle(String articleId, Boolean bookmarked) {

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        try {
            HttpEntity resEntityGet = new Bookmark(articleId,bookmarked).bookmark();

            if (resEntityGet != null) {
                String json = EntityUtils.toString(resEntityGet);
            } else {
                Toast.makeText(FeedView.getContext(),"Connection non response",Toast.LENGTH_SHORT).show();
            }

            if(!bookmarked) {
                try {
                    resEntityGet = new GetZenContent(articleId).getContent();
                    if (resEntityGet != null) {

                        String jsonArticle = EntityUtils.toString(resEntityGet);
                        JSONObject response = new JSONObject(jsonArticle);
                        jsonArticle = response.getString("link");
                        JSONSharedPreferences.saveJSONString(FeedView.getContext(), "savedArticles", articleId, jsonArticle);
                    }
                }
                catch(Exception e) {
                    Toast.makeText(FeedView.getContext(),"Connection error",Toast.LENGTH_SHORT).show();
                }
            } else {
                JSONSharedPreferences.remove(FeedView.getContext(),"savedArticles",articleId);
            }

        } catch (Exception e) {
            Toast.makeText(FeedView.getContext(),"Connection error",Toast.LENGTH_SHORT).show();
        }
    }
}