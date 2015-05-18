package com.informerly.informer.APICalls;

import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import java.util.ArrayList;
import java.util.List;


public class MarkRead {
    String token, id,articleid;
    static String baseurl ;
    public MarkRead(String tooken, String userid, String feedid) {
        this.token = tooken;
        this.id = userid;
        this.articleid = feedid;
        baseurl = "http://informerly.com/api/v1/links/"+articleid+"/read";
    }

    public void mark() {
        try {
            HttpClient client = new DefaultHttpClient();
            client.getParams().setParameter(org.apache.http.params.CoreProtocolPNames.USER_AGENT, System.getProperty("http.agent"));
            HttpPost post = new HttpPost(baseurl);
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("auth_token",token));
            params.add(new BasicNameValuePair("client_id",id));
            UrlEncodedFormEntity ent = new UrlEncodedFormEntity(params, HTTP.UTF_8);
            post.setEntity(ent);
            client.execute(post);
        } catch (Exception e) {
        }
    }
}

