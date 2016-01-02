package com.busticket.amedora.busticketsrl.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.busticket.amedora.busticketsrl.R;
import com.busticket.amedora.busticketsrl.model.Ticket;

import java.util.ArrayList;

/**
 * Created by USER on 12/30/2015.
 */
public class CustomListAdapterTickets extends BaseAdapter {
    private ArrayList<Ticket> ticketData;
    private LayoutInflater layoutInflater;
    private Context context;

    public CustomListAdapterTickets(Context aContext, ArrayList<Ticket> ticketData){
        this.ticketData = ticketData;
        layoutInflater = LayoutInflater.from(aContext);
    }

    @Override
    public int getCount() {
        return ticketData.size();
    }

    @Override
    public Object getItem(int position) {
        return ticketData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent){
        ViewHolder holder;
        if(convertView == null){

            convertView = layoutInflater.inflate(R.layout.layout_ticket_list_item,null);
            holder = new ViewHolder();
            holder.ticketNoView = (TextView) convertView.findViewById(R.id.tvTicketNumberListItem);
            holder.ticketDateView = (TextView) convertView.findViewById(R.id.tvTicketDateListItem);
            holder.ticketStatusView = (TextView) convertView.findViewById(R.id.tvTicketStatusListItem);
            convertView.setTag(holder);

        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        Ticket objectItem = ticketData.get(position);

        holder.ticketNoView.setText(ticketData.get(position).getSerial_no());

        holder.ticketStatusView.setText(objectItem.getTicket_type());
        holder.ticketDateView.setText(ticketData.get(position).getSerial_no());
        return  convertView;
    }

    static class ViewHolder {
        TextView ticketNoView;
        TextView ticketStatusView;
        TextView ticketDateView;
    }

}
