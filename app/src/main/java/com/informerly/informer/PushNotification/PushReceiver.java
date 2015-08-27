package com.informerly.informer.PushNotification;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.NotificationCompat;

import com.parse.ParsePushBroadcastReceiver;
import com.informerly.informer.ArticleView;
import com.informerly.informer.FeedView;
import com.informerly.informer.R;

import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;

public class PushReceiver extends ParsePushBroadcastReceiver {

    SharedPreferences sharedpreferences;

    @Override
    public void onReceive(Context context, Intent intent) {

        try {
            JSONObject json = new JSONObject(intent.getExtras().getString("com.parse.Data"));

            if(json.has("link_id") && json.has("link_url")) {

                String articleId = json.getString("link_id");
                String articleUrl = json.getString("link_url");

                String articleTitle;
                if(json.getJSONObject("aps").has("alert")) {
                    articleTitle = json.getJSONObject("aps").getString("alert");
                } else {
                    articleTitle = "News received!";
                }

                Intent i = new Intent(context, ArticleView.class);
                i.putExtra("articleTitle", articleTitle);
                i.putExtra("articleId", articleId);
                i.putExtra("articleUrl", articleUrl);

                PendingIntent contentIntent = PendingIntent.getActivity(context, 0, i, 0);

                NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                        .setContentTitle("Informer")
                        .setSmallIcon(R.drawable.icon)
                        .setContentText(articleTitle)
                        .setDefaults(NotificationCompat.DEFAULT_ALL)
                        .setAutoCancel(true)
                        .setContentIntent(contentIntent);

                NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                manager.notify("Informer" + articleId, 1, mBuilder.build());

                if (FeedView.notificationReceiver != null) {
                    FeedView.notificationReceiver.notificationReceived();
                } else {
                    sharedpreferences = context.getSharedPreferences("informer_message", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    editor.putBoolean("feed_update", true);
                    editor.apply();
                }
            } else {
                Log.e("Push Notifications", "Not article id or url received in push.");
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
