package com.busticket.amedora.busticketsrl;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.busticket.amedora.busticketsrl.model.Ticket;
import com.busticket.amedora.busticketsrl.utils.CustomListAdapterTickets;
import com.busticket.amedora.busticketsrl.utils.DatabaseHelper;
import com.busticket.amedora.busticketsrl.utils.Installation;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Amedora on 12/29/2015.
 */
public class TicketListActivity extends AppCompatActivity {
    // listTicks;
    Button btnRefresh,btnSync;
    RequestQueue hQueue;
    Toolbar myToolbar;
    TextView tvTLeft;
    ListView listTicks;

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_ticket_list);
        myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
       // getActionBar().setDisplayHomeAsUpEnabled(true);
        hQueue = Volley.newRequestQueue(getApplicationContext());
        listTicks =(ListView)findViewById(R.id.lvTicket);
        btnRefresh = (Button)findViewById(R.id.btnRefreshTicket);
        btnSync =(Button)findViewById(R.id.btnSyncTicket);
        tvTLeft = (TextView) findViewById(R.id.tvTicketLeft);
        btnSync.setOnClickListener(onSyncClick);
        btnRefresh.setOnClickListener(onRefreshClick);

        getTickets();
        ArrayList ticketL = getListData();

        listTicks.setAdapter(new CustomListAdapterTickets(TicketListActivity.this, ticketL));

        listTicks.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Object o = listTicks.getItemAtPosition(i);
                Ticket newTicket = (Ticket) o;
            }
        });
        String ticCount = Long.toString(listTicks.getAdapter().getCount()) + " Tickets Left";
        tvTLeft.setText(ticCount);
    }

    private ArrayList getListData() {
        ArrayList<Ticket> results = new ArrayList<Ticket>();

        DatabaseHelper dbTicks = new DatabaseHelper(getApplicationContext());

        ArrayList<Ticket> mTickets = dbTicks.getUnusedTickets();

        for (int i = 0; i < mTickets.size(); i++) {
            Ticket ticketData = new Ticket();
            Ticket s = mTickets.get(i);
            ticketData.setRoute_id(s.getRoute_id());
            ticketData.setTicket_type(s.getTicket_type());
            ticketData.setSerial_no(s.getSerial_no());
            results.add(ticketData);
        }

        // Add some more dummy data for testing
        return results;
    }

    private View.OnClickListener onSyncClick = new View.OnClickListener() {
        public void onClick(View v) {
            getTickets();
        }
    };

    private View.OnClickListener onRefreshClick = new View.OnClickListener() {
        public void onClick(View v) {
            getTickets();
        }
    };

    private void getTickets(){
        String url ="http://41.77.173.124:81/busticketAPI/tickets/data/"+Installation.appId(getApplicationContext());
        JsonArrayRequest jsonArrayRequestTicket = new JsonArrayRequest(Request.Method.GET,url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try{
                    Toast.makeText(TicketListActivity.this, " Beginning Ticket Synchronization", Toast.LENGTH_SHORT).show();
                    int ja = response.length();
                    if(ja >1){
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
                                Toast.makeText(TicketListActivity.this,numberAsString+", "+counte, Toast.LENGTH_LONG).show();
                            }
                        }
                    }else{
                        String gg  ="Please load your account \r\n" ;
                        gg +="there is no ticket in your account";
                        Toast.makeText(TicketListActivity.this,gg, Toast.LENGTH_LONG).show();
                    }
                }catch(Exception e){
                    Toast.makeText(TicketListActivity.this, e.toString(), Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(TicketListActivity.this, error.toString(), Toast.LENGTH_LONG).show();
            }
        });

        int socketTimeout = 30000; //30 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsonArrayRequestTicket.setRetryPolicy(policy);
        hQueue.add(jsonArrayRequestTicket);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        switch (item.getItemId()) {
            case R.id.action_tickets:
                Intent intent = new Intent(TicketListActivity.this,TicketListActivity.class);
                startActivity(intent);
                break;
            case R.id.action_ticketing:
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
