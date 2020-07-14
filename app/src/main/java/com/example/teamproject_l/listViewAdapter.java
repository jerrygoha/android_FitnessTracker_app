package com.example.teamproject_l;


import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class listViewAdapter extends BaseAdapter {
    private ArrayList<listViewItem> listViewItemList = new ArrayList<listViewItem>() ;

    public listViewAdapter() {

    }

    @Override
    public int getCount() {
        return listViewItemList.size() ;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int pos = position;
        final Context context = parent.getContext();


        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.music_info_item, parent, false);
        }


        ImageView albumArt = (ImageView) convertView.findViewById(R.id.item_imageView) ;
        TextView musicTitle = (TextView) convertView.findViewById(R.id.item_textView) ;
        TextView musicArtist = (TextView) convertView.findViewById(R.id.item_textView2);


        listViewItem listViewItem = listViewItemList.get(position);


        albumArt.setImageBitmap(listViewItem.getImage());
        musicTitle.setText(listViewItem.getTitle());
        musicArtist.setText(listViewItem.getArtist());


        return convertView;
    }


    @Override
    public long getItemId(int position) {
        return position ;
    }


    @Override
    public Object getItem(int position) {
        return listViewItemList.get(position) ;
    }


    public void addItem(Bitmap image, String title, String artist) {
        listViewItem item = new listViewItem();
        item.setImage(image);
        item.setTitle(title);
        item.setArtist(artist);

        listViewItemList.add(item);
    }
}