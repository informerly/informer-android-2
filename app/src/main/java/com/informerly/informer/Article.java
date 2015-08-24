package com.informerly.informer;

import org.json.JSONException;
import org.json.JSONObject;
import android.util.Log;

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

    private int id;
    private String title;
    private String description;
    private int reading_time;
    private String source;
    private String published_at;
    private String original_date;
    private String slug;
    private String url;
    private boolean read;
    private boolean bookmarked;
    private String source_color;

    // for saved articles
    private String content = null;

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
            String source_color,
            String content
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
        this.content = content;
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

            if(jsonArticle.has("content")) {
                this.content = jsonArticle.getString("content");
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public int getReading_time() {
        return reading_time;
    }

    public String getSource() {
        return source;
    }

    public String getPublished_at() {
        return published_at;
    }

    public String getOriginal_date() {
        return original_date;
    }

    public String getSlug() {
        return slug;
    }

    public String getUrl() {
        return url;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public boolean isBookmarked() {
        return bookmarked;
    }

    public void setBookmarked(boolean bookmarked) {
        this.bookmarked = bookmarked;
    }

    public String getSource_color() {
        return source_color;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "Article{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", reading_time=" + reading_time +
                ", source='" + source + '\'' +
                ", published_at='" + published_at + '\'' +
                ", original_date='" + original_date + '\'' +
                ", slug='" + slug + '\'' +
                ", url='" + url + '\'' +
                ", read=" + read +
                ", bookmarked=" + bookmarked +
                ", source_color='" + source_color + '\'' +
                ", content='" + ((content!=null)?content:"") + '\'' +
                '}';
    }
}
