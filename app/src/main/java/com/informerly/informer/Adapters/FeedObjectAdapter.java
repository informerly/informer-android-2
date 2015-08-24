package com.informerly.informer.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.informerly.informer.Feed;
import com.informerly.informer.FeedView;
import com.informerly.informer.R;

import java.util.ArrayList;

public class FeedObjectAdapter extends ArrayAdapter<Feed> {

    protected Context mContext;
    protected ArrayList<Feed> mItems;

    public FeedObjectAdapter(Context context, ArrayList<Feed> feeds) {
        super(context, R.layout.menu_row, feeds);
        mContext = context;
        mItems = feeds;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            convertView = LayoutInflater.from(FeedView.getContext()).inflate(R.layout.menu_row,null);
        }
        TextView feedNameview = ((TextView) convertView.findViewById(R.id.feedTitle));
        ImageView feedIconview =((ImageView) convertView.findViewById(R.id.feedIcon));

        feedNameview.setText(mItems.get(position).getName());
        if(mItems.get(position).getName().equals("Your Feed")) {
            feedIconview.setImageResource(R.drawable.home);
        } else {
            feedIconview.setImageResource(R.drawable.folder);
        }

        return convertView;
    }

}
