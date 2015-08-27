package com.informerly.informer.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.informerly.informer.Article;
import com.informerly.informer.FeedView;
import com.informerly.informer.R;

import java.util.ArrayList;

public class ArticleObjectAdapter extends ArrayAdapter<Article> {

    protected Context mContext;
    protected ArrayList<Article> mItems;

    public ArticleObjectAdapter(Context context, ArrayList<Article> items) {
        super(context, R.layout.row, items);
        mContext = context;
        mItems = items;
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView == null){
            convertView = LayoutInflater.from(FeedView.getContext()).inflate(R.layout.row,null);
        }

        TextView readview = ((TextView) convertView.findViewById(R.id.read));
        TextView titleview = ((TextView) convertView.findViewById(R.id.title));
        ImageView clockview =((ImageView) convertView.findViewById(R.id.clock));
        TextView sourceview = ((TextView) convertView.findViewById(R.id.source));
        ImageView bookmark =((ImageView) convertView.findViewById(R.id.bookmark));

        titleview.setText(mItems.get(position).getTitle());
        sourceview.setText(mItems.get(position).getSource());

        if( mItems.get(position).getSource_color()!=null ) {
            sourceview.setTextColor(Color.parseColor(mItems.get(position).getSource_color()));
        }

        sourceview.setTypeface(null, Typeface.BOLD);

        if(mItems.get(position).isRead()) {
            readview.setText("Read");
            titleview.setTypeface(null, Typeface.NORMAL);
            titleview.setTextColor(Color.GRAY);
            clockview.setImageResource(R.drawable.tick);
        } else {
            titleview.setTextColor(Color.BLACK);
            titleview.setTypeface(null, Typeface.BOLD);
            clockview.setImageResource(R.drawable.clock);
            readview.setText(mItems.get(position).getReading_time() + " min read");
        }

        bookmark.bringToFront();
        if(mItems.get(position).isBookmarked()) {
            bookmark.setVisibility(View.VISIBLE);
        } else {
            bookmark.setVisibility(View.INVISIBLE);
        }

        return convertView;
    }

}
