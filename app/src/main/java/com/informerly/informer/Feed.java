package com.informerly.informer;

import org.json.JSONException;
import org.json.JSONObject;

public class Feed {

    /*
    {
        "id":89,"name":"Products","magic":"products","primary":false,"user_id":461
    }
    */

    int id;
    String name;
    String magic;
    boolean primary;
    int user_id;

    public Feed(
            int id,
            String name,
            String magic,
            boolean primary,
            int user_id
    )
    {
        this.id = id;
        this.name = name;
        this.magic = magic;
        this.primary = primary;
        this.user_id = user_id;
    }

    public Feed( JSONObject jsonFeed ) {
        try {
            this.id = jsonFeed.getInt("id");
            this.name = jsonFeed.getString("name");
            this.magic = jsonFeed.getString("magic");
            this.primary = jsonFeed.getBoolean("primary");
            this.user_id = jsonFeed.getInt("user_id");
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
