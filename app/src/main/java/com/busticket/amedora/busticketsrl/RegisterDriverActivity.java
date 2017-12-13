package com.busticket.amedora.busticketsrl;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
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
import com.busticket.amedora.busticketsrl.utils.DatabaseHelper;
import com.busticket.amedora.busticketsrl.utils.Installation;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by USER on 2/8/2016.
 */
public class RegisterDriverActivity extends AppCompatActivity {
    Button btnLicence;
    EditText edtLicence;
    String licenceNo = "";
    TextView errTv;
    RequestQueue rQDriver;
    DatabaseHelper db;
    ProgressDialog dialog  =null;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_driver_register);
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
        btnLicence = (Button) findViewById(R.id.btnLicenceReg);
        edtLicence = (EditText) findViewById(R.id.edtLicenceNoReg);
        errTv = (TextView) findViewById(R.id.tvloginError);
        //Bundle bundle = getIntent().getExtras();
        db = new DatabaseHelper(getApplicationContext());
        rQDriver = Volley.newRequestQueue(getApplicationContext());

        btnLicence.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Apps app = db.getApp(Installation.appId(getApplicationContext()));

                Toast.makeText(RegisterDriverActivity.this, app.getDriverFname(), Toast.LENGTH_SHORT).show();
                if (validate()) {
                    licenceNo = edtLicence.getText().toString();
                    Toast.makeText(RegisterDriverActivity.this,licenceNo, Toast.LENGTH_SHORT).show();
                    getDriverDetails();
                }
            }
        });


    }

    public boolean validate() {
        boolean valid = true;
        String licenceText = edtLicence.getText().toString();
        if (licenceText.isEmpty() || licenceText.length() < 5 || licenceText.length() > 12) {
            edtLicence.setError("between 5 and 10 alphanumeric characters");
            valid = false;
        } else {
            edtLicence.setError(null);
        }

        return valid;
    }

    public void getDriverDetails() {
        dialog = ProgressDialog.show(RegisterDriverActivity.this, "", "Getting driver data from server. Please wait...", true);
        String url = "http://platinumandco.com/slrtcapi/public/driver/data/"+licenceNo;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,url, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try{
                    JSONObject jsonObject= new JSONObject(response.toString());
                    //extracting json array from response string
                    Toast.makeText(RegisterDriverActivity.this,"", Toast.LENGTH_SHORT).show();

                    String code = jsonObject.getString("code");
                    Toast.makeText(RegisterDriverActivity.this,code, Toast.LENGTH_SHORT).show();
                    if(code.equals("200")){
                        JSONArray jsonArray = jsonObject.getJSONArray("data");
                        JSONObject mydriver = jsonArray.getJSONObject(0);
                        Toast.makeText(RegisterDriverActivity.this,mydriver.toString(), Toast.LENGTH_SHORT).show();

                        DatabaseHelper dbr = new DatabaseHelper(getApplicationContext());

                        Apps app = dbr.getApp(Installation.appId(getApplicationContext()));

                        Toast.makeText(RegisterDriverActivity.this,app.toString(), Toast.LENGTH_SHORT).show();
                        if(app.getLicenceNo() ==null){
                            //app.setAppID(Installation.appId(getApplicationContext()));
                            app.setDriverID(mydriver.getInt("id"));
                            app.setDriverFname(mydriver.getString("firstname"));
                            app.setDriverLname(mydriver.getString("lastname"));
                            app.setLicenceNo(mydriver.getString("licence_code"));
                            app.setPassword(mydriver.getString("password"));
                            //app.setVerified(mydriver.getString("verified"));
                            app.setRoute_id(mydriver.getInt("route_id"));
                            app.setBusID(mydriver.getInt("bus_id"));

                            if(dbr.updateApp(app)>0){
                                Toast.makeText(RegisterDriverActivity.this,jsonObject.getString("msg"), Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(RegisterDriverActivity.this,RegisterDriverActivity2.class);
                                startActivity(intent);
                            }else{
                                Toast.makeText(RegisterDriverActivity.this,"Unexpected Error! Record could not be saved", Toast.LENGTH_SHORT).show();
                            }

                        }else {
                            Toast.makeText(RegisterDriverActivity.this,"Existing Driver registration! please contact developer", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(RegisterDriverActivity.this,RegisterActivity2.class);
                            startActivity(intent);
                        }
                    }else {
                        errTv.setText("License Number does not exist in SRLTC Database");
                        Toast.makeText(RegisterDriverActivity.this,jsonObject.getString("msg"), Toast.LENGTH_SHORT).show();
                    }

                }catch (Exception ex){
                    ex.printStackTrace();
                    Log.e("Sysnc Err", ex.getMessage());
                    Toast.makeText(RegisterDriverActivity.this,ex.getMessage(), Toast.LENGTH_SHORT).show();
                    VolleyLog.d("Driver Err", ex.getMessage());
                }
                dialog.dismiss();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(RegisterDriverActivity.this,error.getMessage(), Toast.LENGTH_SHORT).show();
                VolleyLog.d("Driver Err", error.getMessage());
                dialog.dismiss();
            }
        });
        int socketTimeout = 20000;//30 seconds
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsonObjectRequest.setRetryPolicy(policy);
        rQDriver.add(jsonObjectRequest);
    }

}
