package com.busticket.amedora.busticketsrl.utils;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

/**
 * Created by USER on 1/4/2016.
 */
public class DrawerItemClickListener implements ListView.OnItemClickListener {
    public void onItemClick(AdapterView<?> parent,View view, int position, long id){
        selectedItem(position);
    }
    public void selectedItem(int position){

    }
}
