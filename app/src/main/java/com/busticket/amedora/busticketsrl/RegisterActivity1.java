package com.busticket.amedora.busticketsrl;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.busticket.amedora.busticketsrl.model.Route;
import com.busticket.amedora.busticketsrl.model.Terminal;
import com.busticket.amedora.busticketsrl.utils.*;

import org.json.JSONArray;
import org.json.JSONObject;


/**
 * Created by Amedora on 7/10/2015.
 */
public class RegisterActivity1 extends Activity {
    EditText edtStore,edtContact,edtEmail;
    String StoreName,ContactPerson,Email;
    Button btnNext;
    RequestQueue mQueue ;
    @Override
    protected void onCreate(Bundle saveInstanceState){
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_register_screen1);
        btnNext = (Button) findViewById(R.id.btnregnext1);
        edtStore = (EditText)findViewById(R.id.edtStoreName);
        edtContact = (EditText)findViewById(R.id.edtStoreContact);
        edtEmail = (EditText)findViewById(R.id.edtEmail);
        StoreName   = edtStore.getText().toString();
        ContactPerson   =   edtContact.getText().toString();
        Email   = edtEmail.getText().toString();
        mQueue = Volley.newRequestQueue(getApplicationContext());
        edtEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                Validation.isEmailAddress(edtEmail, true);
            }
        });
        insertTerminals();
        getRoutes();
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkValidation ()){
                    sendData();
                }else{
                    Toast.makeText(getApplicationContext(),
                            "Company name or contact name or email fields must not be empty",
                            Toast.LENGTH_LONG).show();
                }

            }
        });
    }

    public void sendData(){
// Submit your form here. your form is valid

        Toast.makeText(this, "Submitting form...", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(RegisterActivity1.this,RegisterActivity2.class);

        intent.putExtra("SName",edtStore.getText().toString());
        intent.putExtra("SContact",edtContact.getText().toString());
        intent.putExtra("Email",edtEmail.getText().toString());
        startActivity(intent);
    }


    private boolean checkValidation() {
        boolean ret = true;

        if (!Validation.hasText(edtStore)) ret = false;
        if (!Validation.hasText(edtContact)) ret = false;
        if (!Validation.isEmailAddress(edtEmail, true)) ret = false;
        //if (!Validation.isPhoneNumber(edtLname, false)) ret = false;

        return ret;
    }

    public void getRoutes(){
        String url = "http://41.77.173.124:81/busticketAPI/route/index";

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET,url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try{
                    // Iterator<String> iter = response.keys();
                    int ja = response.length();
                    for(int i=0; i<ja; i++){
                        //String key = iter.next();
                        JSONObject term = (JSONObject) response.get(i);
                        //JSONArray jsonArrayTerminals = response.getJSONArray("data");
                        DatabaseHelper db = new DatabaseHelper(getApplicationContext());
                        Route route = new Route();
                        route.setDescription(term.getString("description"));
                        route.setShort_name(term.getString("short_name"));
                        route.setName(term.getString("name"));
                        route.setDistance(term.getString("distance"));
                        db.createRoute(route);
                    }

                }catch (Exception e){

                    Toast.makeText(RegisterActivity1.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(RegisterActivity1.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                // hide the progress dialog
                //pDialog.hide();
            }
        });
        mQueue.add(jsonArrayRequest);
    }

    private void insertTerminals(){
        //RequestQueue requestQueue = new RequestQueue(m)

        //mQueue = Volley.newRequestQueue(getApplicationContext());
        String url ="http://41.77.173.124:81/busticketAPI/terminals/index";
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET,url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try{
                    // Iterator<String> iter = response.keys();
                    int ja = response.length();
                    for(int i=0; i<ja; i++){
                        //String key = iter.next();
                        JSONObject term = (JSONObject) response.get(i);
                        //JSONArray jsonArrayTerminals = response.getJSONArray("data");
                        DatabaseHelper db = new DatabaseHelper(getApplicationContext());
                        Terminal t = db.getTerminalByName(term.getString("short_name"));
                        if(db.ifExists(t)){

                        }else{
                            Terminal terminal = new Terminal();
                            terminal.setTerminal_id(term.getInt("id"));
                            terminal.setShort_name(term.getString("short_name"));
                            terminal.setName(term.getString("name"));
                            terminal.setDistance(term.getString("distance"));
                            terminal.setOne_way_from_fare(term.getDouble("one_way_from_fare"));
                            terminal.setOne_way_to_fare(term.getDouble("one_way_to_fare"));
                            terminal.setDistance(term.getString("distance"));
                            terminal.setRoute_id(term.getInt("route_id"));
                            terminal.setGeodata(term.getString("geodata"));

                            db.createTerminal(terminal);
                        }

                    }

                }catch (Exception e){

                    Toast.makeText(RegisterActivity1.this, e.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(RegisterActivity1.this, error.toString(), Toast.LENGTH_SHORT).show();
                // hide the progress dialog
                //pDialog.hide();
            }
        });
        mQueue.add(jsonArrayRequest);
    }


}
