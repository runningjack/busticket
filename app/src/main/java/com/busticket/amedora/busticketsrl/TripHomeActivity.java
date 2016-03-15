package com.busticket.amedora.busticketsrl;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.busticket.amedora.busticketsrl.model.Apps;
import com.busticket.amedora.busticketsrl.model.Trip;
import com.busticket.amedora.busticketsrl.utils.DatabaseHelper;
import com.busticket.amedora.busticketsrl.utils.Installation;
import com.busticket.amedora.busticketsrl.utils.Validation;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by USER on 1/20/2016.
 */
public class TripHomeActivity extends AppCompatActivity {
    Button btn;
    ProgressDialog pDialog = null;
    EditText speedostart,passenger,speedoend,remark;
    String speedoS,passen,speedoE,rem;
    DatabaseHelper db;
    DateFormat df ;
    RequestQueue rQDriver,QDriver;
    DatabaseHelper dbr ;
    Apps app ;
    String actionText ="START TRIP";
    String startTime="";
    CharSequence curText;
    Trip curTrip;
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_triplog_home);

        btn = (Button) findViewById(R.id.button);
        df = new SimpleDateFormat("HH:mm:ss");
        db = new DatabaseHelper(getApplicationContext());
        dbr = new DatabaseHelper(getApplicationContext());
        app = dbr.getApp(Installation.appId(getApplicationContext()));
        actionText="START TRIP";

        curTrip = db.getCurrTrip(); // Trip with status 0
        if(curTrip != null){
            btn.setText("CLOSE TRIP");
            ((GradientDrawable)btn.getBackground()).setColor(getResources().getColor(R.color.darkgreen));
        }else{
            ((GradientDrawable)btn.getBackground()).setColor(getResources().getColor(R.color.yellow));
        }


        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                curText = ((Button) v).getText();
                if (curText.equals("START TRIP")) {
                    ((TextView) v).setText("START TRIP");
                    ((GradientDrawable) btn.getBackground()).setColor(getResources().getColor(R.color.yellow));
                    OpenDialog();
                }

                if (curText.equals("CLOSE TRIP")) {
                    ((TextView) v).setText("CLOSE TRIP");
                    ((GradientDrawable) btn.getBackground()).setColor(getResources().getColor(R.color.darkgreen));
                    closeTripDialog();
                }

                if (curText.equals("FINALIZE")) {
                    ((GradientDrawable) btn.getBackground()).setColor(getResources().getColor(R.color.red));
                    pDialog = ProgressDialog.show(TripHomeActivity.this, "",
                            "Closing your trip...", true);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Looper.prepare();
                            final Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    //Action to take
                                    closeTrip();
                                    handler.removeCallbacks(this);
                                    Looper.myLooper().quit();
                                }
                            }, 2000);

                            Looper.loop();
                        }
                    }).start();
                }

            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                //.setAction("Action", null).show();
                Intent mCloseTrip = new Intent(TripHomeActivity.this,TripDayCloseActivity.class);
                startActivity(mCloseTrip);

            }
        });

        rQDriver = Volley.newRequestQueue(getApplicationContext());
        QDriver = Volley.newRequestQueue(getApplicationContext());
    }

    private void OpenDialog(){
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Enter Trip Info");
        btn.setText("START TRIP");
        ((GradientDrawable)btn.getBackground()).setColor(getResources().getColor(R.color.yellow));
        LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.layout_start_trip_dialog, null);
        dialog.setView(v);
        dialog.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Dialog f = (Dialog) dialog;
                speedostart = (EditText) f.findViewById(R.id.edtTripSpeedoStart);
                passenger = (EditText) f.findViewById(R.id.edtTripPassengerNo);
                if(checkValidation()){

                    speedoS = speedostart.getText().toString();
                    passen = passenger.getText().toString();
                    //Action to take
                    startTrip();
                    btn.setText("CLOSE TRIP");
                    ((GradientDrawable)btn.getBackground()).setColor(getResources().getColor(R.color.darkgreen));
                }


            }
        });

        dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        dialog.show();

    }

    private void closeTripDialog(){
        AlertDialog.Builder dialog2 = new AlertDialog.Builder(this);
        dialog2.setTitle("Enter Trip Info");
        LayoutInflater inflater2 = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater2.inflate(R.layout.layout_close_trip_dialog, null);
        dialog2.setView(v);
        dialog2.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Dialog z = (Dialog) dialog;
                speedoend = (EditText) z.findViewById(R.id.edtTripSpeedoEnd);
                remark = (EditText) z.findViewById(R.id.edtTripRemark);

                if (checkValidationClose()) {
                    speedoE = speedoend.getText().toString();
                    rem = remark.getText().toString();
                    btn.setText("FINALIZE");
                    ((GradientDrawable) btn.getBackground()).setColor(getResources().getColor(R.color.red));
                }

            }
        });

        dialog2.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        dialog2.show();

    }

    public void startTrip(){


        final Apps app2 = db.getApp(Installation.appId(getApplicationContext()));

        final DatabaseHelper dbr = new DatabaseHelper(getApplicationContext());
        df = new SimpleDateFormat("HH:mm:ss");
        String startTime="";
        Trip trip = new Trip();
        trip.setBusID((app2.getBusID()));
        trip.setDriverID((app2.getDriverID()));
        trip.setDestFrom(app2.getRoute_name());
        trip.setRouteID(app2.getRoute_id());
        trip.setScheduleID(app2.getScheduleID());
        try{
            Date t = new Date();

            startTime = df.format(t);

        }catch (Exception e){

        }
        trip.setStartTime(startTime);
        trip.setSpeedoStart(speedoS);
        trip.setTripDate(dbr.getDate());
        trip.setPassenger(Integer.parseInt(passen));
        String tripID = "T-"+app2.getRoute_name().substring(0,3)+"-"+app2.getDriverFname().substring(0,3)+trip.getStartTime().replace(":","");
        trip.setTrip_ID(tripID);
        if(dbr.createTrip(trip)>0){

            Toast.makeText(TripHomeActivity.this, "Record Saved", Toast.LENGTH_SHORT).show();
            String url = "http://41.77.173.124:81/srltcapi/public/triplog/create/" + app2.getLicenceNo();// Installation.appId(getApplicationContext());
            HashMap<String, String> params = new HashMap<String, String>();
            params.put("driver_id", Integer.toString(app2.getDriverID()));
            params.put("bus_id", Integer.toString(app2.getBusID()));
            params.put("app_id",Installation.appId(getApplicationContext()));
            params.put("trip_id",trip.getTrip_ID());
            params.put("route_id",(Integer.toString(app2.getRoute_id())));
            params.put("destination_from",app2.getRoute_name());
            params.put("trip_date",trip.getTripDate());
            params.put("schedule_id", Integer.toString(app2.getScheduleID()));
            params.put("start_time",trip.getStartTime());
            params.put("speedo_start",speedoS);
            params.put("passenger",passen);
            JSONObject json = new JSONObject(params);
            Log.d("JSON S", json.toString());
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,url,json , new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try{
                        JSONObject jsonObject= new JSONObject(response.toString());

                        String code = jsonObject.getString("code");
                        Toast.makeText(TripHomeActivity.this, code, Toast.LENGTH_SHORT).show();
                        if(code.equals("200")){
                            int trip = app2.getTripCount() +1;
                            app2.setTripCount(trip);
                            dbr.updateApp(app2);
                            Intent intent = new Intent(TripHomeActivity.this,TicketingHomeActivity.class);
                            startActivity(intent);
                        }else {
                            //errTv.setText("Licence does not exist in SRLTC Database");
                            Toast.makeText(TripHomeActivity.this, jsonObject.getString("msg"), Toast.LENGTH_SHORT).show();
                        }
                    }catch (Exception ex){
                        ex.printStackTrace();
                        Log.e("Sysnc Err", ex.getMessage());
                        Toast.makeText(TripHomeActivity.this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                        VolleyLog.d("Driver Err", ex.getMessage());
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    Toast.makeText(TripHomeActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    VolleyLog.d("Driver Err", error.getMessage());
                }
            });
            int socketTimeout = 20000;//20 seconds
            RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            jsonObjectRequest.setRetryPolicy(policy);
            rQDriver.add(jsonObjectRequest);
        }else{
            Toast.makeText(TripHomeActivity.this, "Unexpected Error! Trip could not be saved", Toast.LENGTH_SHORT).show();

        }

    }

    public void closeTrip(){
        Trip trip = dbr.getCurrTrip();

        try{
        DatabaseHelper dbr = new DatabaseHelper(getApplicationContext());
        df = new SimpleDateFormat("HH:mm:ss");
            Date t = new Date();
            startTime = df.format(t);
        trip.setUpdatedAt(dbr.getDateTime());
        trip.setEndTime(startTime);
        trip.setSpeedoEnd(speedoE);
        trip.setRemark(rem);
            trip.setStatus(1);
            Log.d("JSON D", trip.toString());
            Toast.makeText(TripHomeActivity.this, trip.toString(), Toast.LENGTH_SHORT).show();
        }catch (Exception e){

            Toast.makeText(TripHomeActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
    }
        if(db.updateTrip(trip)>0){
            Log.d("JSON SAVE", trip.toString());
            Toast.makeText(TripHomeActivity.this, "Trip Record updated", Toast.LENGTH_SHORT).show();
            String url = "http://41.77.173.124:81/srltcapi/public/triplog/update/"+trip.getTrip_ID();// Installation.appId(getApplicationContext());
            HashMap<String, String> params = new HashMap<String, String>();

            params.put("end_time", startTime);
            params.put("speedo_end", speedoE);
            params.put("remark",rem);
            JSONObject json = new JSONObject(params);
            //Log.d("JSON STOP", json.toString());
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,url, json, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try{
                        JSONObject jsonObject= new JSONObject(response.toString());
                        String code = jsonObject.getString("code");
                        Toast.makeText(TripHomeActivity.this, code, Toast.LENGTH_SHORT).show();
                        if(code.equals("200")){
                            pDialog.dismiss();
                            Intent intent = new Intent(TripHomeActivity.this,LogoutActivity.class);
                            startActivity(intent);
                            Toast.makeText(TripHomeActivity.this, "Congratulations! Trip log was successfully uploaded to the server", Toast.LENGTH_SHORT).show();
                        }else {

                            Toast.makeText(TripHomeActivity.this, jsonObject.getString("msg"), Toast.LENGTH_SHORT).show();
                        }
                    }catch (Exception ex){

                        ex.printStackTrace();
                        Log.e("Sysnc Err", ex.getMessage());
                        Toast.makeText(TripHomeActivity.this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                        VolleyLog.d("Driver Err", ex.getMessage());
                    }
                    pDialog.dismiss();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    Toast.makeText(TripHomeActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    VolleyLog.d("Driver Err", error.getMessage());
                }
            });
            int socketTimeout = 20000;//20 seconds
            RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            jsonObjectRequest.setRetryPolicy(policy);
            QDriver.add(jsonObjectRequest);

        }else{
            Toast.makeText(TripHomeActivity.this, "Unexpected Error! Trip could not be updated", Toast.LENGTH_SHORT).show();

        }
    }



    private boolean checkValidation() {
        boolean ret = true;

        if (!Validation.hasText(speedostart)) ret = false;
        if (!Validation.hasText(passenger)) ret = false;
        return ret;
    }

    private boolean checkValidationClose() {
        boolean ret = true;

        if (!Validation.hasText(speedoend)) ret = false;

        return ret;
    }

}
