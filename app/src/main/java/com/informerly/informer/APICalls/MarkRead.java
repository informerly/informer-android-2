package com.informerly.informer.APICalls;

import org.apache.http.HttpEntity;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.HttpResponse;


public class MarkRead {
    static String baseurl ;
    HttpEntity resEntity = null;

    public MarkRead(String token, String userId, String feedid, Boolean read) {
        if(read) {
            baseurl = "https://informerly.com/api/v1/links/"+feedid+"/mark_as_unread?auth_token=" + token + "&client_id=" + userId;
        } else {
            baseurl = "https://informerly.com/api/v1/links/"+feedid+"/read?auth_token=" + token + "&client_id=" + userId;
        }
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