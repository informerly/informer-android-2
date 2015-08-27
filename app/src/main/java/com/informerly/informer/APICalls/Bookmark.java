package com.informerly.informer.APICalls;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.informerly.informer.FeedView;

import org.apache.http.HttpEntity;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.HttpResponse;

public class Bookmark {
    String token, id, articleid;
    static String baseurl ;
    HttpEntity resEntity = null;
    SharedPreferences sharedpreferences;

    public Bookmark(String feedid, Boolean bookmarked) {

        sharedpreferences = FeedView.getContext().getSharedPreferences("user_session", Context.MODE_PRIVATE);
        this.token = sharedpreferences.getString("token", null);;
        this.id = sharedpreferences.getString("userid", null);;
        this.articleid = feedid;
        baseurl = "https://informerly.com/api/v1/bookmarks?link_id=" + articleid + "&auth_token=" + token + "&client_id=" + id;
    }

    public HttpEntity bookmark() {
        try {
            HttpClient client = new DefaultHttpClient();
            client.getParams().setParameter(org.apache.http.params.CoreProtocolPNames.USER_AGENT, System.getProperty("http.agent"));
            HttpPost post = new HttpPost(baseurl);
            HttpResponse response = client.execute(post);
            resEntity = response.getEntity();
            return resEntity;

        } catch (Exception e) {
            return null;
        }
    }
}