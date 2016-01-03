
package com.busticket.amedora.busticketsrl;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.busticket.amedora.busticketsrl.utils.*;

import com.busticket.amedora.busticketsrl.model.Apps;

import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Amedora on 7/10/2015.
 */
public class RegisterActivity3 extends Activity {
    EditText edtPhone;
    TextView tvMessage;
    String SName,SContact,Email,Password,bankName,accNo;
    Button btnNext,btnBack;
    HttpClient httpclient;
    //OkHttpClient okHttp;
    HttpPost httppost;
    RequestQueue kQueue;
    String ERRORMSG;
    List<NameValuePair> nameValuePairs;
    ProgressDialog dialog  =null;
    String Phone ;
    String AppID;
    boolean success = false;
    DatabaseHelper db = new DatabaseHelper(this);
    protected void onCreate(Bundle savedInstanceBundle){
        super.onCreate(savedInstanceBundle);
        setContentView(R.layout.activity_register_screen3);
        tvMessage =(TextView)findViewById(R.id.tvMessage3);
        edtPhone = (EditText) findViewById(R.id.edtPhone);

        Bundle bundle = getIntent().getExtras();

        SName 		= bundle.getString("SName");
        SContact 		= bundle.getString("SContact");
        Email 		= bundle.getString("Email");
        Password    = bundle.getString("Password");

        bankName    = bundle.getString("BankName");
        accNo       = bundle.getString("AccNo");

        btnBack     = (Button) findViewById(R.id.btnregback3);
        btnNext     = (Button) findViewById(R.id.btnregnext3);
        kQueue = Volley.newRequestQueue(getApplicationContext());
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog = ProgressDialog.show(RegisterActivity3.this, "","Creating your ticketer account. Please wait...", true);
                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        Looper.prepare();

                        final Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                register();
                                handler.removeCallbacks(this);

                                Looper.myLooper().quit();
                            }
                        }, 2000);

                        Looper.loop();
                    }
                }).start();
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

  private void register(){
      AppID       =  Installation.appId(getApplicationContext());
            HashMap<String,String> params = new HashMap<String,String>();
            params.put("company",SName);
            params.put("c_fname",SContact);
            params.put("email",Email);
            params.put("app_id", AppID);
            params.put("key_salt","");
            params.put("username",Email);
            params.put("phone",edtPhone.getText().toString());
            params.put("password",Password);
            String url = "http://41.77.173.124:81/busticketAPI/merchants/create/";
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(url,new JSONObject(params),new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                   // VolleyLog.d("Return Json",response.toString());
                    try{
                        if(Boolean.parseBoolean(response.getString("success"))==true){
                            VolleyLog.d("Return Json",response.getString("success"));
                            //dialog.dismiss();
                            Apps app = new Apps();
                            app.setApp_id(AppID);
                            app.setAgent_id(response.getString("data"));
                            app.setStatus(0);

                            try{
                                if(db.createApp(app) >0){
                                    Toast.makeText(getApplicationContext(),"App Created Record Save",Toast.LENGTH_LONG).show();
                                    Intent serverIntent = new Intent(  RegisterActivity3.this,RegisterActivityBank.class);
                                    startActivity(serverIntent);
                                }

                            }catch(Exception ex){
                                VolleyLog.d("Return Json",ex.getMessage());
                            }
                        }else{
                            Installation.deleteInstallionFile(getApplicationContext());
                            Toast.makeText(getApplicationContext(),response.getString("msg"),Toast.LENGTH_LONG).show();
                            VolleyLog.d("Return Json",response.toString());
                        }
                    }catch(Exception ex){

                        Toast.makeText(getApplicationContext(),response.toString()+" "+ex.getMessage(),Toast.LENGTH_LONG).show();
                        VolleyLog.d("Return Json",ex.getMessage());
                        try {
                            Installation.deleteInstallionFile(getApplicationContext());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                }
            },new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    try {

                        Installation.deleteInstallionFile(getApplicationContext());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    VolleyLog.d("Return Json",error.getMessage());
                }
            });

            kQueue.add(jsonObjectRequest);

            //dialog.dismiss();

    }
}
