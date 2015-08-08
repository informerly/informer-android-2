package com.informerly.informer;

import org.json.JSONException;
import org.json.JSONObject;

public class Article {

    /*
    ACTUAL ARTICLE MODEL API RESPONSE
    {
        "id":38685,
        "title":"Web Design - The First 100 Years",
        "description":"This is the expanded version of a talk I gave on September 9, 2014, at the HOW Interactive Design conference in Washington, DC.",
        "reading_time":2,
        "source":"Idlewords",
        "published_at":"2015-07-21T18:38:02.440-04:00",
        "original_date":null,
        "slug":"web-design-the-first-100-years",
        "url":"http://idlewords.com/talks/web_design_first_100_years.htm?curator=Informerly",
        "read":true,
        "bookmarked":false,
        "source_color":"#ae1414"
    }
    */

    int id;
    String title;
    String description;
    int reading_time;
    String source;
    String published_at;
    String original_date;
    String slug;
    String url;
    boolean read;
    boolean bookmarked;
    String source_color;

    public Article(
            int id,
            String title,
            String description,
            int reading_time,
            String source,
            String published_at,
            String original_date,
            String slug,
            String url,
            boolean read,
            boolean bookmarked,
            String source_color
    )
    {

        this.id = id;
        this.title = title;
        this.description = description;
        this.reading_time = reading_time;
        this.source = source;
        this.published_at = published_at;
        this.original_date = original_date;
        this.slug = slug;
        this.url = url;
        this.read = read;
        this.bookmarked = bookmarked;
        this.source_color = source_color;

    }

    public Article( JSONObject jsonArticle ) {
        try {
            this.id = jsonArticle.getInt("id");
            this.title = jsonArticle.getString("title");
            this.description = jsonArticle.getString("description");
            this.reading_time = jsonArticle.getInt("reading_time");
            this.source = jsonArticle.getString("source");
            this.published_at = jsonArticle.getString("published_at");
            this.original_date = jsonArticle.getString("original_date");
            this.slug = jsonArticle.getString("slug");
            this.url = jsonArticle.getString("url");
            this.read = jsonArticle.getBoolean("read");
            this.bookmarked = jsonArticle.getBoolean("bookmarked");
            this.source_color = jsonArticle.getString("source_color");
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
