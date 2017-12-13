package com.busticket.amedora.busticketsrl;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.busticket.amedora.busticketsrl.model.Apps;
import com.busticket.amedora.busticketsrl.utils.DatabaseHelper;
import com.busticket.amedora.busticketsrl.utils.Installation;

/**
 * Created by USER on 1/17/2016.
 */
public class LogoutActivity extends AppCompatActivity {
    DatabaseHelper db ; RequestQueue rqRequest;
    @Override
    public void  onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        db = new DatabaseHelper(this);
        rqRequest = Volley.newRequestQueue(this);
        Apps app = db.getApp(Installation.appId(this));
        app.setIs_logged_in(0);
        if(db.updateApp(app) >0){
            serverLogout();
            Intent intent = new Intent(LogoutActivity.this,LoginActivity.class);
            startActivity(intent);
        }else{
            Intent intent = new Intent(LogoutActivity.this,TicketingHomeActivity.class);
            startActivity(intent);
            Toast.makeText(LogoutActivity.this, "Unexpected Error! Logout was not successful", Toast.LENGTH_SHORT).show();
        }

    }

    public void serverLogout(){
        String url = "http://platinumandco.com/slrtcapi/public/account/logout/"+Installation.appId(this);
        StringRequest strUpdateAccount = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        int socketTimeout = 30000;//30 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        strUpdateAccount.setRetryPolicy(policy);
        rqRequest.add(strUpdateAccount);
    }
}
