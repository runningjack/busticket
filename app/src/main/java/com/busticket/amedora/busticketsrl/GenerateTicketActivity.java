package com.busticket.amedora.busticketsrl;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.support.v7.widget.Toolbar;
import android.text.format.Time;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.busticket.amedora.busticketsrl.model.*;
import com.busticket.amedora.busticketsrl.utils.DatabaseHelper;
import com.busticket.amedora.busticketsrl.utils.Installation;
import com.zj.btsdk.BluetoothService;
import com.zj.btsdk.PrintPic;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.busticket.amedora.busticketsrl.androidserialportapi.sample.SerialPortActivity;
import hdx.HdxUtil;
//import and


/**
 * Created by Amedora on 12/6/2015.
 */
public class GenerateTicketActivity extends SerialPortActivity {
    DatabaseHelper db,db2;
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
    public byte[] qrCode = new  byte[256];
    private static final int REQUEST_ENABLE_BT = 2;
    BluetoothService mService = null;
    BluetoothDevice con_dev = null;
    private static final int REQUEST_CONNECT_DEVICE = 1;
    Apps apps;
    Toolbar myToolbar;

    private Context mContext = null;


    //POS Printer Declarations

    private final int ENABLE_BUTTON = 2;
    private final int SHOW_VERSION = 3;
    private final int UPDATE_FW = 4;
    private final int SHOW_PROGRESS = 5;
    private final int DISABLE_BUTTON = 6;
    private final int HIDE_PROGRESS=7;
    private final int REFRESH_PROGRESS=8;
    private final int SHOW_FONT_UPTAE_INFO=9;
    private final int SHOW_PRINTER_INFO_WHEN_INIT=10;
    private final byte  HDX_ST_NO_PAPER1 = (byte)(1<<0);     // 1 缺纸
    //private final byte  HDX_ST_BUF_FULL  = (byte)(1<<1);     // 1 缓冲满
    //private final byte  HDX_ST_CUT_ERR   = (byte)(1<<2);     // 1 Printer Cutter error
    private final byte  HDX_ST_HOT       = (byte)(1<<4);     // 1 The printer is too hot
    private final byte  HDX_ST_WORK      = (byte)(1<<5);     // 1 Printer in working condition

    private boolean stop = false;
    public static int BinFileNum = 0;
    public static boolean ver_start_falg = false;
    boolean Status_Start_Falg = false;
    byte [] Status_Buffer=new byte[300];
    int Status_Buffer_Index = 0;
    public static int update_ver_event = 0;
    public static boolean update_ver_event_err = false;
    public static StringBuilder strVer=new StringBuilder("922");
    public static StringBuilder oldVer=new StringBuilder("922");
    public static File BinFile;
    // EditText mReception;
    private static final String TAG = "TestPrintActivity";
    private static   String Error_State = "";
    Time time = new Time();
    int TimeSecond;
    public CheckBox myCheckBox;
    public ProgressDialog myDialog = null;
    MyHandler handler;
    private int iProgress   = 0;
    String Printer_Info =new String();

    public static boolean flow_start_falg = false;
    byte [] flow_buffer=new byte[300];

    public static Context context;

    Button btnTest;
    TextView tvTest;
    ExecutorService pool = Executors.newSingleThreadExecutor();
    PowerManager.WakeLock lock;
    int printer_status = 0;
    private ProgressDialog m_pDialog;

    private class MyHandler extends Handler {
        public void handleMessage(Message msg) {
            if (stop == true)
                return;
            switch (msg.what) {
                case DISABLE_BUTTON:
                    //Close_Button();
                    Log.d(TAG, "DISABLE_BUTTON");
                    break;
                case ENABLE_BUTTON:

                    btnTest.setEnabled(true);

                    Log.d(TAG, "ENABLE_BUTTON");
                    break;

                case SHOW_PROGRESS:
                    m_pDialog = new ProgressDialog(GenerateTicketActivity.this);
                    m_pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                    m_pDialog.setMessage((String)msg.obj);
                    m_pDialog.setIndeterminate(false);
                    m_pDialog.setCancelable(false);
                    m_pDialog.show();
                    break;
                case  HIDE_PROGRESS:
                    m_pDialog.hide();
                    break;
                case   REFRESH_PROGRESS :
                    m_pDialog.setProgress(iProgress);
                    break;
                case     SHOW_PRINTER_INFO_WHEN_INIT:
                    tvTest.setText(Printer_Info+strVer.toString());
                    break;
                default:
                    break;
            }
        }
    }



    @Override
    protected void onCreate(Bundle savedInstance){
        super.onCreate(savedInstance);
        setContentView(R.layout.layout_ticket_preview);
        myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        //getActionBar().setDisplayHomeAsUpEnabled(true);
        mContext = this;

        db = new DatabaseHelper(this);
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


        btnSendDraw = (Button) this.findViewById(R.id.btn_test);
        btnSendDraw.setOnClickListener(new ClickEvent());
        btnSearch = (Button) this.findViewById(R.id.btnSearch);
        btnSearch.setOnClickListener(new ClickEvent());
        btnClose = (Button) this.findViewById(R.id.btnClose);
        btnClose.setOnClickListener(new ClickEvent());
//btnSendDraw.setEnabled(true);
        tvPreview  = (TextView) findViewById(R.id.tvPreview);
        imgV = (ImageView)findViewById(R.id.imgView);
        if(ticket != null){
            //btnSearch.
            //btnSearch.setEnabled(true);
            btnSendDraw.setEnabled(true);
            if(apps.getAppMode() == 0){
                msg=    "Ticket ID:     "+ ticket.getTicket_id()+"\r\n"
                        +"Boarding:     "+ boardStage.getShort_name() +"\r\n"
                        +"Alighting:    "+ highlightStage.getShort_name()+ "\r\n"
                        +"Amount:       "+ "SLL "+ticket.getAmount()+"\r\n"
                        +"Bus No:       "+ bus +"\r\n"
                        +"Driver: "+busBoarded.getDriver()+"   Agent: "+ apps.getAgent_code().toUpperCase() +"\r\n"
                        +"Serial No:    "+ ticket.getSerial_no() +"\r\n"
                        +"Code:         "+ ticket.getScode().toUpperCase()+"\r\n";
                //tvPreview  = (TextView) findViewById(R.id.tvPreview);
            }else {
                String tripcount = Integer.toString(apps.getTripCount());
                msg=    "Ticket ID:     "+ ticket.getTicket_id()+"\r\n"
                        +"Trip:         "+ apps.getTripCount()+"\r\n"
                        +"Boarding:     "+ boardStage.getShort_name() +"\r\n"
                        +"Alighting:    "+ highlightStage.getShort_name()+ "\r\n"
                        +"Amount:       "+ "SLL "+ticket.getAmount()+"\r\n"
                        +"Bus No:       "+ bus +"\r\n"
                        +"Driver: "+apps.getDriverFname()+"   Agent: "+ apps.getAgent_code().toUpperCase() +"\r\n"
                        +"Serial No:    "+ ticket.getSerial_no() +"\r\n"
                        +"Code:         "+ ticket.getScode().toUpperCase()+"\r\n";
                //tvPreview  = (TextView) findViewById(R.id.tvPreview);
            }
        }else{
            btnSearch.setEnabled(false);
            btnSendDraw.setEnabled(false);
            msg ="Please load your account \r\n";
            msg +="there is no ticket in your account";
        }

        mService = new BluetoothService(this, mHandler);
        if(mService.isAvailable() == false){
            Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
            finish();
        }




        handler = new MyHandler();
        HdxUtil.SwitchSerialFunction(HdxUtil.SERIAL_FUNCTION_PRINTER);
        PowerManager pm = (PowerManager) getApplicationContext().getSystemService(Context.POWER_SERVICE);
        lock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK, TAG);
    }


    //@Override




    @Override
    public void onStart() {
        super.onStart();

        if( mService.isBTopen() == false)
        {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
        }
        try {

            //edtContext = (EditText) findViewById(R.id.txt_content);
            btnClose.setEnabled(false);
            tvPreview.setText(msg);
            //btnSendDraw.setEnabled(false);
        } catch (Exception ex) {
            Log.e("ERRORMSG", ex.getMessage());
        }

        try{
            kQueue = Volley.newRequestQueue(getApplicationContext());
        }catch(Exception e){
            Log.e("ERRORMSG", e.getMessage());
        }
    }


    //@Override
    /*protected void onDestroy() {
        super.onDestroy();
        if (mService != null)
            mService.stop();
        mService = null;
        tvPreview.setText(msg);
    }*/

    class ClickEvent implements View.OnClickListener {
        public void onClick(View v) {
            if (v == btnSearch) {
                Intent serverIntent = new Intent(  GenerateTicketActivity.this,DeviceListActivity.class);
                startActivityForResult(serverIntent,REQUEST_CONNECT_DEVICE);
            } else if (v == btnClose) {
                mService.stop();
            } else if (v == btnSendDraw) {

                db = new DatabaseHelper(getApplicationContext());
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
                if(ticket != null){
                    //btnSearch.
                   // btnSearch.setEnabled(true);
                    if(apps.getAppMode() == 0){
                        msg=    "Ticket ID:     "+ ticket.getTicket_id()+"\r\n"
                                +"Boarding:     "+ boardStage.getShort_name() +"\r\n"
                                +"Alighting:    "+ highlightStage.getShort_name()+ "\r\n"
                                +"Amount:       "+ "SLL "+ticket.getAmount()+"\r\n"
                                +"Bus No:       "+ bus +"\r\n"
                                +"Driver: "+busBoarded.getDriver()+"   Agent: "+ apps.getAgent_code().toUpperCase() +"\r\n"
                                +"Serial No:    "+ ticket.getSerial_no() +"\r\n"
                                +"Code:         "+ ticket.getScode().toUpperCase()+"\r\n";
                        //tvPreview  = (TextView) findViewById(R.id.tvPreview);
                    }else {
                        String tripcount = Integer.toString(apps.getTripCount());
                        msg=    "Ticket ID:     "+ ticket.getTicket_id()+"\r\n"
                                +"Trip:         "+ apps.getTripCount()+"\r\n"
                                +"Boarding:     "+ boardStage.getShort_name() +"\r\n"
                                +"Alighting:    "+ highlightStage.getShort_name()+ "\r\n"
                                +"Amount:       "+ "SLL "+ticket.getAmount()+"\r\n"
                                +"Bus No:       "+ bus +"\r\n"
                                +"Driver: "+apps.getDriverFname()+"   Agent: "+ apps.getAgent_code().toUpperCase() +"\r\n"
                                +"Serial No:    "+ ticket.getSerial_no() +"\r\n"
                                +"Code:         "+ ticket.getScode().toUpperCase()+"\r\n";
                        //tvPreview  = (TextView) findViewById(R.id.tvPreview);
                    }
                }else{
                    btnSearch.setEnabled(false);
                    btnSendDraw.setEnabled(false);
                    msg ="Please load your account \r\n";
                    msg +="there is no ticket in your account";
                }
                tvPreview.setText(msg);
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

                                //sendToPrinter();
                                //handler.sendMessage(handler.obtainMessage(DISABLE_BUTTON, 1, 0, null));
                                new WriteThread(0).start();
                                updateTicket();
                                //
                                handler.removeCallbacks(this);
                                Looper.myLooper().quit();
                            }
                        }, 2000);
                        Looper.loop();
                    }
                }).start();


            }
            //PrinterPowerOff();
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

                           // btnSendDraw.setEnabled(true);
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

                    //btnSendDraw.setEnabled(false);
                    break;
                case BluetoothService.MESSAGE_UNABLE_CONNECT:
                    Toast.makeText(getApplicationContext(), "Unable to connect device",
                            Toast.LENGTH_SHORT).show();
                    break;
            }
        }

    };


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
            ticketing.setFare(ticket.getAmount());
            ticketing.setBoard_stage(boardStage.getShort_name());
            ticketing.setHighlight_stage(highlightStage.getShort_name());
            ticketing.setTicketing_id(ticket.getTicket_id());
            ticketing.setScode(ticket.getScode());
            ticketing.setBus_no(busBoarded.getPlate_no());
            ticketing.setRoute(apps.getRoute_name());
            //Unfortunately route id is not set
            ticketing.setTripe(trip);
            ticketing.setSerial_no(ticket.getSerial_no());

            if(db.createTicketing(ticketing) >0){
                String url ="http://platinumandco.com/slrtcapi/public/ticketing/create";
                HashMap<String, String> params = new HashMap<String, String>();
                String ticket_id = Long.toString(ticket.getTicket_id());
                params.put("ticket_id",ticket_id);
                params.put("serial_no",ticket.getSerial_no());
                params.put("trip",trip);
                params.put("amount",Double.toString(ticket.getAmount()));
                params.put("app_id",apps.getApp_id());
                params.put("route_id",Integer.toString(apps.getRoute_id()));//Integer.toString(busBoarded.getRoute_id()));
                params.put("route_name",apps.getRoute_name());
                params.put("bus_id",Integer.toString(busBoarded.getBus_id()));
                params.put("bus_plate_no",busBoarded.getPlate_no());
                params.put("scode",ticket.getScode());
                params.put("highlight_stage",highlightStage.getShort_name());
                params.put("board_stage",boardStage.getShort_name());
                params.put("conductor",busBoarded.getConductor());

                if(apps.getAppMode()==0){
                    params.put("driver_id",Integer.toString(apps.getDriverID()));
                    params.put("driver",busBoarded.getDriver());
                }else{
                    params.put("driver_id",Integer.toString(apps.getDriverID()));
                    params.put("driver",apps.getLicenceNo());
                }

                params.put("status","0");
                params.put("agent_id",apps.getAgent_id());
                params.put("terminal_id",Integer.toString(boardStage.getTerminal_id()));
                params.put("created_at",db.getDateTime());
                JSONObject j = new JSONObject(params);

                //Log.d("ANDND",j.toString());

                JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST,url, j, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try{
                            String code = response.getString("code");
                            if(code.equals("200")){
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
                int socketTimeout = 20000; //30 seconds - change to what you want
                RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                req.setRetryPolicy(policy);
            kQueue.add(req);
            }
            dialog.dismiss();
            Toast.makeText(GenerateTicketActivity.this, "Ticket Successful Generated", Toast.LENGTH_SHORT).show();
        }else{
            dialog.dismiss();
            Toast.makeText(GenerateTicketActivity.this, "Unexpected Errors", Toast.LENGTH_SHORT).show();
        }
        dialog.dismiss();

    }

    public void onPause(){
        super.onPause();
        Bundle bundle = getIntent().getExtras();
        board 		= bundle.getString("Board");
        highlight 		= bundle.getString("Highlight");
        trip =bundle.getString("Trip");
        bus = bundle.getString("Bus");
        apps = db.getApp(Installation.appId(getApplicationContext()));
        boardStage = db.getTerminalByName(board);
        highlightStage = db.getTerminalByName(highlight);
        busBoarded = db.getBusByPlateNo(bus);
        DatabaseHelper dbr = new DatabaseHelper(getApplicationContext());
        ticket = dbr.getUnusedTicket();
       if(ticket != null){
           btnSendDraw.setEnabled(true);
           if(apps.getAppMode() == 0){
               msg=    "Ticket ID:     "+ ticket.getTicket_id()+"\r\n"
                       +"Boarding:     "+ boardStage.getShort_name() +"\r\n"
                       +"Alighting:    "+ highlightStage.getShort_name()+ "\r\n"
                       +"Amount:       "+ "SLL "+ticket.getAmount()+"\r\n"
                       +"Bus No:       "+ bus +"\r\n"
                       +"Driver: "+busBoarded.getDriver()+"   Agent: "+ apps.getAgent_code().toUpperCase() +"\r\n"
                       +"Serial No:    "+ ticket.getSerial_no() +"\r\n"
                       +"Code:         "+ ticket.getScode().toUpperCase()+"\r\n";
               //tvPreview  = (TextView) findViewById(R.id.tvPreview);
           }else {
               String tripcount = Integer.toString(apps.getTripCount());
               msg=    "Ticket ID:     "+ ticket.getTicket_id()+"\r\n"
                       +"Trip:         "+ apps.getTripCount()+"\r\n"
                       +"Boarding:     "+ boardStage.getShort_name() +"\r\n"
                       +"Alighting:    "+ highlightStage.getShort_name()+ "\r\n"
                       +"Amount:       "+ "SLL "+ticket.getAmount()+"\r\n"
                       +"Bus No:       "+ bus +"\r\n"
                       +"Driver: "+apps.getDriverFname()+"   Agent: "+ apps.getAgent_code().toUpperCase() +"\r\n"
                       +"Serial No:    "+ ticket.getSerial_no() +"\r\n"
                       +"Code:         "+ ticket.getScode().toUpperCase()+"\r\n";
               //tvPreview  = (TextView) findViewById(R.id.tvPreview);
           }

       }else{
           btnSearch.setEnabled(false);
           btnSendDraw.setEnabled(false);
           msg ="Please load your account \r\n";
           msg +="there is no ticket in your account";
       }
       tvPreview.setText(msg);
    }

    public void onResume(){
        super.onResume();
        Bundle bundle = getIntent().getExtras();
        board 		= bundle.getString("Board");
        highlight 		= bundle.getString("Highlight");
        trip =bundle.getString("Trip");
        bus = bundle.getString("Bus");
        apps = db.getApp(Installation.appId(getApplicationContext()));
        boardStage = db.getTerminalByName(board);
        highlightStage = db.getTerminalByName(highlight);
        busBoarded = db.getBusByPlateNo(bus);
        DatabaseHelper dbr = new DatabaseHelper(getApplicationContext());
        ticket = dbr.getUnusedTicket();
        if(ticket != null){
            btnSendDraw.setEnabled(true);
            if(apps.getAppMode() == 0){ //app is not running on triplog mode
                msg=    "Ticket ID:     "+ ticket.getTicket_id()+"\r\n"
                        +"Boarding:     "+ boardStage.getShort_name() +"\r\n"
                        +"Alighting:    "+ highlightStage.getShort_name()+ "\r\n"
                        +"Amount:       "+ "SLL "+ticket.getAmount()+"\r\n"
                        +"Bus No:       "+ bus +"\r\n"
                        +"Driver: "+busBoarded.getDriver()+"   Agent: "+ apps.getAgent_code().toUpperCase() +"\r\n"
                        +"Serial No:    "+ ticket.getSerial_no() +"\r\n"
                        +"Code:         "+ ticket.getScode().toUpperCase()+"\r\n";
                //tvPreview  = (TextView) findViewById(R.id.tvPreview);
            }else { //app is running on trip log mode
                String tripcount = Integer.toString(apps.getTripCount());

                msg=    "Ticket ID:     "+ ticket.getTicket_id()+"\r\n"
                        +"Trip:         "+ apps.getTripCount()+"\r\n"
                        +"Boarding:     "+ boardStage.getShort_name() +"\r\n"
                        +"Alighting:    "+ highlightStage.getShort_name()+ "\r\n"
                        +"Amount:       "+ "SLL "+ticket.getAmount()+"\r\n"
                        +"Bus No:       "+ bus +"\r\n"
                        +"Driver: "+apps.getLicenceNo().toUpperCase() +"\r\n"
                        +"Agent: "+ apps.getAgent_code().toUpperCase() +"\r\n"
                        +"Serial No:    "+ ticket.getSerial_no() +"\r\n"
                        +"Code:         "+ ticket.getScode().toUpperCase()+"\r\n";
                //tvPreview  = (TextView) findViewById(R.id.tvPreview);
            }


        }else{
            btnSearch.setEnabled(false);
            btnSendDraw.setEnabled(true);
            msg ="Please load your account \r\n";
            msg +="there is no ticket in your account";
        }
        tvPreview.setText(msg);
    }

     public Map<String, String> getHeaders() throws AuthFailureError {
        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Content-Type", "application/json");
        headers.put("charset", "utf-8");
        return headers;
    }

    public void sendToPrinter() {
        String lang = getString(R.string.strLang);
        // printImage();
        //1F 43 02 00 0d
        //ff();
        byte[] cmd = new byte[3];
        //byte[] qrCode = {(byte)0x1b, (byte)0x3a, (byte)0x05, (byte)0x00,(byte)0x31,(byte)0x32,(byte)0x33,(byte)0x34,(byte)0x35};
        // String[] qString = stringToHex(ticket.getScode());//.split("\\s");
                /*int[] data;*/
        int k = 4;
        qrCode[0] = (byte) 0x1b;
        qrCode[1] = (byte) 0x3a;
        qrCode[2] = (byte) 0x10;
        qrCode[3] = (byte) 0x00;
        String ticketingData = Long.toString(ticket.getTicket_id());//+"@"+ticket.getScode()
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
            mService.sendMessage("ROUTE: "+apps.getRoute_name()+"\n", "GBK");
            cmd[2] &= 0xEF;
            mService.write(cmd);
            mService.sendMessage(msg, "GBK");
            mService.write(qrCode);
            mService.write(cmd);
            mService.sendMessage("Thank you for your patronage", "GBK");
            mService.sendMessage("NOTE: Ticket is valid for same day of purchase", "GBK");
        }
    }

    public void updateAccount(){
        String url ="http://platinumandco.com/slrtcapi/public/account/update/"+apps.getApp_id();
        HashMap<String,String> params = new HashMap<String,String>();
        params.put("merchant_id",apps.getAgent_id());
        params.put("app_id",apps.getApp_id());
        params.put("balance",Double.toString(balance));

        JsonObjectRequest uAccount = new JsonObjectRequest(Request.Method.POST,url,new JSONObject(params), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try{
                    String code = response.getString("code");
                    if(code.equals("200")){
                        Toast.makeText(GenerateTicketActivity.this, "Account Updated Successfully", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(GenerateTicketActivity.this, "Unexpected Error! Account could not be Updated", Toast.LENGTH_SHORT).show();
                    }

                }catch(Exception ex){

                }

            }
        },new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(GenerateTicketActivity.this, "Internet Access Error! Account could not be updated.. ", Toast.LENGTH_SHORT).show();
            }
        });
        int socketTimeout = 20000;//30 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        uAccount.setRetryPolicy(policy);
        kQueue.add(uAccount);
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
                Intent intent = new Intent(GenerateTicketActivity.this,TicketListActivity.class);
                startActivity(intent);
                break;
            case R.id.action_ticketing:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDataReceived(final byte[] buffer, final int size,final int n) 	{
        int i;
        String strTemp;
        if(Status_Start_Falg == true)
        {
            for (i = 0; i < size; i++)
            {
                Status_Buffer[getStatus_Buffer_Index()]=buffer[i];
                setStatus_Buffer_Index(1+i);
            }
        }

        if (TestPrintActivity.ver_start_falg == true) {
            for (i = 0; i < size; i++) {
                TestPrintActivity.strVer.append(String.format("%c",(char) buffer[i]));
            }
        }
		/*
		 * 	public static boolean flow_start_falg = false;
		byte [] flow_buffer=new byte[300];

		 * */
        StringBuilder str = new StringBuilder();
        StringBuilder strBuild = new StringBuilder();
        for (i = 0; i < size; i++) {
            if(flow_start_falg == true)
            {
                if( (buffer[i] ==0x13) || ( buffer[i] ==0x11)  )
                {
                    flow_buffer[0]= buffer[i];

                }
            }
            str.append(String.format(" %x", buffer[i]));
            strBuild.append(String.format("%c", (char) buffer[i]));
        }
        Log.e(TAG, "onReceivedC= " + strBuild.toString());
        Log.e(TAG, "onReceivedx= " + str.toString());

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.e("quck3", "finish-> Activity -->3 "+ requestCode +"  "+resultCode);
        if (resultCode != 1)
        {
            Log.e("quck3", "finish->  resultCode != 1 ");
            return ;
        }



    }
    int getStatus_Buffer_Index() {
        return Status_Buffer_Index;

    }
    void setStatus_Buffer_Index(int v)
    {
        Status_Buffer_Index=v;
    }
    byte Get_Printer_Status()    {
        Status_Buffer[0]=0;
        Status_Buffer[1]=0;
        Status_Start_Falg = true;
        setStatus_Buffer_Index(0);
        sendCommand(0x1b, 0x76);
        Log.i(TAG, "Get_Printer_Status->0x1b,0x76");
        Time_Check_Start();

        while(true)
        {
            if(getStatus_Buffer_Index()>0)
            {

                Status_Start_Falg = false;
                Log.e(TAG,"Get_Printer_Status :"+Status_Buffer[0]);
                return Status_Buffer[0] ;
            }
            if(TimeIsOver(5))
            {
                Status_Start_Falg = false;
                Log.e(TAG,"Get_Printer_Status->TIME OVER:"+Status_Buffer[0]);
                return (byte)0xff;

            }
            sleep(50);
        }

    }

    void Time_Check_Start() {
        time.setToNow();
        TimeSecond = time.second;


    }

    boolean TimeIsOver(int second) {

        time.setToNow(); // ȡ��ϵͳʱ�䡣
        int t = time.second;
        if (t < TimeSecond) {
            t += 60;
        }

        if (t - TimeSecond > second) {
            return true;
        }
        return false;
    }

    void PrinterPowerOnAndWaitReady()    {
        //Status_Buffer_Index=0;
        //Status_Start_Falg = true;
        HdxUtil.SetPrinterPower(1);
        sleep(500);
    }
    void PrinterPowerOff()
    {
        HdxUtil.SetPrinterPower(0);
    }
    void Wait_Printer_Ready()    {
        byte status;

        while(true)
        {
            status = Get_Printer_Status() ;
            if(status== 0xff)
            {
                Log.e(TAG," time is out");
                return ;

            }

            if( (status & HDX_ST_WORK)>0 )
            {

                Log.d(TAG,"printer is busy");
            }
            else
            {
                Log.d(TAG," printer is ready");
                return;
            }
            sleep(50);
        }
    }
    //Returns true, the paper , no paper returns FALSE
    boolean  Printer_Is_Normal()   {
        byte status;


        status = Get_Printer_Status() ;

        if(status== 0xff)
        {
            Log.e(TAG,"huck time is out");
            Error_State="huck unkown err";
            return  false;

        }

        if( (status & HDX_ST_NO_PAPER1 )>0 )
        {

            Log.d(TAG,"huck is not paper");
            Error_State=getResources().getString(R.string.IsOutOfPaper);
            return false;
        }
        else if( (status & HDX_ST_HOT )>0 )
        {
            Log.d(TAG,"huck is too hot");
            Error_State=getResources().getString(R.string.PrinterNotNormal1);
            return false;
        }
        else
        {
            Log.d(TAG," huck is ready");
            return true;
        }


    }
    //Analyzing the printer installed paper , and if so, returns true , whether those returns false
    boolean Warning_When_Not_Normal()    {

        handler.sendMessage(handler.obtainMessage(DISABLE_BUTTON, 1, 0, null));
        if(  Printer_Is_Normal() )
        {

            Log.i(TAG,"quck_Is_Normal ok");
            return true;
        }
        else
        {
            handler.sendMessage(handler.obtainMessage(SHOW_FONT_UPTAE_INFO, 1, 0, Error_State));
            Log.d(TAG," quck_Is not_Paper");
            return false;

        }

    }
    /*
     * 	public static boolean flow_start_falg = false;
    byte [] flow_buffer=new byte[300];
     * */
    void flow_begin()    {

        flow_start_falg = true;
        flow_buffer[0]=  0x0;
        Log.i(TAG, "flow_begin ");

    }

    void flow_end()    {

        flow_start_falg = false;
        flow_buffer[0]=  0x0;
        Log.i(TAG, "flow_end ");
    }

    boolean  flow_check_and_Wait(int timeout) 	{
        boolean flag=false;
        Time_Check_Start();
        while(true)
        {
            sleep(5);
            if(flow_buffer[0]== 0)
            {
                return true;
                //flow_start_falg = false;
                //Log.e(TAG,"Get flow ready" );
                //return true ;
            }
            sleep(50);
            if(flow_buffer[0]== 0x13)//Suspend flag
            {
                if(flag ==false )
                {
                    flag=true;
                    Log.e(TAG,"Get flow 13" );
                }
                continue;
                //flow_start_falg = false;
                //return true ;
            }

            if(flow_buffer[0]== 0x11)
            {
                Log.e(TAG,"Get flow 11" );
                flow_buffer[0]=  0x0;
                return true;
                //flow_start_falg = false;
                //Log.e(TAG,"Get flow ready" );
                //return true ;
            }
            if(timeout !=0)
            {
                if(TimeIsOver(timeout))
                {
                    Log.e(TAG,"Get_Printer flow timeout");
                    return false;
                }
            }
            sleep(50);
        }
    }

    private void sendCommand(int... command) {
        try {
            for (int i = 0; i < command.length; i++) {
                mOutputStream.write(command[i]);
                // Log.e(TAG,"command["+i+"] = "+Integer.toHexString(command[i]));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        //sleep(1);
    }

    private void sendCharacterDemo() {

        Log.e("TAG", "#########sendCharacterDemo##########");//,0x1B,0x23,0x46
        //sendCommand(0x1B, 0x23, 0x23, 0x53, 0x4C, 0x41, 0x4E, 0x0e); // taiwan

        /*cmd[0] = 0x1b;
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
            mService.sendMessage("NOTE: Ticket is valid for same day of purchase", "GBK");
        }*/
       //new BmpThread().start();
        //Wait_Printer_Ready();
        //ConsoleActivity.this.sleep(1000);


        sendCommand(0x1B, 0x23, 0x23, 0x53, 0x4C, 0x41, 0x4E, 0x0e );
        try {
            SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
            String startTime="";
            try{
                Date t = new Date();
                startTime = df.format(t);
            }catch (Exception e){
                Log.d(TAG,e.getMessage());
            }
            mOutputStream.write(("DATE: " + db.getDate() + " " + startTime).getBytes()); //Output stream write text with getBytes
            //mOutputStream.write(("TIME: " + startTime + "\n").getBytes());
            sendCommand(0x0a); // new line/carriage return
            sendCommand(0x1D, 0x21, 0x01);
            mOutputStream.write(("ROUTE: " + apps.getRoute_name() + "\n").getBytes());
            //sendCommand(0x0a);
            sendCommand(0x1D, 0x21, 0x00); // cancel double height
            mOutputStream.write(tvPreview.getText().toString().getBytes());
            //byte[] cmd = new byte[3];
            //byte[] qrCode = {(byte)0x1b, (byte)0x3a, (byte)0x05, (byte)0x00,(byte)0x31,(byte)0x32,(byte)0x33,(byte)0x34,(byte)0x35};
            // String[] qString = stringToHex(ticket.getScode());//.split("\\s");
                /*int[] data;*/
            int k = 0;

            String ticketingData = Long.toString(ticket.getTicket_id())+"~"+ticket.getScode();
            for (char ch : ticketingData.toCharArray()) {
                qrCode[k] = (byte) (int) ch;
                k++;
            }

            sendCommand(0x1d, 0x28, 0x6b, 150 + 3 + 1, 0x00, 0x31, 0x50, 0x30); // command to write QR code

           // sendCommand(qrCode);
            mOutputStream.write((qrCode));

            sendCommand(0x1d, 0x28, 0x6b, 0x03, 0x00, 0x31, 0x51, 0x30);
            sendCommand(0x1b, 0x4a, 0x30); // line feed
            sendCommand(0x0a);

            mOutputStream.write(("Thank you for your patronage!").getBytes());
            sendCommand(0x0a);//newline
            mOutputStream.write(("NOTE: Ticket is valid for same day of purchase").getBytes());
            sendCommand(0x0a);
            sendCommand(0x1b,0x4d,0x49);
            mOutputStream.write(("*Powered by Platinum & Co Ltd.*").getBytes());
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        sendCommand(0x0a,0x0a);
    }

    private class BmpThread extends Thread {
        public BmpThread() {
        }

        public void run() {
            super.run();
            PrinterPowerOnAndWaitReady();
            if(!Warning_When_Not_Normal())
            {
                PrinterPowerOff();
                return;
            }
            Wait_Printer_Ready();
            //ConsoleActivity.this.sleep(1000);
            lock.acquire();
            try {
                Resources r = getResources();
                // Reads the data stream resources
                InputStream is = r.openRawResource(R.raw.srltclogo);
                BitmapDrawable bmpDraw = new BitmapDrawable(is);
                Bitmap bmp = bmpDraw.getBitmap();
                PrintBmp(10, bmp);

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                Wait_Printer_Ready();
                lock.release();
                //ConsoleActivity.this.sleep(1000);
                //HdxUtil.SetPrinterPower(0);
                PrinterPowerOff();
            }
            handler.sendMessage(handler
                    .obtainMessage(ENABLE_BUTTON, 1, 0, null));
        }
    }

    private void sleep(int ms) {
        // Log.d(TAG,"start sleep "+ms);
        try {
            java.lang.Thread.sleep(ms);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // Log.d(TAG,"end sleep "+ms);
    }

    private class WriteThread extends Thread {
        int  action_code;

        public WriteThread(int  code) {
            action_code = code;
        }

        public void run() {
            super.run();
            PrinterPowerOnAndWaitReady();
            if(!Warning_When_Not_Normal())
            {
                PrinterPowerOff();
                return;
            }

            lock.acquire();
            try {

                Wait_Printer_Ready();
                switch(action_code)
                {
                    case 0:

                        try{
                            Resources r = getResources();
                            // Reads the data stream resources
                            InputStream is = r.openRawResource(R.raw.srltclogo);
                            BitmapDrawable bmpDraw = new BitmapDrawable(is);
                            Bitmap bmp = bmpDraw.getBitmap();
                            PrintBmp(60, bmp);

                        }catch(Exception e){

                        }

                        sendCharacterDemo();
                        sendCommand(0x0a);
                        sendCommand(0x1d,0x56,0x42,0x20);
                        sendCommand(0x1d, 0x56, 0x30);
                        Log.e("quck2", " print char test"   );
                        break;
                    case 1:
                        Log.e("quck2", "Print Code test  ");
                        // sendCodeDemo();
                    default:
                        break;
                }
                //TestPrintActivity.this.sleep(4000);

            } finally {
                // Wait_Printer_Ready();
                // lock.release();
                // PrinterPowerOff();
                //handler.sendMessage(handler.obtainMessage(ENABLE_BUTTON, 1,0, null));
            }

        }
    }

    public void PrintBmp(int startx, Bitmap bitmap) throws IOException {
        // byte[] start1 = { 0x0d,0x0a};
        byte[] start2 = { 0x1D, 0x76, 0x30, 0x30, 0x00, 0x00, 0x01, 0x00 };

        int width = bitmap.getWidth() + startx;
        int height = bitmap.getHeight();

        if (width > 384)
            width = 384;
        int tmp = (width + 7) / 8;
        byte[] data = new byte[tmp];
        byte xL = (byte) (tmp % 256);
        byte xH = (byte) (tmp / 256);
        start2[4] = xL;
        start2[5] = xH;
        start2[6] = (byte) (height % 256);
        ;
        start2[7] = (byte) (height / 256);
        ;
        mOutputStream.write(start2);
        for (int i = 0; i < height; i++) {

            for (int x = 0; x < tmp; x++)
                data[x] = 0;
            for (int x = startx; x < width; x++) {
                int pixel = bitmap.getPixel(x - startx, i);
                if (Color.red(pixel) == 0 || Color.green(pixel) == 0 || Color.blue(pixel) == 0) {
                    //High on the left , so the use of 128 to the right
                    data[x / 8] += 128 >> (x % 8);
                    //(byte) (128 >> (y % 8));
                }
            }


            mOutputStream.write(data);
			/*
			 * try { Thread.sleep(5); } catch (InterruptedException e) { }
			 */
        }
    }


    public String pictureFromTxt() {
        StringBuffer sb = new StringBuffer();
        try {
            InputStream is = getAssets().open("printlogo.txt");
            int c;
            while ((c = is.read()) != -1) {
                sb.append((char) c);
            }

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return sb.toString();
    }

    protected void onDestroy() {
        super.onDestroy();
        stop = true;
        //PrinterPowerOff();
        Log.e(TAG, "onDestroy");

    }

}
