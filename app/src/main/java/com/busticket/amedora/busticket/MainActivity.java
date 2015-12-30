package com.busticket.amedora.busticket;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.busticket.amedora.busticket.model.Apps;
import com.busticket.amedora.busticket.utils.*;

import java.io.File;


public class MainActivity extends Activity {
    Button btnRgister,btnLogin;
    private Handler mHandler = new Handler();
    private static final String INSTALLATION = "INSTALLATION";
    int status =0;
    DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

            setContentView(R.layout.activity_main);
            db = new DatabaseHelper(getApplicationContext());
       mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                File installation = new File(getApplicationContext().getFilesDir(),INSTALLATION);

                if(installation.exists()){
                    String AppID = Installation.readInstallationFile(installation);

                    if(AppID ==null ){

                        Intent intent = new Intent(MainActivity.this, LoadingFeatures.class);
                        startActivity(intent);

                    }else{
                        Apps apps = db.getApp(AppID);
                        if(apps == null){
                            Intent intent = new Intent(MainActivity.this, LoadingFeatures.class);
                            startActivity(intent);
                        }else if ((apps.getStatus() == 0)){
                            Intent intent = new Intent(MainActivity.this, LoadingFeatures.class);
                            startActivity(intent);
                        }else if((apps.getStatus() == 1)){
                            Intent intent = new Intent(MainActivity.this, TicketingHomeActivity.class);
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

        btnLogin = (Button)findViewById(R.id.btnstartlogin);
        btnRgister = (Button)findViewById(R.id.btnstartregister);

        btnRgister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,RegisterActivity1.class);
                startActivity(intent);
            }
        });
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
