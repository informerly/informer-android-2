package com.informerly.informer.APICalls;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

public class GetZenContent {
    String url;
    HttpEntity resEntity = null;

    public GetZenContent(String token, String articleId) {
        this.url = "https://informerly.com/api/v1/links/" + articleId + "?auth_token=" + token + "&content=true";
    }

    public HttpEntity getContent() {
        try {
            HttpClient client = new DefaultHttpClient();
            client.getParams().setParameter(org.apache.http.params.CoreProtocolPNames.USER_AGENT, System.getProperty("http.agent"));
            HttpGet get = new HttpGet(url);
            HttpResponse response = client.execute(get);
            resEntity = response.getEntity();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return resEntity;
    }
}
