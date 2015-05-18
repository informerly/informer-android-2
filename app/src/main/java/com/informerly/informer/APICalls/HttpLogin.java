package com.informerly.informer.APICalls;

import android.os.StrictMode;
import android.widget.EditText;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import java.util.ArrayList;
import java.util.List;

public class HttpLogin {
    EditText passwords, emails;
    HttpEntity resEntity;
    String username, password;

//    private static Context mContext;

    public HttpLogin(EditText email, EditText password) {
        this.passwords = password;
        this.emails = email;
    }

    public HttpEntity getEntity() {
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        try {
            HttpClient client = new DefaultHttpClient();
            client.getParams().setParameter(org.apache.http.params.CoreProtocolPNames.USER_AGENT, System.getProperty("http.agent"));
            String postURL = "http://informerly.com/api/v1/users/sign_in";
            HttpPost post = new HttpPost(postURL);
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            username = emails.getText().toString();
            password = passwords.getText().toString();

            params.add(new BasicNameValuePair("login", username));
            params.add(new BasicNameValuePair("password", password));
            UrlEncodedFormEntity ent = new UrlEncodedFormEntity(params, HTTP.UTF_8);
            post.setEntity(ent);
            HttpResponse responsePOST = client.execute(post);
            resEntity = responsePOST.getEntity();
        } catch (Exception e) {
        }
        return resEntity;
    }
}