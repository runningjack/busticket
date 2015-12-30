package com.busticket.amedora.busticket;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.busticket.amedora.busticket.model.Ticket;
import com.busticket.amedora.busticket.utils.DatabaseHelper;
import com.busticket.amedora.busticket.utils.Installation;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by Amedora on 12/29/2015.
 */
public class TicketListActivity extends AppCompatActivity {
    ListView listTicks;Button btnRefresh,btnSync;
    RequestQueue mQueue;
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_ticket_list);
        mQueue = Volley.newRequestQueue(getApplicationContext());
        listTicks =(ListView)findViewById(R.id.lvTicket);
        btnRefresh = (Button)findViewById(R.id.btnRefreshTicket);
        btnSync =(Button)findViewById(R.id.btnSyncTicket);

    }

    private void getTickets(){
        String url ="http://41.77.173.124:81/busticketAPI/tickets/data/"+ Installation.appId(getApplicationContext());
        JsonArrayRequest jsonArrayRequestTicket = new JsonArrayRequest(Request.Method.GET,url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try{
                    VolleyLog.d("TICKETLIST", "MSG: Beginning Ticket Synchronization ");
                    Toast.makeText(TicketListActivity.this, " Beginning Ticket Synchronization", Toast.LENGTH_SHORT).show();
                    int ja = response.length();

                    for(int w=0; w<ja; w++){
                        // String key = iter.next();
                        JSONObject jsonObject = (JSONObject) response.get(w);
                        DatabaseHelper db = new DatabaseHelper(getApplicationContext());
                        Ticket t = db.getTicketBySerialNo(jsonObject.getString("serial_no"));
                        if(!db.ifExists(t)){
                            Ticket ticket = new Ticket();
                            ticket.setTicket_id(jsonObject.getInt("id"));
                            ticket.setSerial_no(jsonObject.getString("serial_no"));
                            ticket.setBatch_code(jsonObject.getString("stack_id"));
                            ticket.setRoute_id(jsonObject.getInt("route_id"));
                            ticket.setStatus(jsonObject.getInt("status"));
                            ticket.setAmount(jsonObject.getDouble("amount"));
                            ticket.setScode(jsonObject.getString("code"));
                            ticket.setTerminal_id(jsonObject.getInt("terminal_id"));
                            ticket.setTicket_type(jsonObject.getString("ticket_type"));
                            long u = db.createTicket(ticket);
                            String numberAsString = new Double(u).toString();
                            String counte = new Double(w).toString();
                            Toast.makeText(TicketListActivity.this,numberAsString+", "+counte, Toast.LENGTH_SHORT).show();
                        }

                    }
                }catch(Exception e){
                    VolleyLog.d("TICKETLIST", "Error: " + e.getMessage());
                    Toast.makeText(TicketListActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("TICKETLIST", "Error: " + error.getMessage());
                Toast.makeText(TicketListActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
            }
        });
        jsonArrayRequestTicket.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mQueue.add(jsonArrayRequestTicket);
    }
}
