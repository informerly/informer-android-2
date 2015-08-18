package com.informerly.informer.APICalls;

import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;


public class MarkRead {
    String token, id, articleid;
    static String baseurl ;
    HttpEntity resEntity = null;

    public MarkRead(String tooken, String userid, String feedid) {
        this.token = tooken;
        this.id = userid;
        this.articleid = feedid;
        baseurl = "https://informerly.com/api/v1/links/"+articleid+"/read?auth_token=" + token + "&client_id=" + id;
    }

    public HttpEntity mark() {
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