package com.busticket.amedora.busticketsrl;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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
import com.busticket.amedora.busticketsrl.model.Route;
import com.busticket.amedora.busticketsrl.utils.DatabaseHelper;
import com.busticket.amedora.busticketsrl.utils.Installation;
import com.busticket.amedora.busticketsrl.utils.Validation;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by USER on 2/10/2016.
 */
public class RegisterDriverActivity2 extends AppCompatActivity {
    EditText edtPassword,edtConfirm;
    Button btnNext;
    TextView errTv;
    ProgressDialog dialog;
    RequestQueue rQDriver;
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_driver_register2);
        edtPassword = (EditText) findViewById(R.id.edtPassword);
        edtConfirm  =   (EditText)findViewById(R.id.edtConfirm);
        btnNext =(Button) findViewById(R.id.btnNextReg2);
        errTv = (TextView) findViewById(R.id.tvloginError);
        rQDriver = Volley.newRequestQueue(getApplicationContext());
        edtConfirm.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                Validation.isPasswordMatch(edtPassword, edtConfirm, true);
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkValidation()) {


                    dialog = ProgressDialog.show(RegisterDriverActivity2.this, "", "Setting up your ticketing app. Please wait...", true);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {

                            Looper.prepare();

                            final Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    postUpdateDriverDetails();
                                    Intent intent = new Intent(RegisterDriverActivity2.this, TripHomeActivity.class);
                                    startActivity(intent);
                                    handler.removeCallbacks(this);

                                    Looper.myLooper().quit();
                                }
                            }, 30000);

                            Looper.loop();
                        }
                    }).start();


                } else {
                    errTv.setText("Password mismatch please ensure that you enter the same character in the two input fields");
                    Toast.makeText(getApplicationContext(),
                            "Password mismatch please ensure that you enter the same character in the two input fields",
                            Toast.LENGTH_LONG).show();
                }
            }
        });
        //Route spinner
    }

    public void postUpdateDriverDetails() {
        DatabaseHelper db = new DatabaseHelper(getApplicationContext());
        Apps app2 = db.getApp(Installation.appId(getApplicationContext()));

        String url = "http://41.77.173.124:81/srltcapi/public/driver/update/" + app2.getLicenceNo();// Installation.appId(getApplicationContext());
        HashMap<String, String> params = new HashMap<String, String>();
        //String ticket_id = Long.toString(ticket.getTicket_id());
        params.put("route_id",Integer.toString(app2.getRoute_id()));
        params.put("bus_id", Integer.toString(app2.getBusID()));
        params.put("app_id", Installation.appId(getApplicationContext()));
        params.put("password", edtPassword.getText().toString());
        params.put("is_logged_in","1");


        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,url, new JSONObject(params), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try{
                    JSONObject jsonObject= new JSONObject(response.toString());
                    //extracting json array from response string
                    Toast.makeText(RegisterDriverActivity2.this, "", Toast.LENGTH_SHORT).show();

                    String code = jsonObject.getString("code");
                    Toast.makeText(RegisterDriverActivity2.this,code, Toast.LENGTH_SHORT).show();
                    if(code.equals("200")){
                        JSONArray jsonArray = jsonObject.getJSONArray("data");
                        JSONObject mydriver = jsonArray.getJSONObject(0);
                        Toast.makeText(RegisterDriverActivity2.this,mydriver.toString(), Toast.LENGTH_SHORT).show();

                        DatabaseHelper dbr = new DatabaseHelper(getApplicationContext());

                        Apps app = dbr.getApp(Installation.appId(getApplicationContext()));
                        //Route mRoute = dbr.getRouteByName(busroute);

                        Toast.makeText(RegisterDriverActivity2.this,mydriver.toString(), Toast.LENGTH_SHORT).show();

                        if(app.getApp_id() !=null){
                            //app.setAppID(Installation.appId(getApplicationContext()));
                            app.setDriverID(mydriver.getInt("id"));
                            app.setDriverFname(mydriver.getString("firstname"));
                            app.setDriverLname(mydriver.getString("lastname"));
                            app.setLicenceNo(mydriver.getString("licence_code"));
                            app.setBusID(Integer.parseInt(mydriver.getString("bus_id")));

                            //app.setP(mBus.getPlate_no());
                            app.setScheduleID(mydriver.getInt("schedule_id"));
                            app.setDriverPassword(edtPassword.getText().toString());
                            app.setDriverLoggedIn(1);
                            app.setStatus(1);
                            //app.setVerified(mydriver.getString("verified"));

                            //app.setRouteID(mRoute.getRoute_id());
                            app.setBusID(mydriver.getInt("bus_id"));

                            if(dbr.updateApp(app)>0){

                                Toast.makeText(RegisterDriverActivity2.this,jsonObject.getString("msg"), Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(RegisterDriverActivity2.this,TripHomeActivity.class);
                                startActivity(intent);

                            }else{

                                Toast.makeText(RegisterDriverActivity2.this,"Unexpected Error! Record could not be saved", Toast.LENGTH_SHORT).show();
                            }

                        }else {

                            Toast.makeText(RegisterDriverActivity2.this,"Existing Driver registration! please contact developer", Toast.LENGTH_SHORT).show();
                        }
                    }else {

                        //errTv.setText("Licence does not exist in SRLTC Database");
                        Toast.makeText(RegisterDriverActivity2.this,jsonObject.getString("msg"), Toast.LENGTH_SHORT).show();
                    }



                }catch (Exception ex){

                    ex.printStackTrace();
                    Log.e("Sysnc Err", ex.getMessage());
                    Toast.makeText(RegisterDriverActivity2.this,ex.getMessage(), Toast.LENGTH_SHORT).show();
                    VolleyLog.d("Driver Err", ex.getMessage());

                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(RegisterDriverActivity2.this,error.getMessage(), Toast.LENGTH_SHORT).show();
                VolleyLog.d("Driver Err", error.getMessage());
            }
        });
        int socketTimeout = 20000;//30 seconds
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsonObjectRequest.setRetryPolicy(policy);
        rQDriver.add(jsonObjectRequest);
        //dialog.cancel();


    }

    private boolean checkValidation() {
        boolean ret = true;
        if (!Validation.hasText(edtPassword)) ret = false;
        if (!Validation.isPasswordMatch(edtPassword, edtConfirm, true)) ret = false;
        //if (!Validation.isPhoneNumber(edtLname, false)) ret = false;
        return ret;
    }
}
