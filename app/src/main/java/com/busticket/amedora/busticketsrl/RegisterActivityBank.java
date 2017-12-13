package com.busticket.amedora.busticketsrl;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.busticket.amedora.busticketsrl.model.Apps;
import com.busticket.amedora.busticketsrl.model.Route;
import com.busticket.amedora.busticketsrl.model.Terminal;
import com.busticket.amedora.busticketsrl.utils.*;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Amedora on 7/30/2015.
 */
public class RegisterActivityBank extends AppCompatActivity {
    Spinner spinner,spStation;
    String[] dias, routeDias;
    String StoreName,StoreContact,SEmail,pass,bankName,accNo,station,busroute;
    Button btnNext,btnBack;
    EditText edtAccNo;
    RequestQueue kQueue;
    ProgressDialog dialog  =null;
    public static String TAG_BANK_NAME,TAG_SHORT_NAME;
    DatabaseHelper db = new DatabaseHelper(this);
    String Password;
    ArrayList<HashMap<String, String>> bankList;
    ArrayList<HashMap<String,String>> routeList;
    protected void onCreate(Bundle savedInstanceBundle){
        super.onCreate(savedInstanceBundle);
        setContentView(R.layout.activity_register_screen_bank);
        spinner = (Spinner)findViewById(R.id.spinner);
        spStation =(Spinner) findViewById(R.id.spRegScreenStation);

        Bundle bundle = getIntent().getExtras();
        Password = bundle.getString("Password");
        kQueue= Volley.newRequestQueue(getApplicationContext());

        btnNext = (Button)findViewById(R.id.btnNextBank);
        btnBack = (Button)findViewById(R.id.btnRegBackBank);
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendData();

                /*new Thread(new Runnable() {
                    @Override
                    public void run() {

                        Looper.prepare();

                        final Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                sendData();
                                handler.removeCallbacks(this);
                                Looper.myLooper().quit();
                            }
                        }, 3000);

                        Looper.loop();
                    }
                }).start();*/

            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        //Route spinner
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((TextView) parent.getChildAt(0)).setTextColor(Color.WHITE);
                busroute = parent.getItemAtPosition(position).toString();
                Route rt = db.getRouteByName(busroute);

                bankList = new ArrayList<HashMap<String, String>>();
                List<Terminal> terminal = db.getTerminalsByRouteId(rt.getRoute_id());

                dias = new String[terminal.size()];
                for (int i = 0; i < terminal.size(); i++) {
                    Terminal s = terminal.get(i);
                    // creating new HashMap
                    HashMap<String, String> map = new HashMap<String, String>();
                    // adding each child node to HashMap key => value
                    map.put(TAG_BANK_NAME, String.valueOf(s.getShort_name()));
                    // adding HashList to ArrayList
                    bankList.add(map);
                    // add sqlite id to array
                    // used when deleting a website from sqlite
                    dias[i] = String.valueOf(s.getShort_name());
                }

                ArrayAdapter adps = new ArrayAdapter(getApplicationContext(),android.R.layout.simple_spinner_item,dias);
                adps.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spStation.setAdapter(adps);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

       spStation.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((TextView) parent.getChildAt(0)).setTextColor(Color.WHITE);
                station = parent.getItemAtPosition(position).toString();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
       });

        DatabaseHelper dbTerminalL = new DatabaseHelper(this);
        bankList = new ArrayList<HashMap<String, String>>();
        List<Terminal> terminal = dbTerminalL.getAllTerminals(); //
        System.out.println(terminal.size());
        // loop through each website
        dias = new String[terminal.size()];
        for (int i = 0; i < terminal.size(); i++) {
            Terminal s = terminal.get(i);
            // creating new HashMap
            HashMap<String, String> map = new HashMap<String, String>();
            // adding each child node to HashMap key => value
            map.put(TAG_BANK_NAME, String.valueOf(s.getShort_name()));
            // adding HashList to ArrayList
            bankList.add(map);
            // add sqlite id to array
            // used when deleting a website from sqlite
            dias[i] = String.valueOf(s.getShort_name());
        }

       // ArrayAdapter adp = new ArrayAdapter(this,android.R.layout.simple_spinner_item,dias);
        //adp.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //spStation.setAdapter(adp);

        routeList = new ArrayList<HashMap<String, String>>();
        List<Route> route = db.getAllRoute();
        routeDias = new String[route.size()];
        for (int i = 0; i < route.size(); i++) {
            Route s = route.get(i);
            // creating new HashMap
            HashMap<String, String> map = new HashMap<String, String>();
            // adding each child node to HashMap key => value
            map.put(TAG_SHORT_NAME, String.valueOf(s.getShort_name()));
            // adding HashList to ArrayList
            routeList.add(map);
            // add sqlite id to array
            // used when deleting a website from sqlite
            routeDias[i] = String.valueOf(s.getShort_name());
        }

        ArrayAdapter adps = new ArrayAdapter(this,android.R.layout.simple_spinner_item,routeDias);
        adps.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adps);
    }

    private void  sendData(){
        dialog = ProgressDialog.show(RegisterActivityBank.this, "", "Setting up your ticketing app. Please wait...", true);
       final Apps app = db.getApp(Installation.appId(getApplicationContext()));
        Terminal terminal = db.getTerminalByName(station);
        Route route = db.getRouteByName(busroute);
        app.setRoute_name(route.getShort_name());
        app.setRoute_id(route.getRoute_id());
        app.setTerminal_id(terminal.getTerminal_id());
        app.setTerminal(terminal.getShort_name());
        app.setApp_id(Installation.appId(getApplicationContext()));
        app.setStatus(1);

        if(true){
            String url ="http://platinumandco.com/slrtcapi/public/account/create";
            HashMap<String, String> params = new HashMap<String, String>();
            params.put("route_id", Integer.toString(route.getRoute_id()));
            params.put("route_name",route.getShort_name());
            params.put("merchant_id",app.getAgent_id());
            params.put("app_id", Installation.appId(getApplicationContext()));
            params.put("station_id",Integer.toString(terminal.getTerminal_id()));
            params.put("password",Password);
            params.put("station_name",terminal.getShort_name());
            params.put("status",Integer.toString(app.getStatus()));
            JSONObject json = new JSONObject(params);
            Log.d("JSON M", json.toString());
            JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST,url,json, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try{
                        if(response.getString("code").equals("200")){


                            app.setStatus(1); //set application satus to active
                            app.setIs_logged_in(1);
                            app.setAgent_code(response.getString("agentCode"));
                            Toast.makeText(RegisterActivityBank.this,response.getString("msg"), Toast.LENGTH_SHORT).show();
                            if(db.updateApp(app)>0){
                                Intent intent = new Intent(RegisterActivityBank.this,TicketingHomeActivity.class);
                                startActivity(intent);
                            }else {
                                Toast.makeText(RegisterActivityBank.this,"APp could not be sett", Toast.LENGTH_SHORT).show();
                            }

                        }else{

                            Toast.makeText(RegisterActivityBank.this,response.getString("msg")+app.getAgent_id(), Toast.LENGTH_SHORT).show();
                       }
                    }catch (Exception e){

                        Toast.makeText(RegisterActivityBank.this,e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                    dialog.dismiss();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

//                    Log.d("BTICKET",error.getMessage());
                    VolleyLog.d("BTICKET", "Error: " + error.getMessage());
                    Toast.makeText(RegisterActivityBank.this,error.getMessage(), Toast.LENGTH_SHORT).show();
                    dialog.dismiss();

                }
            });
            int socketTimeout = 3000;//30 seconds
            RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, 2, 2);
            req.setRetryPolicy(policy);
            kQueue.add(req);
        }

    }

    private boolean checkValidation() {
        boolean ret = true;
        if (!Validation.hasText(edtAccNo)) ret = false;
        if (!Validation.isAccountNo(edtAccNo, 10)) ret = false;
        return ret;
    }
    /*
     * Load Station/Terminal spinner on chage
     * of the Route spinner
     */
    public void getLoadStationSpanner(int routeId){
        bankList = new ArrayList<HashMap<String, String>>();
        List<Terminal> terminal = db.getTerminalsByRouteId(routeId);
        System.out.println(terminal.size());
        // loop through each website
        dias = new String[terminal.size()];
        for (int i = 0; i < terminal.size(); i++) {
            Terminal s = terminal.get(i);
            // creating new HashMap
            HashMap<String, String> map = new HashMap<String, String>();
            // adding each child node to HashMap key => value
            map.put(TAG_BANK_NAME, String.valueOf(s.getShort_name()));
            // adding HashList to ArrayList
            bankList.add(map);
            // add sqlite id to array
            // used when deleting a website from sqlite
            dias[i] = String.valueOf(s.getShort_name());
        }

        ArrayAdapter adps = new ArrayAdapter(this,android.R.layout.simple_spinner_item,dias);
        adps.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adps);
    }
}
