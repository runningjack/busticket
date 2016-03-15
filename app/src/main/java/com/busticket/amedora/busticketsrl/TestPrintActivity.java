package com.busticket.amedora.busticketsrl;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.text.format.Time;
import android.view.View;
import android.widget.Button;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.busticket.amedora.busticketsrl.androidserialportapi.sample.SerialPortActivity;


import hdx.HdxUtil;

/**
 * Created by USER on 2/16/2016.
 */

public class TestPrintActivity extends SerialPortActivity {

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
                    m_pDialog = new ProgressDialog(TestPrintActivity.this);
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
    public void onCreate(Bundle savedInstatnceState){
        super.onCreate(savedInstatnceState);
        setContentView(R.layout.testprint);
        btnTest = (Button) findViewById(R.id.btnTest);
        handler = new MyHandler();
        HdxUtil.SwitchSerialFunction(HdxUtil.SERIAL_FUNCTION_PRINTER);
        PowerManager pm = (PowerManager) getApplicationContext().getSystemService(Context.POWER_SERVICE);
        lock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK, TAG);

        tvTest =(TextView) findViewById(R.id.tvTest);
      btnTest.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                handler.sendMessage(handler.obtainMessage(DISABLE_BUTTON, 1, 0, null));
                new WriteThread(0).start();
            }
        });
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
        Log.i(TAG,"flow_begin ");

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
        // / sleep(1);
    }

    private void sendCharacterDemo() {

        Log.e("TAG", "#########sendCharacterDemo##########");//,0x1B,0x23,0x46
        sendCommand(0x1B, 0x23, 0x23, 0x53, 0x4C, 0x41, 0x4E, 0x0e); // taiwan

        try {
            mOutputStream.write("english test testing SLRTC Bus Ticketing Project".getBytes());
            sendCommand(0x0a);
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }


        sendCommand(0x0a);

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

    protected void onDestroy() {
        super.onDestroy();
        stop = true;
        //PrinterPowerOff();
        Log.e(TAG, "onDestroy");

    }
}
