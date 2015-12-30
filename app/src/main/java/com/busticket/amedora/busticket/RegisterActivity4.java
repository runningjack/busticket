package com.busticket.amedora.busticket;

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

import com.android.volley.toolbox.JsonObjectRequest;
import com.busticket.amedora.busticket.utils.*;
import com.busticket.amedora.busticket.model.Apps;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Amedora on 7/10/2015.
 */
public class RegisterActivity4 extends AppCompatActivity {

    EditText edtCode;
    TextView tvMessage;
    List<NameValuePair> nameValuePairs;
    ProgressDialog dialog  =null;
    String Phone ;
    String AppID;
    boolean success = true;
    Button btnNext,btnBack;
    DatabaseHelper db = new DatabaseHelper(this);
    protected void onCreate(Bundle savedInstanceBundle){
        super.onCreate(savedInstanceBundle);
        setContentView(R.layout.activity_register_screen4);
        edtCode = (EditText) findViewById(R.id.edtVerify);

        tvMessage =(TextView)findViewById(R.id.tvMessage4);
        btnNext =(Button) findViewById(R.id.btnregverify);
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity4.this,TicketingHomeActivity.class);
                startActivity(intent);
            }
        });

    }

    private void register(){
        try{

            HttpClient httpclient=new DefaultHttpClient();
            HttpPost httppost= new HttpPost("http://mylcpschoolbook.net/VirtualPOS/vposapi/");
            nameValuePairs = new ArrayList<>(2);
            nameValuePairs.add(new BasicNameValuePair("controller","merchant"));
            nameValuePairs.add(new BasicNameValuePair("action",  "verify"));
            nameValuePairs.add(new BasicNameValuePair("number",getIntent().getExtras().getString("number")));
            nameValuePairs.add(new BasicNameValuePair("pin",edtCode.getText().toString()));
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            HttpResponse response = httpclient.execute(httppost);
            Integer status = response.getStatusLine().getStatusCode();

            if(status == 200){
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(RegisterActivity4.this, "Registration Success", Toast.LENGTH_SHORT).show();
                    }
                });


                String result = EntityUtils.toString(response.getEntity());
                JSONObject jsonResponse;
                jsonResponse = new JSONObject(result);
                // CONVERT RESPONSE STRING TO JSON ARRAY
                JSONArray ja = jsonResponse.getJSONArray("data");

                // ITERATE THROUGH AND RETRIEVE FIELDS
                int n = ja.length();
                for (int i = 0; i < n; i++) {
                    // GET INDIVIDUAL JSON OBJECT FROM JSON ARRAY
                    JSONObject jo = ja.getJSONObject(i);
                    success = jo.optBoolean("success");

                }

                if(success){

                    //Update apps status on verification by sms
                    Apps myapp = db.getApp(Installation.appId(getApplicationContext()));
                    myapp.setStatus(1);
                    db.updateApp(myapp);

                    Intent regIntent = new Intent(RegisterActivity4.this, TicketingHomeActivity.class);
                    regIntent.putExtra("AppID", Installation.appId(getApplicationContext()));
                    dialog.dismiss();
                    startActivity(regIntent);

                }else{
                    Toast.makeText(RegisterActivity4.this, "verification was not successful", Toast.LENGTH_SHORT).show();
                    Log.e("ERROR", "Unsuccessful");
                }

            }else{
                Toast.makeText(RegisterActivity4.this, "Request could not be reached", Toast.LENGTH_SHORT).show();
                Log.e("ERROR", "Unsuccessful");
            }

        }catch(Exception e){
            // dialog.dismiss();
            System.out.println("Exception : " + e.getMessage());
            //Log.e("Exception", e.getMessage());
        }
        dialog.dismiss();
    }

    @Override
    public void onBackPressed()
    {

        // super.onBackPressed(); // Comment this super call to avoid calling finish()
    }
}
