package com.informerly.informer.Tasks;

import android.os.AsyncTask;
import android.os.StrictMode;
import android.widget.Toast;

import com.informerly.informer.APICalls.MarkRead;
import com.informerly.informer.FeedView;

import org.apache.http.HttpEntity;
import org.apache.http.util.EntityUtils;

public class MarkReadTask extends AsyncTask<String, Void, String> {

    @Override
    protected String doInBackground(String... params) {
        try {
            String articleId = params[0];
            Boolean isRead = Boolean.valueOf(params[1]);
            markArticle(articleId, isRead);
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

    public void markArticle(String articleId, Boolean read) {

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        try {
            HttpEntity resEntityGet = new MarkRead(articleId,read).mark();

            if (resEntityGet != null) {
                String json = EntityUtils.toString(resEntityGet);
            } else {
                Toast.makeText(FeedView.getContext(),"Connection non response",Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(FeedView.getContext(),"Connection error",Toast.LENGTH_SHORT).show();
        }
    }
}
