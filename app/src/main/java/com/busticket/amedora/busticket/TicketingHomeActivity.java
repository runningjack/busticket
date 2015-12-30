package com.busticket.amedora.busticket;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.busticket.amedora.busticket.model.Apps;
import com.busticket.amedora.busticket.model.Bus;
import com.busticket.amedora.busticket.model.Terminal;
import com.busticket.amedora.busticket.model.Ticket;
import com.busticket.amedora.busticket.utils.DatabaseHelper;
import com.busticket.amedora.busticket.utils.Installation;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeoutException;

/**
 * Created by Amedora on 12/4/2015.
 */
public class TicketingHomeActivity extends AppCompatActivity {
    DatabaseHelper db = new DatabaseHelper(this);
    Spinner spBoard, spBuses, spTrips, spHighlight;
    String[] dias;
    Button btnGenerate;
    String bus, board, highlight, trip;
    public static String TAG_NAME, TAG_SHORT_NAME;
    ArrayList<HashMap<String, String>> terminalList, busList, TicketList;
    public static final String  TAG ="My App";


    public static final String AUTHORITY = "com.busticket.amedora.busticket.app";
    // An account type, in the form of a domain name
    public static final String ACCOUNT_TYPE = "busticket.com";
    // The account name
    public static final String ACCOUNT = "dummyaccount";
    RequestQueue mQueue;
    // Instance fields
    Account mAccount;
    ContentResolver mResolver;
    public static final long SECONDS_PER_MINUTE = 2L;
    public static final long SYNC_INTERVAL_IN_MINUTES = 2L;
    public static final long SYNC_INTERVAL =SYNC_INTERVAL_IN_MINUTES * SECONDS_PER_MINUTE;
    Apps apps;
    Toolbar myToolbar;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.layout_ticket_home);
        myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        spBoard = (Spinner) findViewById(R.id.spBoard);
        spBuses = (Spinner) findViewById(R.id.spBusNo);
        spTrips = (Spinner) findViewById(R.id.spTripType);
        spHighlight = (Spinner) findViewById(R.id.spHighlight);
        mAccount = CreateSyncAccount(this);
        mQueue = Volley.newRequestQueue(getApplicationContext());
        insertTerminals();
        insertBuses();
        getTickets();
        apps = db.getApp(Installation.appId(getApplicationContext()));
        // Get the content resolver for your app
       mResolver = getContentResolver();
        /*
         * Turn on periodic syncing
         */
       ContentResolver.addPeriodicSync(CreateSyncAccount(this), AUTHORITY, Bundle.EMPTY, SYNC_INTERVAL);

        String[] tdata = populateTerminals();
        String[] bdata = populateBuses();
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, tdata);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spBoard.setAdapter(adapter);/**/

        ArrayAdapter Hadapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, tdata);
        Hadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spHighlight.setAdapter(Hadapter);

        ArrayAdapter gadapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, bdata);
        gadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spBuses.setAdapter(gadapter);

        ArrayAdapter<CharSequence> tadapter = ArrayAdapter.createFromResource(this, R.array.tripTypes, android.R.layout.simple_spinner_item);
        tadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        /*ArrayAdapter badapter =new ArrayAdapter(this,android.R.layout.simple_spinner_item,bdata);
        badapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);*/
        spTrips.setAdapter(tadapter);

        spBuses.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((TextView) parent.getChildAt(0)).setTextColor(Color.WHITE);
                //((TextView) parent.getChildAt(0)).setTextSize(25);
                bus = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spBoard.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((TextView) parent.getChildAt(0)).setTextColor(Color.WHITE);
                //((TextView) parent.getChildAt(0)).setTextSize(25);
                board = parent.getItemAtPosition(position).toString();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spHighlight.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((TextView) parent.getChildAt(0)).setTextColor(Color.WHITE);
                //((TextView) parent.getChildAt(0)).setTextSize(25);
                highlight = parent.getItemAtPosition(position).toString();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spTrips.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((TextView) parent.getChildAt(0)).setTextColor(Color.WHITE);
                //((TextView) parent.getChildAt(0)).setTextSize(25);
                trip = parent.getItemAtPosition(position).toString();

            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        btnGenerate = (Button) findViewById(R.id.btnGenerate);

        btnGenerate.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // get a new activity to show invoice preview
                sendData();
            }
        });
    }

    public String[] populateTerminals() {
        terminalList = new ArrayList<HashMap<String, String>>();
        List<Terminal> terminal = db.getAllTerminals();

        TicketList = new ArrayList<HashMap<String, String>>();
        List<Ticket> ticket = db.getAllTickets();
        dias = new String[terminal.size()];

        for (int i = 0; i < terminal.size(); i++) {
            Terminal s = terminal.get(i);
            // creating new HashMap
            HashMap<String, String> map = new HashMap<String, String>();
            // adding each child node to HashMap key => value

            map.put(TAG_SHORT_NAME, String.valueOf(s.getShort_name()));
            // adding HashList to ArrayList
            terminalList.add(map);
            // add sqlite id to array
            // used when deleting a website from sqlite
            dias[i] = String.valueOf(s.getShort_name());
        }
        return dias;
    }

    public String[] populateBuses() {
        busList = new ArrayList<HashMap<String, String>>();
        List<Bus> bus = db.getAllBuses();
        dias = new String[bus.size()];
        for (int i = 0; i < bus.size(); i++) {
            Bus s = bus.get(i);
            //creating new HashMap
            HashMap<String, String> map = new HashMap<String, String>();
            //adding each child node to HashMap key => value
            map.put(TAG_SHORT_NAME, String.valueOf(s.getPlate_no()));
            // adding HashList to ArrayList
            busList.add(map);
            // add sqlite id to array
            // used when deleting a website from sqlite
            dias[i] = String.valueOf(s.getPlate_no());
        }

        return dias;
    }



    private void sendData() {
        Intent intent = new Intent(TicketingHomeActivity.this, GenerateTicketActivity.class);
        intent.putExtra("Board", board);
        intent.putExtra("Highlight", highlight);
        intent.putExtra("Trip", trip);
        intent.putExtra("Bus", bus);
        startActivity(intent);
    }

    /**
     * Create a new dummy account for the sync adapter
     *
     * @param context The application context
     */
    public static Account CreateSyncAccount(Context context) {
        // Create the account type and default account
        Account newAccount = new Account(ACCOUNT, ACCOUNT_TYPE);
        // Get an instance of the Android account manager
        AccountManager accountManager = (AccountManager) context.getSystemService(ACCOUNT_SERVICE);
        /*
         * Add the account and account type, no password or user data
         * If successful, return the Account object, otherwise report an error.
         */
        if (accountManager.addAccountExplicitly(newAccount, null, null)) {
            /*
             * If you don't set android:syncable="true" in
             * in your <provider> element in the manifest,
             * then call context.setIsSyncable(account, AUTHORITY, 1)
             * here.
             */

        } else {
            /*
             * The account exists or some other error occurred. Log this, report it,
             * or handle it internally.
             */
            Log.d("SYNC ERR","The account exists or some other error occurred. Log this, report it,");
        }
        return newAccount;
    }

    private void insertTerminals(){
        //RequestQueue requestQueue = new RequestQueue(m)

        //mQueue = Volley.newRequestQueue(getApplicationContext());
        String url ="http://41.77.173.124:81/busticketAPI/terminals/index";
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET,url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try{
                    // Iterator<String> iter = response.keys();
                    int ja = response.length();
                    for(int i=0; i<ja; i++){
                        //String key = iter.next();
                        JSONObject term = (JSONObject) response.get(i);
                        //JSONArray jsonArrayTerminals = response.getJSONArray("data");
                        DatabaseHelper db = new DatabaseHelper(getApplicationContext());
                        Terminal t = db.getTerminalByName(term.getString("short_name"));
                        if(db.ifExists(t)){

                        }else{
                            Terminal terminal = new Terminal();
                            terminal.setTerminal_id(term.getInt("id"));
                            terminal.setShort_name(term.getString("short_name"));
                            terminal.setName(term.getString("name"));
                            terminal.setDistance(term.getString("distance"));
                            terminal.setOne_way_from_fare(term.getDouble("one_way_from_fare"));
                            terminal.setOne_way_to_fare(term.getDouble("one_way_to_fare"));
                            terminal.setDistance(term.getString("distance"));
                            terminal.setRoute_id(term.getInt("route_id"));
                            terminal.setGeodata(term.getString("geodata"));
                            db.createTerminal(terminal);
                        }
                    }
                }catch (Exception e){
                    VolleyLog.d(TAG, "Error: " + e.getMessage());
                    Toast.makeText(TicketingHomeActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                Toast.makeText(TicketingHomeActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
                // hide the progress dialog
                //pDialog.hide();
            }
        });
        mQueue.add(jsonArrayRequest);
    }

    private void insertBuses(){
        //RequestQueue requestQueue = new RequestQueue(m)


        String url ="http://41.77.173.124:81/busticketAPI/buses/index";
        JsonArrayRequest jsonArrayRequestBus = new JsonArrayRequest(Request.Method.GET,url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try{
                    // Iterator<String> iter = response.keys();
                    int ja = response.length();
                    for(int i=0; i<ja; i++){
                        //String key = iter.next();
                        JSONObject term = (JSONObject) response.get(i);
                        //JSONArray jsonArrayTerminals = response.getJSONArray("data");
                        DatabaseHelper db = new DatabaseHelper(getApplicationContext());
                        Bus b = db.getBusByPlateNo(term.getString("plate_no"));
                        if(db.ifExistsBus(b)){

                        }else{

                            Toast.makeText(TicketingHomeActivity.this, " Beginning Ticket Synchronization", Toast.LENGTH_SHORT).show();
                            Bus bus = new Bus();
                            bus.setBus_id(term.getInt("id"));
                            bus.setDriver(term.getString("driver"));
                            bus.setPlate_no(term.getString("plate_no"));
                            bus.setConductor(term.getString("conductor"));
                            bus.setRoute_id(term.getInt("route_id"));

                            long u = db.createBus(bus);
                            String numberAsString = new Double(u).toString();
                            String counte = new Double(i).toString();
                            Toast.makeText(TicketingHomeActivity.this,numberAsString+", "+counte, Toast.LENGTH_SHORT).show();
                        }

                    }

                }catch (Exception e){
                    VolleyLog.d(TAG, "Error: " + e.getMessage());
                    Toast.makeText(TicketingHomeActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                Toast.makeText(TicketingHomeActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
                // hide the progress dialog
                //pDialog.hide();
            }
        });
        mQueue.add(jsonArrayRequestBus);
    }

    private void getTickets(){
        String url ="http://41.77.173.124:81/busticketAPI/tickets/data/"+Installation.appId(getApplicationContext());
        JsonArrayRequest jsonArrayRequestTicket = new JsonArrayRequest(Request.Method.GET,url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try{
                    VolleyLog.d(TAG, "MSG: Beginning Ticket Synchronization " );
                    Toast.makeText(TicketingHomeActivity.this, " Beginning Ticket Synchronization", Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(TicketingHomeActivity.this,numberAsString+", "+counte, Toast.LENGTH_SHORT).show();
                        }

                    }
                }catch(Exception e){
                    VolleyLog.d(TAG, "Error: " + e.getMessage());
                    Toast.makeText(TicketingHomeActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                Toast.makeText(TicketingHomeActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
            }
        });
        jsonArrayRequestTicket.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mQueue.add(jsonArrayRequestTicket);
    }

    private void postTicket(){
        List<Ticket> usedTickets = db.getUsedTickets();
        int k =0;
        if(usedTickets.size()>=1){
            for(Ticket ticket : usedTickets){
                String url ="http://41.77.173.124:81/busticketAPI/tickets/update/"+ticket.getTicket_id()+"/1";
                JsonObjectRequest jUpdateTicket = new JsonObjectRequest(Request.Method.GET,url,new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try{
                            if(response.getString("msg") == "success"){
                                Toast.makeText(TicketingHomeActivity.this,"Record Updated ton server Successfully", Toast.LENGTH_SHORT).show();
                            }
                        }catch(Exception e){
                            Toast.makeText(TicketingHomeActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(TicketingHomeActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
                    }
                });
                mQueue.add(jUpdateTicket);
            }
        }

    }

    private void postTicketing(){
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("ticket_id", "AbCdEfGh123456");
        //JsonObjectRequest req = new JsonObjectRequest(URL, new JSONObject(params),
               // new Response.Listener<JSONObject>()
    }

    private void getAppBalance(){
        String url = "http://41.77.173.124:81/busticketAPI/account/update/"+apps.getApp_id()+"/"+apps;
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,url,new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try{
                    if(response.getString("msg") == "success"){
                        Toast.makeText(TicketingHomeActivity.this,"Record Updated ton server Successfully", Toast.LENGTH_SHORT).show();
                    }
                }catch(Exception e){
                    Toast.makeText(TicketingHomeActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        },new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
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

        return super.onOptionsItemSelected(item);
    }

}
