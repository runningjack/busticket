package com.busticket.amedora.busticket;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.busticket.amedora.busticket.model.*;
import com.busticket.amedora.busticket.utils.DatabaseHelper;
import com.busticket.amedora.busticket.utils.Installation;
import com.zj.btsdk.BluetoothService;
import com.zj.btsdk.PrintPic;


import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.android.Contents;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.encoder.QRCode;

import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by Amedora on 12/6/2015.
 */
public class GenerateTicketActivity extends AppCompatActivity {
    DatabaseHelper db = new DatabaseHelper(this);
    String board,highlight,trip,bus;
    Terminal boardStage,highlightStage;
    Bus busBoarded;
    Ticket ticket;
    RequestQueue kQueue;
    public final static int WHITE = 0xFFFFFFFF;
    public final static int BLACK = 0xFF000000;
    double balance=0;
    Button btnSearch;
    Button btnSendDraw;
    String msg;
    ProgressDialog dialog  =null;
    Button btnClose;
    EditText edtContext;
    TextView    tvPreview;
    ImageView imgV;
    public byte[] qrCode = new  byte[20];
    private static final int REQUEST_ENABLE_BT = 2;
    BluetoothService mService = null;
    BluetoothDevice con_dev = null;
    private static final int REQUEST_CONNECT_DEVICE = 1;
    Apps apps;
    protected void onCreate(Bundle savedInstance){
        super.onCreate(savedInstance);
        setContentView(R.layout.layout_ticket_preview);
        Bundle bundle = getIntent().getExtras();
        board 		= bundle.getString("Board");
        highlight 		= bundle.getString("Highlight");
        trip =bundle.getString("Trip");
        bus = bundle.getString("Bus");
        apps = db.getApp(Installation.appId(getApplicationContext()));
        boardStage = db.getTerminalByName(board);
        highlightStage = db.getTerminalByName(highlight);
        busBoarded = db.getBusByPlateNo(bus);
        ticket = db.getUnusedTicket();

        tvPreview  = (TextView) findViewById(R.id.tvPreview);
        imgV = (ImageView)findViewById(R.id.imgView);
        msg=    "Ticket ID:     "+ ticket.getTicket_id()+"\n\n"
                +"Boarding:     "+ boardStage.getShort_name() +"\n\n"
                +"Alighting:    "+ highlightStage.getShort_name()+ "\n\n"
                +"Amount:       "+ "SLL "+ticket.getAmount()+"\n\n"
                +"Bus No:       "+ bus +"\n\n"
                +"Driver: "+busBoarded.getDriver()+"   Agent: "+busBoarded.getConductor() +"\n\n"
                +"Serial No:    "+ ticket.getSerial_no() +"\n\n"
                +"Code:         "+ ticket.getScode().toUpperCase()+"\n\n";

        tvPreview.setText(msg);

        mService = new BluetoothService(this, mHandler);
        if(mService.isAvailable() == false){
            Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        if( mService.isBTopen() == false)
        {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
        }
        try {
            btnSendDraw = (Button) this.findViewById(R.id.btn_test);
            btnSendDraw.setOnClickListener(new ClickEvent());
            btnSearch = (Button) this.findViewById(R.id.btnSearch);
            btnSearch.setOnClickListener(new ClickEvent());
            btnClose = (Button) this.findViewById(R.id.btnClose);
            btnClose.setOnClickListener(new ClickEvent());
            //edtContext = (EditText) findViewById(R.id.txt_content);
            btnClose.setEnabled(false);

            btnSendDraw.setEnabled(false);
        } catch (Exception ex) {
            Log.e("ERRORMSG", ex.getMessage());
        }

        try{
            kQueue = Volley.newRequestQueue(getApplicationContext());
        }catch(Exception e){
            Log.e("ERRORMSG", e.getMessage());
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mService != null)
            mService.stop();
        mService = null;
    }

    class ClickEvent implements View.OnClickListener {
        public void onClick(View v) {
            if (v == btnSearch) {
                Intent serverIntent = new Intent(  GenerateTicketActivity.this,DeviceListActivity.class);
                startActivityForResult(serverIntent,REQUEST_CONNECT_DEVICE);
            } else if (v == btnClose) {
                mService.stop();
            } else if (v == btnSendDraw) {
                dialog = ProgressDialog.show(GenerateTicketActivity.this, "",
                        "Generating Ticket...", true);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Looper.prepare();
                        final Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                sendToPrinter();
                                updateTicket();
                                handler.removeCallbacks(this);
                                Looper.myLooper().quit();
                            }
                        }, 2000);
                        Looper.loop();
                    }
                }).start();
            }
        }
    }

    public void stringToHex(String string) {
        StringBuilder buf = new StringBuilder(200);
        int k =4;
        for (char ch: string.toCharArray()) {
            if (buf.length() > 0)

                qrCode[k] =(byte)(int) ch;
            //tvPreview.setText(qrCode[k]);
            k++;
        }

    }


    private final Handler mHandler = new Handler() {
        @Override

        public void handleMessage(Message msg) {
            switch (msg.what) {
                case BluetoothService.MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {
                        case BluetoothService.STATE_CONNECTED:
                           // printQrCode();
                            Toast.makeText(getApplicationContext(), "Connect successful",
                                    Toast.LENGTH_SHORT).show();
                            btnClose.setEnabled(true);

                            btnSendDraw.setEnabled(true);
                            break;
                        case BluetoothService.STATE_CONNECTING:
                            Log.d("PRINTER", "Connecting.....");
                            break;
                        case BluetoothService.STATE_LISTEN:
                        case BluetoothService.STATE_NONE:
                            Log.d("PRINTER","Not Available.....");
                            break;
                    }
                    break;
                case BluetoothService.MESSAGE_CONNECTION_LOST:
                    Toast.makeText(getApplicationContext(), "Device connection was lost",
                            Toast.LENGTH_SHORT).show();
                    btnClose.setEnabled(false);

                    btnSendDraw.setEnabled(false);
                    break;
                case BluetoothService.MESSAGE_UNABLE_CONNECT:
                    Toast.makeText(getApplicationContext(), "Unable to connect device",
                            Toast.LENGTH_SHORT).show();
                    break;
            }
        }

    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_ENABLE_BT:
                if (resultCode == Activity.RESULT_OK) {
                    Toast.makeText(this, "Bluetooth open successful", Toast.LENGTH_LONG).show();
                } else {
                    finish();
                }
                break;
            case  REQUEST_CONNECT_DEVICE:
                if (resultCode == Activity.RESULT_OK) {
                    String address = data.getExtras()
                            .getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
                    con_dev = mService.getDevByMac(address);

                    mService.connect(con_dev);
                }
                break;
        }
    }
   // @SuppressLint("SdCardPath")
    private void printImage() {
        byte[] sendData = null;
        PrintPic pg = new PrintPic();
        pg.initCanvas(384);
        pg.initPaint();
        File file = new File(Environment.getExternalStorageDirectory() + "/Busticket/Images/"+ticket.getSerial_no()+".jpg");
        if(file.exists()) {
            pg.drawImage(0, 0, Environment.getExternalStorageDirectory() + "/Busticket/Images/" + ticket.getSerial_no() + ".jpg");
            sendData = pg.printDraw();
            mService.write(sendData);
        }
    }

    private void printQrCode(){
        try{
            Bitmap bitmap = encodeAsBitmap(msg);
            imgV.setImageBitmap(bitmap);
            storeImage(bitmap, ticket.getSerial_no() + ".jpg");
        }catch (Exception e){
            Log.w("TAG", "Error saving image file: " + e.getMessage());
        }

    }
    //@SuppressLint("SdCardPath")
    private boolean storeImage(Bitmap imageData, String filename) {
        //get path to external storage (SD card)

        String iconsStoragePath = Environment.getExternalStorageDirectory() + "/Busticket/Images/";
        File sdIconStorageDir = new File(iconsStoragePath);
        //create storage directories, if they don't exist
        sdIconStorageDir.mkdirs();

        try {
            String filePath = sdIconStorageDir.toString() + filename;
            File file = new File(filePath);
            if (file.exists ()) file.delete ();
            FileOutputStream fileOutputStream = new FileOutputStream(filePath);

            BufferedOutputStream bos = new BufferedOutputStream(fileOutputStream);

            //choose another format if PNG doesn't suit you
            imageData.compress(Bitmap.CompressFormat.JPEG, 100, bos);

            bos.flush();
            bos.close();

        } catch (FileNotFoundException e) {
            Log.w("TAG", "Error saving image file: " + e.getMessage());
            return false;
        } catch (IOException e) {
            Log.w("TAG", "Error saving image file: " + e.getMessage());
            return false;
        }

        return true;
    }

    Bitmap encodeAsBitmap(String str) throws WriterException {
        BitMatrix result;
        WindowManager manager = (WindowManager) getSystemService(WINDOW_SERVICE);
        Display display = manager.getDefaultDisplay();
        Point point = new Point();
        display.getSize(point);
        int width = point.x;
        int height = point.y;
        try {
            result = new MultiFormatWriter().encode(str, BarcodeFormat.QR_CODE, width, width, null);
        }catch (IllegalArgumentException iae) {
            return null;
        }
        int w = result.getWidth();
        int h = result.getHeight();
        int[] pixels = new int[w * h];
        for (int y = 0; y < h; y++) {
            int offset = y * w;
            for (int x = 0; x < w; x++) {
                pixels[offset + x] = result.get(x, y) ? BLACK : WHITE;
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, width, 0, 0, w, h);
        return bitmap;
    }

    public void generateTicketing(){

    }

    public void updateTicket(){
        ticket.setStatus(1);
        if(db.updateTicket(ticket) > 0){
            Ticketing ticketing = new Ticketing();
            if(trip == "1"){
                ticketing.setFare(highlightStage.getOne_way_to_fare());
            }else{
                ticketing.setFare(highlightStage.getOne_way_from_fare());
            }

            ticketing.setDriver(busBoarded.getDriver());
            ticketing.setConductor(busBoarded.getConductor());
            ticketing.setQty(1);
            ticketing.setBoard_stage(boardStage.getShort_name());
            ticketing.setHighlight_stage(highlightStage.getShort_name());
            ticketing.setTicketing_id(ticket.getTicket_id());
            ticketing.setScode(ticket.getScode());
            ticketing.setBus_no(busBoarded.getPlate_no());
            ticketing.setRoute(apps.getRoute_name());
            ticketing.setTripe(trip);
            ticketing.setSerial_no(ticket.getSerial_no());

            if(db.createTicketing(ticketing) >0){
                String url ="http://41.77.173.124:81/busticketAPI/ticketing/create/";
                HashMap<String, String> params = new HashMap<String, String>();
                String ticket_id = Integer.toString(ticket.getTicket_id());
                params.put("ticket_id",ticket_id);
                params.put("serial_no",ticket.getSerial_no());
                params.put("trip",trip);
                params.put("app_id",apps.getApp_id());
                params.put("route_id",Integer.toString(busBoarded.getRoute_id()));
                params.put("route_name",apps.getRoute_name());
                params.put("bus_id",Integer.toString(busBoarded.getBus_id()));
                params.put("bus_plate_no",busBoarded.getPlate_no());
                params.put("scode",ticket.getScode());
                params.put("alight_stage",highlightStage.getShort_name());
                params.put("board_stage",boardStage.getShort_name());
                params.put("conductor",busBoarded.getConductor());
                params.put("driver",busBoarded.getDriver());
                params.put("status","0");
                params.put("created_at",db.getDateTime());

                JsonObjectRequest req = new JsonObjectRequest(url, new JSONObject(params), new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try{
                            if(Boolean.getBoolean(response.getString("success")) == true){
                                apps.setBalance(apps.getBalance() - ticket.getAmount());
                                balance = apps.getBalance() - ticket.getAmount();
                                db.updateApp(apps);

                                updateAccount();
                                Toast.makeText(GenerateTicketActivity.this,"Record Updated to server Successfully", Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(GenerateTicketActivity.this,"Unexpected Error! Ticket could not be uploaded to server ", Toast.LENGTH_SHORT).show();
                            }
                        }catch (Exception e){
                            Toast.makeText(GenerateTicketActivity.this,e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        VolleyLog.d("BTICKET", "Error: " + error.getMessage());
                        Toast.makeText(GenerateTicketActivity.this,error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            kQueue.add(req);
            }
            dialog.dismiss();
            Toast.makeText(GenerateTicketActivity.this, "Ticket Successful Generated", Toast.LENGTH_SHORT).show();
        }else{
            dialog.dismiss();
            Toast.makeText(GenerateTicketActivity.this, "Unexpected Errors", Toast.LENGTH_SHORT).show();
        }
    }
     public Map<String, String> getHeaders() throws AuthFailureError {
        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Content-Type", "application/json");
        headers.put( "charset", "utf-8");
        return headers;
    }

    public void sendToPrinter() {
        String lang = getString(R.string.strLang);
        // printImage();

        byte[] cmd = new byte[3];
        //byte[] qrCode = {(byte)0x1b, (byte)0x3a, (byte)0x05, (byte)0x00,(byte)0x31,(byte)0x32,(byte)0x33,(byte)0x34,(byte)0x35};
        // String[] qString = stringToHex(ticket.getScode());//.split("\\s");
                /*int[] data;*/
        int k = 4;
        qrCode[0] = (byte) 0x1b;
        qrCode[1] = (byte) 0x3a;
        qrCode[2] = (byte) 0x10;
        qrCode[3] = (byte) 0x00;
        String ticketingData = Integer.toString(ticket.getTicket_id());//+"@"+ticket.getScode()
        for (char ch : ticketingData.toCharArray()) {
            qrCode[k] = (byte) (int) ch;
            k++;
        }

       // Route route = db.getRouteByName("IKD-CMS");
        cmd[0] = 0x1b;
        cmd[1] = 0x21;
        if ((lang.compareTo("en")) == 0) {
            cmd[2] |= 0x10;
            mService.write(cmd);
            mService.sendMessage("BUS TICKET    ROUTE: "+apps.getRoute_name()+"\n", "GBK");
            cmd[2] &= 0xEF;
            mService.write(cmd);
            mService.sendMessage(msg, "GBK");
            mService.write(qrCode);
            mService.write(cmd);
            mService.sendMessage("Thank you for your patronage", "GBK");
        }
    }


    public void updateAccount(){
        String url ="http://41.77.173.124:81/busticketAPI/account/update/";
        HashMap<String,String> params = new HashMap<String,String>();
        params.put("merchant_id",apps.getAgent_id());
        params.put("app_id",apps.getApp_id());
        params.put("balance",Double.toString(balance));

        JsonObjectRequest uAccount = new JsonObjectRequest(url,new JSONObject(params), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Toast.makeText(GenerateTicketActivity.this, "Account Updated", Toast.LENGTH_SHORT).show();
            }
        },new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(GenerateTicketActivity.this, "Account balance could not be updated Unexpected Errors", Toast.LENGTH_SHORT).show();
            }
        });
        kQueue.add(uAccount);
    }

}
