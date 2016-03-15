package com.busticket.amedora.busticketsrl;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.busticket.amedora.busticketsrl.utils.*;

import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;

import java.util.List;


/**
 * Created by Amedora on 7/10/2015.
 */
public class RegisterActivity2 extends Activity {
    EditText edtPassword,edtConfirm;
    String StoreName,StoreContact,mailmy,imei;
    Button btnNext,btnBack;
    HttpPost httppost;
    HttpClient httpclient;
    List<NameValuePair> nameValuePairs;
    ProgressDialog dialog  =null;
    String AppID = "";

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_screen2);
        edtPassword = (EditText) findViewById(R.id.edtPassword);
        edtConfirm  =   (EditText)findViewById(R.id.edtConfirm);
        Bundle bundle = getIntent().getExtras();
        StoreName 		= bundle.getString("SName");
        StoreContact 		= bundle.getString("SContact");
        mailmy 		= bundle.getString("Email");
        Log.e("MAil", getIntent().getExtras().getString("Email"));
        btnBack     = (Button) findViewById(R.id.btnregback2);
        btnNext     = (Button) findViewById(R.id.btnregnext2);



        edtConfirm.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                Validation.isPasswordMatch(edtPassword, edtConfirm, true);
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(checkValidation()){
                    sendData();
                }else{
                    Toast.makeText(getApplicationContext(),
                            "Password mismatch please ensure that you enter the same character in the two input fields",
                            Toast.LENGTH_LONG).show();
                }

            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }


    private void  sendData(){

        Intent intent = new Intent(RegisterActivity2.this,RegisterActivity3.class);
        //TelephonyManager tmgt = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        //imei = tmgt.getDeviceId();
        intent.putExtra("SContact",getIntent().getExtras().getString("SContact"));

        intent.putExtra("SName",getIntent().getExtras().getString("SName"));
        intent.putExtra("Email",getIntent().getExtras().getString("Email"));
        intent.putExtra("Password",edtPassword.getText().toString());
        startActivity(intent);
    }

    private boolean checkValidation() {
        boolean ret = true;
        if (!Validation.hasText(edtPassword)) ret = false;
        if (!Validation.isPasswordMatch(edtPassword, edtConfirm, true)) ret = false;
        //if (!Validation.isPhoneNumber(edtLname, false)) ret = false;
        return ret;
    }



}
