package com.udemy.ac_twitterclone;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class DisplayTweetAdapter extends ArrayAdapter<DisplayTweet> {

    private Context context;

    public DisplayTweetAdapter(Context context, ArrayList<DisplayTweet> displayTweets){
        super(context,0,displayTweets);
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        DisplayTweet displayTweet = getItem(position);

        if(convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_display_tweet,parent,false);
        }

        TextView txtHeader = convertView.findViewById(R.id.itemDisplayTweetHeader);
        TextView txtMessage = convertView.findViewById(R.id.itemDisplayTweetMessage);

        txtHeader.setText(displayTweet.getHeader());
        txtMessage.setText(displayTweet.getMessage());



        return convertView;
    }
}
