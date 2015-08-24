package com.informerly.informer;

import org.json.JSONException;
import org.json.JSONObject;

public class Feed {

    /*
    {
        "id":89,"name":"Products","magic":"products","primary":false,"user_id":461
    }
    */

    private int id;
    private String name;
    private String magic;
    private boolean primary;
    private int user_id;

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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMagic() {
        return magic;
    }

    public void setMagic(String magic) {
        this.magic = magic;
    }

    public boolean isPrimary() {
        return primary;
    }

    public void setPrimary(boolean primary) {
        this.primary = primary;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

}
