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
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.busticket.amedora.busticketsrl.model.Apps;
import com.busticket.amedora.busticketsrl.utils.DatabaseHelper;
import com.busticket.amedora.busticketsrl.utils.Installation;

/**
 * Created by USER on 2/8/2016.
 */
public class LoginDriverActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private static final int REQUEST_SIGNUP = 0;
    DatabaseHelper db;
    TextView tvlogin;


    EditText _emailText;
    EditText _passwordText;
    Button _loginButton;
    String email ;
    String password;
    RequestQueue rqRequest;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_driver_login);

        _passwordText = (EditText) findViewById(R.id.input_password);
        rqRequest = Volley.newRequestQueue(this);
        _loginButton = (Button) findViewById(R.id.btn_login);
        tvlogin = (TextView)findViewById(R.id.tvloginError);

        _loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                login();
            }
        });

    }

    public void login() {
        Log.d(TAG, "Login");

        if (!validate()) {
            onLoginFailed();
            return;
        }

        _loginButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(LoginDriverActivity.this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        progressDialog.show();

        password = _passwordText.getText().toString();

        // TODO: Implement your own authentication logic here.

        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        // On complete call either onLoginSuccess or onLoginFailed
                        if (loginLocal()) {
                            onLoginSuccess();
                        } else {
                            onLoginFailed();
                        }

                        // onLoginFailed();
                        progressDialog.dismiss();
                    }
                }, 3000);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SIGNUP) {
            if (resultCode == RESULT_OK) {

                // TODO: Implement successful signup logic here
                // By default we just finish the Activity and log them in automatically
                this.finish();
            }
        }
    }
    @Override
    public void onBackPressed() {
        // disable going back to the MainActivity
        moveTaskToBack(true);
    }
    public void onLoginSuccess() {
        _loginButton.setEnabled(true);
        Intent myIntent = new Intent(LoginDriverActivity.this,TripHomeActivity.class);
        startActivity(myIntent);
        finish();
    }

    public void onLoginFailed() {
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();

        _loginButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;
        String password = _passwordText.getText().toString();

                /*if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    _emailText.setError("enter a valid email address");
                    valid = false;
                } else {
                    _emailText.setError(null);
                }
*/
        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            _passwordText.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        return valid;
    }

    public boolean loginLocal(){
        DatabaseHelper fb = new DatabaseHelper(getApplicationContext());

        String password = _passwordText.getText().toString();
        final Apps appLogin = fb.getApp(Installation.appId(getApplicationContext()));
        if( password.equals(appLogin.getDriverPassword())){
            appLogin.setDriverLoggedIn(1);
            DatabaseHelper kb =  new DatabaseHelper(getApplicationContext());
            if(kb.updateApp(appLogin) >0){
                serverLogin();
                return true;
            }else{
                tvlogin.setText("Login was not successful please contact developer");
                return false;
            }
        }else{
            tvlogin.setText("Invalid username or password combination please try again");
            return false;
        }
    }


    public void serverLogin(){
        String url = "http://platinumandco.com/slrtcapi/public/account/login/"+ Installation.appId(this);
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
