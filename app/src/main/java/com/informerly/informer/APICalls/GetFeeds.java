package com.informerly.informer.APICalls;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

public class GetFeeds {
    String token, id;
    HttpEntity resEntityGet;

    public GetFeeds(String tooken, String userid) {
        this.token = tooken;
        this.id = userid;
    }
    public HttpEntity getUserFeeds()
    {
        try {
            HttpClient client = new DefaultHttpClient();
            client.getParams().setParameter(org.apache.http.params.CoreProtocolPNames.USER_AGENT, System.getProperty("http.agent"));
            String getURL = "https://informerly.com/api/v1/users/feeds?auth_token=" + token + "&client_id=" + id;
            HttpGet get = new HttpGet(getURL);
            HttpResponse responseGet = client.execute(get);
            resEntityGet = responseGet.getEntity();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return resEntityGet;
    }
}
