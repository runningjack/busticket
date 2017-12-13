package com.busticket.amedora.busticketsrl;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.busticket.amedora.busticketsrl.model.Apps;
import com.busticket.amedora.busticketsrl.model.Ticket;
import com.busticket.amedora.busticketsrl.model.Trip;
import com.busticket.amedora.busticketsrl.utils.DatabaseHelper;
import com.busticket.amedora.busticketsrl.utils.Installation;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

/**
 * Created by USER on 2/11/2016.
 */
public class TripDayCloseActivity extends AppCompatActivity {
    DatabaseHelper db ;
    RequestQueue rqRequest,rQSyncTicket,rqTrip;
    Apps app;
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        db = new DatabaseHelper(this);
        rqRequest = Volley.newRequestQueue(getApplicationContext());
        rQSyncTicket = Volley.newRequestQueue(getApplicationContext());
        rqTrip = Volley.newRequestQueue(getApplicationContext());
        app = db.getApp(Installation.appId(this));
        app.setDriverLoggedIn(0);
        if(db.updateApp(app) >0){
            List<Trip> trips = db.getAllTrip();
            if(trips.size()>0){
                for(int i = 0; i < trips.size(); i++){
                    db.deleteTrip(trips.get(i));
                }
            }
            synchTrips();
            serverLogout();
            Intent intent = new Intent(TripDayCloseActivity.this,LoginActivity.class);
            startActivity(intent);
        }else{
            Intent intent = new Intent(TripDayCloseActivity.this,TripHomeActivity.class);
            startActivity(intent);
            Toast.makeText(TripDayCloseActivity.this, "Unexpected Error! Logout was not successful", Toast.LENGTH_SHORT).show();
        }
    }

    public void serverLogout(){
        String url = "http://41.77.173.124:81/srltcapi/public/driver/logout/"+app.getLicenceNo();
        StringRequest strUpdateAccount = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        int socketTimeout = 3000;//30 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout,2,2);
        strUpdateAccount.setRetryPolicy(policy);
        rqRequest.add(strUpdateAccount);
    }

    private void synchTrips(){
        List<Trip> registeredTrip = db.getAllTrip();
        if(registeredTrip.size() >=1){
            HashMap<String,String> params = new HashMap<String,String>();
            JSONObject jsonObj = new JSONObject();
            String url ="http://41.77.173.124:81/busticketAPI/triplog/batchsync";
            params.put("triplogs",registeredTrip.toString());
            JSONObject j = new JSONObject(params);
            JsonObjectRequest jsonObjectRequestTrips = new JsonObjectRequest(Request.Method.POST, url, j, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try{
                        if(true){
                            JSONArray jsonArray = response.getJSONArray("data");
                            if(jsonArray.length() > 0){
                                Toast.makeText(TripDayCloseActivity.this,response.getString("msg"), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }catch (Exception ex){
                        ex.printStackTrace();
                        Log.e("Sysnc Err",ex.getMessage());
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });
            int socketTimeout = 2000;//30 seconds - change to what you want
            RetryPolicy policy = new DefaultRetryPolicy(socketTimeout,2,2);
            jsonObjectRequestTrips.setRetryPolicy(policy);
            rqTrip.add(jsonObjectRequestTrips);
        }
    }
}
