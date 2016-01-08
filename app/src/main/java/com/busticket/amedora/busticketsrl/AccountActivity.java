package com.busticket.amedora.busticketsrl;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.busticket.amedora.busticketsrl.model.Apps;
import com.busticket.amedora.busticketsrl.model.Ticket;
import com.busticket.amedora.busticketsrl.utils.DatabaseHelper;
import com.busticket.amedora.busticketsrl.utils.Installation;

import java.util.ArrayList;

/**
 * Created by USER on 1/8/2016.
 */
public class AccountActivity extends AppCompatActivity {
    Toolbar myToolbar;
    TextView tvAccBal, tvTicketBal;
    Apps apps;
    ArrayList<Ticket> ticket;
    DatabaseHelper db;
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_account);
        myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        db = new DatabaseHelper(getApplicationContext());
        apps = db.getApp(Installation.appId(getApplicationContext()));
        tvAccBal = (TextView) findViewById(R.id.tvCashBalance);
        tvTicketBal =(TextView)findViewById(R.id.tvTicketBalance);
        tvAccBal.setText(Double.toString(apps.getBalance()));
        ticket = db.getUnusedTickets();
        tvTicketBal.setText(Integer.toString(ticket.size()));
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

        switch (item.getItemId()) {
            case R.id.action_tickets:
                Intent intent = new Intent(AccountActivity.this,TicketListActivity.class);
                startActivity(intent);
                break;
            case R.id.action_ticketing:
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
