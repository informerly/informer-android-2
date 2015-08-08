package com.informerly.informer.APICalls;

import android.os.StrictMode;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.impl.client.DefaultHttpClient;


public class HttpLogout {
    String token, id;
    HttpEntity resEntity;

    public HttpLogout(String token, String userid) {
        this.token = token;
        this.id = userid;
    }

    public HttpEntity getEntity() {
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        try {

            HttpClient client = new DefaultHttpClient();
            client.getParams().setParameter(org.apache.http.params.CoreProtocolPNames.USER_AGENT, System.getProperty("http.agent"));
            String postURL = "https://informerly.com/api/v1/users/sign_out";
            HttpDelete delete = new HttpDelete(postURL);
            delete.setHeader("auth_token", token);
            delete.setHeader("client_id", id);
            HttpResponse responsePOST = client.execute(delete);
            resEntity = responsePOST.getEntity();
        } catch (Exception e) {
        }
        return resEntity;
    }
}

