package com.busticket.amedora.busticketsrl;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

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
import com.busticket.amedora.busticketsrl.utils.*;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;


public class MainActivity extends Activity {
    Button btnRgister,btnLogin;
    private Handler mHandler = new Handler();
    private static final String INSTALLATION = "INSTALLATION";
    int status =0;
    DatabaseHelper db;
    Apps apps;
    String AppID;
    File installation;
    RequestQueue kQueue;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

            setContentView(R.layout.activity_main);
            db = new DatabaseHelper(getApplicationContext());
        installation= new File(getApplicationContext().getFilesDir(),INSTALLATION);
        AppID= Installation.readInstallationFile(installation);
        apps =  new Apps();
        btnLogin = (Button)findViewById(R.id.btnstartlogin);
        btnRgister = (Button)findViewById(R.id.btnstartregister);
        kQueue = Volley.newRequestQueue(getApplicationContext());
        if(apps.getStatus() == 1){
            btnRgister.setEnabled(false);
        }

        if(db.getApp(AppID) == null){
            btnLogin.setEnabled(false);
        }


       mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(installation.exists()){

                    if(AppID ==null ){
                        Intent intent = new Intent(MainActivity.this, LoadingFeatures.class);
                        startActivity(intent);
                    }else{
                        apps = db.getApp(AppID);
                        if(apps == null){
                            Intent intent = new Intent(MainActivity.this, LoadingFeatures.class);
                            startActivity(intent);
                        }else if ((apps.getStatus() == 0)){
                            Intent intent = new Intent(MainActivity.this, LoadingFeatures.class);
                            startActivity(intent);
                        }else if((apps.getStatus() == 1) && apps.getIs_logged_in()==1){
                            synchAccount();//get account info from server synchronize before login
                            /*Befor login check application mode*/
                            if(apps.getAppMode() == 0){ // if mode is equals to zero normal mode
                                Intent intent = new Intent(MainActivity.this, TicketingHomeActivity.class);
                                startActivity(intent);
                            }else if(apps.getAppMode() == 1){ // if mode is equals to one ---Driver login mode
                                /**
                                 * check if driver exist
                                 * */
                                if(apps.getLicenceNo().length() == 0){ //  Driver does not exist
                                    /**
                                     * If null start driver register activity
                                     * */
                                    Intent myIntent = new Intent(MainActivity.this,RegisterDriverActivity.class);
                                    startActivity(myIntent);
                                    finish();
                                }else if(apps.getLicenceNo().length() >0){ // Driver Exixist
                                    /*Check if driver is logged in*/
                                    if(apps.getDriverLoggedIn() >0){ //driver is logged in
                                        Intent myIntent = new Intent(MainActivity.this,TicketingHomeActivity.class);
                                        startActivity(myIntent);
                                        finish();
                                    }else{ // Driver is not logged in
                                        /*start driver login activity*/
                                        Intent myIntent = new Intent(MainActivity.this,LoginDriverActivity.class);
                                        startActivity(myIntent);
                                        finish();
                                    }

                                }else{ //undefined application mode
                                    //Toast.makeText(Ma)
                                    /*Intent myIntent = new Intent(MainActivity.this,TicketingHomeActivity.class);
                                    startActivity(myIntent);
                                    finish();*/
                                }

                            }

                        }else if((apps.getStatus() == 1) && apps.getIs_logged_in()==0){
                            synchAccount() ; //get account info from server synchronize before login
                            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                            startActivity(intent);
                        }
                    }
                    //else if(AppID !=null && (apps.getStatus() == 0))
                }else{
                    Intent intent = new Intent(MainActivity.this, LoadingFeatures.class);
                    startActivity(intent);
                }
            }
       }, 5000);


        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        btnRgister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,RegisterActivity1.class);
                startActivity(intent);
            }
        });
    }
    public void synchAccount(){
        String url ="http://41.77.173.124:81/srltcapi/public/account/synch/"+apps.getApp_id();

        JsonObjectRequest uAccount = new JsonObjectRequest(Request.Method.GET,url, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try{
                    JSONObject jsonObject= new JSONObject(response.toString());

                    String code = jsonObject.getString("code");
                    Toast.makeText(MainActivity.this,code, Toast.LENGTH_SHORT).show();
                    if(code.equals("200")){
                        JSONArray jsonArray = jsonObject.getJSONArray("data");
                        JSONObject myapp = jsonArray.getJSONObject(0);
                        Toast.makeText(MainActivity.this,myapp.toString(), Toast.LENGTH_SHORT).show();

                            apps.setRoute_id(myapp.getInt("route_id"));
                            apps.setTerminal_id(myapp.getInt("sation_id"));
                            apps.setRoute_name(myapp.getString("route_name"));
                            apps.setTerminal(myapp.getString("station_name"));
                            apps.setBalance(myapp.getDouble("balance"));
                            apps.setStatus(myapp.getInt("status"));
                            apps.setAgent_code(myapp.getString("agent_code"));
                            //apps.setIs_logged_in(cursor.getColumnIndex(KEY_APP_IS_LOGGED_IN));
                            //apps.setBusID(cursor.getInt(cursor.getColumnIndex(KEY_APP_BUS_ID)));
                            //apps.setScheduleID(cursor.getInt(cursor.getColumnIndex(KEY_APP_SCHEDULE_ID)));
                            //apps.setDriverFname(cursor.getString(cursor.getColumnIndex(KEY_APP_DRIVER_FNAME)));
                            //apps.setDriverLname(cursor.getString(cursor.getColumnIndex(KEY_APP_DRIVER_LNAME)));
                            //apps.setDriverID(cursor.getInt(cursor.getColumnIndex(KEY_APP_DRIVER_ID)));
                            //apps.setTripCount(cursor.getInt(cursor.getColumnIndex(KEY_APP_TRIPS)));

                            apps.setAppMode(myapp.getInt("app_mode"));


                            if(db.updateApp(apps)>0){
                                Toast.makeText(MainActivity.this,"Account Synchronized Successfully", Toast.LENGTH_SHORT).show();

                            }else{
                                Toast.makeText(MainActivity.this,"Unexpected Error! Record could not be synchronized", Toast.LENGTH_SHORT).show();
                            }


                    }else {
                        //errTv.setText("License Number does not exist in SRLTC Database");
                        Toast.makeText(MainActivity.this,"Server Error", Toast.LENGTH_SHORT).show();
                    }



                }catch (Exception ex){
                    ex.printStackTrace();
                    Log.e("Sysnc Err", ex.getMessage());
                    Toast.makeText(MainActivity.this,ex.getMessage(), Toast.LENGTH_SHORT).show();
                    VolleyLog.d("Driver Err", ex.getMessage());
                }

                Toast.makeText(MainActivity.this, "Account Updated", Toast.LENGTH_SHORT).show();
            }
        },new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, "Account balance could not be updated Unexpected Errors", Toast.LENGTH_SHORT).show();
            }
        });
        int socketTimeout = 20000;//30 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        uAccount.setRetryPolicy(policy);
        kQueue.add(uAccount);
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
