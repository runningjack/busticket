package com.busticket.amedora.busticket.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.busticket.amedora.busticket.model.Account;
import com.busticket.amedora.busticket.model.Apps;
import com.busticket.amedora.busticket.model.Bank;
import com.busticket.amedora.busticket.model.Bus;
import com.busticket.amedora.busticket.model.Route;
import com.busticket.amedora.busticket.model.Terminal;
import com.busticket.amedora.busticket.model.Ticket;
import com.busticket.amedora.busticket.model.Ticketing;
import com.busticket.amedora.busticket.model.Transaction;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Amedora on 7/16/2015.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    // Logcat tag
    private static final String LOG = "DatabaseHelper";

    // Database Version
    private static final int DATABASE_VERSION = 2;

    // Database Name
    private static final String DATABASE_NAME = "busticketapp";

    // Table name
    private static final String TABLE_ACCOUNT       = "accounts";
    private static final String TABLE_APP           = "apps";
    private static final String TABLE_BANKS         = "banks";
    private static final String TABLE_TRANS         = "transactions";
    private static final String TABLE_TICKET        = "ticket";
    private static final String TABLE_TICKETING     = "ticketing";
    private static final String TABLE_TERMINALS     = "terminals";
    private static final String TABLE_BUSES         = "buses";
    private static final String TABLE_ROUTE         = "route";

    //Generic Fields
    private static final String KEY_ID = "id";
    private static final String KEY_CREATED_AT ="created_at";
    private static final String KEY_UPDATED_AT ="updated_at";

    //fields for account
    private static final String KEY_BANK_ID ="bank_id";
    private static final String KEY_BANK_NAME ="bank";
    private static final String KEY_ACCOUNT_NO ="account_no";
    private static final String KEY_ACCOUNT_APP_ID ="app_id";
    //private static final String KEY_BANK_SORT_CODE ="bank_sort_code";

    //Fields for banks
    private static final String KEY_BANK ="bank_name";
    private static final String KEY_BANK_SHORT_NAME = "short_name";
    private static final String KEY_BANK_CODE ="sort_code";
    private static final String KEY_BANK_ADDRESS = "address";

    //fields for app
    /*route id is linked from ticket id field in ticket table field*/
    private static final String KEY_APP_ID = "app_id";
    private static final String KEY_APP_AGENT_ID = "agent_id";
    private static final String KEY_APP_ROUTE_NAME = "route_name";
    private static final String KEY_APP_TERMINAL_NAME = "station_name";
    private static final String KEY_APP_TERMINAL_ID = "station_id";
    private static final String KEY_APP_BALANCE ="balance";
    private static final String KEY_APP_STATUS="status";


    //fields for transaction
    private static final String KEY_TRANS_STATUS = "trans_status";
    private static final String KEY_TRANS_ID ="trans_id";
    private static final String KEY_TRANS_AMOUNT = "trans_amount";
    private static final String KEY_TRANS_TYPE = "trans_type";
    private static final String KEY_TRANS_ACCOUNT = "account_no";
    private static final String KEY_TRANS_NARRATION ="narration";

    //fields for tickets
    private static final String KEY_TICKET_ID = "ticket_id";
    private static final String KEY_TICKET_SCODE = "ticket_scode";
    private static final String KEY_TICKET_SERIAL_NO   =   "ticket_serial_no";
    private static final String KEY_TICKET_TERMINAL_ID = "terminal_id";
    private static final String KEY_TICKET_ROUTE_ID               =   "route_id";
    private static final String KEY_TICKET_BATCH_CODE           ="batch_code";
    private static final String KEY_TICKET_TICKET_TYPE  ="ticket_type";
    private static final String KEY_TICKET_AMOUNT ="amount" ;
    private static final String KEY_TICKET_STATUS ="status";


    //fields for ticketing
    private static final String KEY_TICKETING_ID = "ticketing_id";
    private static final String KEY_TICKETING_TRIPE = "tripe";
    private static final String KEY_TICKETING_BOARD = "board_stage";
    private static final String KEY_TICKETING_HIGHLIGHT = "highlight_stage";
    private static final String KEY_TICKETING_FARE    ="fare";
    private static final String KEY_TICKETING_SERIAL_NO ="serial_no";
    private static final String KEY_TICKETING_SCODE ="scode";
    private static final String KEY_TICKETING_QR_CODE ="qr_code";
    private static final String KEY_TICKETING_BUS_NO ="bus_no";
    private static final String KEY_TICKETING_QTY ="qty";
    private static final String KEY_TICKETING_ROUTE ="route";
    private static final String KEY_TICKETING_DRIVER ="driver";
    private static final String KEY_TICKETING_CONDUCTOR ="conductor";

    //fields for terminal
    private static final String KEY_TERMINAL_ID = "terminal_id" ;
    private static final String KEY_TERMINAL_SHORT_NAME = "short_name";
    private static final String KEY_TERMINAL_ROUTE_ID =  "route_id";
    private static final String KEY_TERMINAL_NAME = "name";
    private static final String KEY_TERMINAL_DESCRIPTION ="description";
    private static final String KEY_TERMINAL_GEODATA = "geodata";
    private static final String KEY_TERMINAL_DISTANCE ="distance";
    private static final String KEY_TERMINAL_TO_FARE ="to_fare";
    private static final String KEY_TERMINAL_FROM_FARE ="one_way_from_fare";

    //fields for route
    private static final String KEY_ROUTE_ID ="route_id";
    private static final String KEY_ROUTE_SHORT_NAME ="short_name";
    private static final String KEY_ROUTE_NAME ="name";
    private static final String KEY_ROUTE_DESCRIPTION ="description";
    private static final String KEY_ROUTE_DISTANCE ="distance";

    //Field for buses
    private static final String KEY_BUSES_ROUTE_ID = "route_id";
    private static final String KEY_BUSES_BUS_ID = "bus_id";
    private static final String KEY_BUSES_BUS_PLATE_NO = "plate_no";
    private static final String KEY_BUSES_BUS_DRIVER = "driver";
    private static final String KEY_BUSES_CONDUCTOR = "conductor";

    //string to create database tables
    private static final String CREATE_TABLE_ACCOUNT = " CREATE TABLE "+ TABLE_ACCOUNT +"("+KEY_ID+ " INTEGER PRIMARY KEY,"
            + KEY_BANK_ID +" TEXT, "+KEY_BANK_CODE+" TEXT,"+KEY_BANK_NAME+ " TEXT, "+ KEY_ACCOUNT_NO+" INTEGER,"+ KEY_ACCOUNT_APP_ID+" TEXT,"
            +KEY_CREATED_AT +" DATETIME,"+KEY_UPDATED_AT+ " DATETIME)";

    private static final String CREATE_TABLE_ROUTE = " CREATE TABLE "+ TABLE_ROUTE +"("+KEY_ID+ " INTEGER PRIMARY KEY,"
            + KEY_ROUTE_ID +" INTEGER, "+KEY_ROUTE_SHORT_NAME+" TEXT,"+KEY_ROUTE_NAME+ " TEXT, "+ KEY_ROUTE_DESCRIPTION+" TEXT,"
            +KEY_ROUTE_DISTANCE+" TEXT,"+KEY_CREATED_AT +" DATETIME,"+KEY_UPDATED_AT+ " DATETIME)";

    private static final String CREATE_TABLE_APP = " CREATE TABLE "+TABLE_APP +"("+KEY_ID+" INTEGER PRIMARY KEY,"
            +KEY_ROUTE_ID+" INTEGER,"+KEY_APP_ID+" TEXT,"+KEY_APP_AGENT_ID+" TEXT,"+KEY_APP_TERMINAL_ID+" INTEGER,"
            +KEY_APP_ROUTE_NAME+" TEXT,"+KEY_APP_TERMINAL_NAME+ " TEXT, "+KEY_APP_BALANCE+" REAL,"+KEY_APP_STATUS+" INTEGER DEFAULT 0, "+KEY_CREATED_AT+" DATETIME,"+KEY_UPDATED_AT+" DATETIME"+ ")";

    private static final String CREATE_TABLE_BANK = " CREATE TABLE "+TABLE_BANKS + "("+KEY_ID+" INTERGET PRIMARY KEY,"
            +KEY_BANK_SHORT_NAME+ " TEXT, "+KEY_BANK +" TEXT, "+KEY_BANK_CODE+ " TEXT,"+KEY_BANK_ADDRESS+" TEXT,"
            +KEY_CREATED_AT +" DATETIME,"+KEY_UPDATED_AT+ " DATETIME"+")";

    private static final String CREATE_TABLE_TRANSACTION = " CREATE TABLE "+ TABLE_TRANS + "("+KEY_ID+" INTEGER PRIMARY KEY,"+KEY_APP_ID+" TEXT,"
            +KEY_TRANS_ID+" TEXT,"+KEY_TRANS_ACCOUNT +" TEXT,"+ KEY_TRANS_AMOUNT +" TEXT, "+ KEY_TRANS_NARRATION +" TEXT,"+KEY_TRANS_TYPE+" TEXT,"+KEY_TRANS_STATUS+" INTEGER, "
            +KEY_CREATED_AT+" DATETIME,"+KEY_UPDATED_AT+" DATETIME)";

    private static final String CREATE_TABLE_TICKET = " CREATE TABLE "+ TABLE_TICKET+ "("+KEY_ID+" INTEGER PRIMARY KEY,"+KEY_TICKET_ID+" INTEGER,"+KEY_TICKET_SCODE+" TEXT,"+KEY_TICKET_SERIAL_NO+" TEXT,"
            +KEY_TICKET_TERMINAL_ID+" INTEGER,"+KEY_TICKET_ROUTE_ID+" INTEGER,"+KEY_TICKET_BATCH_CODE+" TEXT,"+KEY_TICKET_TICKET_TYPE+" TEXT,"+KEY_TICKET_AMOUNT+" INTEGER,"+KEY_TICKET_STATUS+" INTEGER,"+KEY_CREATED_AT+" DATETIME,"+KEY_UPDATED_AT+" DATETIME)";

    private static final String CREATE_TABLE_TICKETING = " CREATE TABLE "+ TABLE_TICKETING+ "("+KEY_ID+" INTEGER PRIMARY KEY,"+KEY_TICKETING_ID+" INTEGER,"+KEY_TICKETING_TRIPE+" TEXT,"+KEY_TICKETING_BOARD+" TEXT,"
            +KEY_TICKETING_HIGHLIGHT+" TEXT,"+KEY_TICKETING_FARE+" INTEGER,"+KEY_TICKETING_SERIAL_NO+" TEXT,"+KEY_TICKETING_SCODE+" TEXT,"+KEY_TICKETING_QR_CODE+" TEXT,"
            +KEY_TICKETING_BUS_NO+" TEXT,"+KEY_TICKETING_QTY+" INTEGER,"+KEY_TICKETING_ROUTE+" TEXT,"+KEY_TICKETING_DRIVER+" TEXT,"+KEY_TICKETING_CONDUCTOR+" TEXT,"+KEY_TICKET_STATUS+" INTEGER,"+KEY_CREATED_AT+" DATETIME,"+KEY_UPDATED_AT+" DATETIME)";

    private static final String CREATE_TABLE_TERMINAL = " CREATE TABLE "+ TABLE_TERMINALS+ "("+KEY_ID+" INTEGER PRIMARY KEY,"+KEY_TERMINAL_ID+" INTEGER,"+KEY_TERMINAL_SHORT_NAME+" TEXT,"+KEY_TERMINAL_ROUTE_ID+" INTEGER,"
            +KEY_TERMINAL_NAME+" TEXT,"+KEY_TERMINAL_DESCRIPTION+" TEXT,"+KEY_TERMINAL_GEODATA+" TEXT,"+KEY_TERMINAL_DISTANCE+" TEXT,"+ KEY_TERMINAL_TO_FARE+" REAL,"+ KEY_TERMINAL_FROM_FARE+" REAL,"
            +KEY_CREATED_AT+" DATETIME,"+KEY_UPDATED_AT+" DATETIME)";

    private static final String CREATE_TABLE_BUSES = " CREATE TABLE "+ TABLE_BUSES+ "("+KEY_ID+" INTEGER PRIMARY KEY,"+KEY_BUSES_ROUTE_ID+" INTEGER,"+KEY_BUSES_BUS_ID+" INTEGER,"+KEY_BUSES_BUS_PLATE_NO+ " TEXT,"+KEY_BUSES_BUS_DRIVER+ " TEXT,"
            +KEY_BUSES_CONDUCTOR+" TEXT,"+KEY_CREATED_AT+" DATETIME,"+KEY_UPDATED_AT+" DATETIME)";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_ACCOUNT);
        db.execSQL(CREATE_TABLE_APP);
        db.execSQL(CREATE_TABLE_BANK);
        db.execSQL(CREATE_TABLE_TRANSACTION);
        db.execSQL(CREATE_TABLE_BUSES);
        db.execSQL(CREATE_TABLE_TERMINAL);
        db.execSQL(CREATE_TABLE_TICKETING);
        db.execSQL(CREATE_TABLE_TICKET);
        db.execSQL(CREATE_TABLE_ROUTE);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        db.execSQL("DROP TABLE IF EXISTS " + CREATE_TABLE_ACCOUNT);
        db.execSQL("DROP TABLE IF EXISTS " + CREATE_TABLE_APP);
        db.execSQL("DROP TABLE IF EXISTS " + CREATE_TABLE_BANK);
        db.execSQL("DROP TABLE IF EXISTS " + CREATE_TABLE_TRANSACTION);
        db.execSQL("DROP TABLE IF EXISTS " + CREATE_TABLE_BUSES);
        db.execSQL("DROP TABLE IF EXISTS " + CREATE_TABLE_TERMINAL);
        db.execSQL("DROP TABLE IF EXISTS " + CREATE_TABLE_TICKET);
        db.execSQL("DROP TABLE IF EXISTS " + CREATE_TABLE_TICKETING);
        db.execSQL("DROP TABLE IF EXISTS " + CREATE_TABLE_ROUTE);
        onCreate(db);
    }




    public long createRoute(Route route){
        SQLiteDatabase dbCRoute = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_ROUTE_ID,route.getRoute_id());
        values.put(KEY_ROUTE_SHORT_NAME,route.getShort_name());
        values.put(KEY_ROUTE_NAME,route.getName());
        values.put(KEY_ROUTE_DESCRIPTION,route.getDescription());
        values.put(KEY_ROUTE_DISTANCE,route.getDistance());
        values.put(KEY_CREATED_AT,getDateTime());
        values.put(KEY_UPDATED_AT,getDateTime());
        long acc_id = dbCRoute.insert(TABLE_ROUTE, null, values);

        dbCRoute.close();
        return acc_id;
    }

    /**
     * Updating an Route
     */
    public int updateRoute(Route route) {
        SQLiteDatabase dbuRoute = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_ROUTE_ID,route.getRoute_id());
        values.put(KEY_ROUTE_SHORT_NAME,route.getShort_name());
        values.put(KEY_ROUTE_NAME,route.getName());
        values.put(KEY_ROUTE_DESCRIPTION,route.getDescription());
        values.put(KEY_ROUTE_DISTANCE,route.getDistance());
        values.put(KEY_CREATED_AT,getDateTime());
        values.put(KEY_UPDATED_AT,getDateTime());
        // updating row
        int k = dbuRoute.update(TABLE_ROUTE, values, KEY_ID + " = ?",
                new String[] { String.valueOf(route.getId()) });

        dbuRoute.close();
        return k;

    }

    public void deleteRoute(Route account) {
        SQLiteDatabase dbdRoute = this.getWritableDatabase();
        dbdRoute.delete(TABLE_ROUTE, KEY_ID + " = ?",
                new String[] { String.valueOf(account.getId()) });
        dbdRoute.close();
    }

    public List<Route> getAllRoute() {
        List<Route> accountList = new ArrayList<Route>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_ROUTE;
        SQLiteDatabase dbAllRoute = this.getWritableDatabase();
        Cursor cursor = dbAllRoute.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {

                Route contact = new Route();
                contact.setId(cursor.getInt(cursor.getColumnIndex(KEY_ID)));
                contact.setRoute_id(cursor.getInt(cursor.getColumnIndex(KEY_ROUTE_ID)));
                contact.setName(cursor.getString(cursor.getColumnIndex(KEY_ROUTE_NAME)));
                contact.setShort_name(cursor.getString(cursor.getColumnIndex(KEY_ROUTE_SHORT_NAME)));
                contact.setDescription(cursor.getString(cursor.getColumnIndex(KEY_ROUTE_DESCRIPTION)));
                //contact.setDis(cursor.getString(cursor.getColumnIndex(KEY_ROUTE_DESCRIPTION)));
                // Adding contact to list
                accountList.add(contact);
            } while (cursor.moveToNext());
        }
        cursor.close();
        dbAllRoute.close();
        return accountList;
    }

    public Route getRouteByName(String routeName){

        Route route = new Route();
        SQLiteDatabase dbgRouteByName = this.getReadableDatabase();
        String selectQuery = "SELECT * FROM "+TABLE_ROUTE+" WHERE "
                + KEY_ROUTE_SHORT_NAME +" ='"+routeName+"'";
        Cursor cursor = dbgRouteByName.rawQuery(selectQuery,null);
        if(cursor.getCount() > 0) {
            cursor.moveToFirst();
            route.setId(cursor.getInt(cursor.getColumnIndex(KEY_ID)));
            route.setRoute_id(cursor.getInt(cursor.getColumnIndex(KEY_ROUTE_ID)));
            route.setRoute_id(cursor.getInt(cursor.getColumnIndex(KEY_ROUTE_ID)));
            route.setName(cursor.getString(cursor.getColumnIndex(KEY_ROUTE_NAME)));
            route.setShort_name(cursor.getString(cursor.getColumnIndex(KEY_ROUTE_SHORT_NAME)));
            route.setDescription(cursor.getString(cursor.getColumnIndex(KEY_ROUTE_DESCRIPTION)));
        }
        cursor.close();
        dbgRouteByName.close();
        return route;
    }

    public boolean ifExistsRoute(Route account){
        SQLiteDatabase dbiffAcc = this.getReadableDatabase();
        String selectQuery = "SELECT * FROM "+TABLE_ROUTE+" WHERE "
                +KEY_ROUTE_SHORT_NAME + " ='" +account.getShort_name()+"'";
        Cursor cursor = dbiffAcc.rawQuery(selectQuery,null);

        if(cursor.moveToFirst()) {
            cursor.close();
            dbiffAcc.close();
            return true;
        }else{
            cursor.close();
            dbiffAcc.close();
            return false;
        }
    }


    public long createAccount(Account account){
        SQLiteDatabase dbcAcc = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_ACCOUNT_APP_ID,account.getApp_id());
        values.put(KEY_ACCOUNT_NO,account.getAccount_no());
        values.put(KEY_BANK_CODE,account.getSort_code());
        values.put(KEY_BANK_NAME,account.getBank());
        values.put(KEY_CREATED_AT,getDateTime());
        values.put(KEY_UPDATED_AT,getDateTime());
        long acc_id = dbcAcc.insert(TABLE_ACCOUNT, null, values);

        dbcAcc.close();
        return acc_id;
    }



    /**
     * Updating an account
     */
    public long updateAccount(Account account) {
        SQLiteDatabase dbuAcc = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_ACCOUNT_NO, account.getAccount_no());
        values.put(KEY_BANK_NAME, account.getBank());
        values.put(KEY_BANK_CODE,account.getSort_code());
        values.put(KEY_BANK_ID,account.getBank_id());
        values.put(KEY_UPDATED_AT,getDateTime());
        // updating row


        long k =dbuAcc.update(TABLE_ACCOUNT, values, KEY_ID + " = ?",
                new String[] { String.valueOf(account.getId()) });
        dbuAcc.close();
        return k;
    }

    // Deleting an account
    public void deleteAccount(Account account) {
        SQLiteDatabase dbdAcc = this.getWritableDatabase();
        dbdAcc.delete(TABLE_ACCOUNT, KEY_ID + " = ?",
                new String[] { String.valueOf(account.getId()) });
        dbdAcc.close();
    }

    public boolean ifExists(Account account){
        SQLiteDatabase dbiffAcc = this.getReadableDatabase();
        String selectQuery = "SELECT * FROM "+TABLE_ACCOUNT+" WHERE "
                +KEY_BANK_NAME + " ='" +account.getBank()+"' AND "+KEY_ACCOUNT_NO+ "= "+account.getAccount_no()+"";
        Cursor cursor = dbiffAcc.rawQuery(selectQuery,null);

        if(cursor.moveToFirst()) {
            cursor.close();
            dbiffAcc.close();
            return true;
        }else{
            cursor.close();
            dbiffAcc.close();
            return false;
        }
    }

    public List<Account> getAllAccount() {
        List<Account> accountList = new ArrayList<Account>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_ACCOUNT;
        SQLiteDatabase dbgAllAcc = this.getWritableDatabase();
        Cursor cursor = dbgAllAcc.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {

                Account contact = new Account();
                contact.setId(cursor.getInt(cursor.getColumnIndex(KEY_ID)));
                contact.setBank_id(cursor.getInt(cursor.getColumnIndex(KEY_BANK_ID)));
                contact.setBank(cursor.getString(cursor.getColumnIndex(KEY_BANK_NAME)));
                contact.setAccount_no(cursor.getInt(cursor.getColumnIndex(KEY_ACCOUNT_NO)));
                contact.setApp_id(cursor.getString(cursor.getColumnIndex(KEY_ACCOUNT_APP_ID)));
                // Adding contact to list
                accountList.add(contact);
            } while (cursor.moveToNext());
        }

        // return contact list
        cursor.close();
        dbgAllAcc.close();
        return accountList;
    }


    /**
     * Bank Detail
     **/
    public long createBank(Bank bank){
        SQLiteDatabase dbcreateBank = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_BANK,bank.getBank_name());
        values.put(KEY_BANK_SHORT_NAME,bank.getShort_name());
        values.put(KEY_BANK_CODE,bank.getSort_code());
        values.put(KEY_BANK_ADDRESS,bank.getAddress());
        values.put(KEY_CREATED_AT,getDateTime());
        values.put(KEY_UPDATED_AT,getDateTime());
        long bank_id = dbcreateBank.insert(TABLE_BANKS, null, values);
        dbcreateBank.close();
        return bank_id;


    }

    /**
     * Updating an account
     */
    public int updateBank(Bank bank) {
        SQLiteDatabase dbupdateBank = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_BANK,bank.getBank_name());
        values.put(KEY_BANK_SHORT_NAME,bank.getShort_name());
        values.put(KEY_BANK_CODE,bank.getSort_code());
        values.put(KEY_BANK_ADDRESS,bank.getAddress());
        values.put(KEY_UPDATED_AT,getDateTime());
        // updating row

        int k= dbupdateBank.update(TABLE_BANKS, values, KEY_ID + " = ?",
                new String[] { String.valueOf(bank.getId()) });
        dbupdateBank.close();
        return k;

    }


    public List<Bank> getAllBank() {
        List<Bank> bankList = new ArrayList<Bank>();
        //Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_BANKS;
        SQLiteDatabase dbgetAllBank = this.getWritableDatabase();
        Cursor cursor = dbgetAllBank.rawQuery(selectQuery, null);
        //Looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Bank bank = new Bank();
                bank.setId(cursor.getInt(cursor.getColumnIndex(KEY_ID)));
                bank.setShort_name(cursor.getString(cursor.getColumnIndex(KEY_BANK_SHORT_NAME)));
                bank.setBank_name(cursor.getString(cursor.getColumnIndex(KEY_BANK)));
                bank.setSort_code(cursor.getString(cursor.getColumnIndex(KEY_BANK_CODE)));
                bank.setAddress(cursor.getString(cursor.getColumnIndex(KEY_BANK_ADDRESS)));
                //Adding contact to list
                bankList.add(bank);
            } while (cursor.moveToNext());
        }
        //Return contact list
        cursor.close();
        dbgetAllBank.close();
        return bankList;
    }



    public boolean ifExists(Bank bank){
        SQLiteDatabase dbiffBank = this.getReadableDatabase();
        String selectQuery = "SELECT * FROM "+TABLE_BANKS+" WHERE "
                +KEY_BANK + " ='" +bank.getBank_name()+"' AND "+KEY_BANK_CODE+ "= '"+bank.getSort_code()+"'";
        Cursor cursor = dbiffBank.rawQuery(selectQuery,null);

        if(cursor.moveToFirst()) {
            return true;

        }else{
            return false;

        }

    }

    public String getBankSortCode(String bankName){
        String sortCode = "";
        SQLiteDatabase dbgetBankSortCode = this.getReadableDatabase();
        String selectQuery = "SELECT * FROM "+TABLE_BANKS+" WHERE "
                + KEY_BANK_SHORT_NAME +" ='"+bankName+"'";
        Cursor cursor = dbgetBankSortCode.rawQuery(selectQuery,null);
        if(cursor.getCount() > 0) {
            cursor.moveToFirst();
            sortCode = cursor.getString(cursor.getColumnIndex(KEY_BANK_CODE));
        }
        cursor.close();
        dbgetBankSortCode.close();
        return sortCode;
    }

    public Bank getBankByName(String bankName){

        Bank bank = new Bank();
        SQLiteDatabase dbgetBankByName = this.getReadableDatabase();
        String selectQuery = "SELECT * FROM "+TABLE_BANKS+" WHERE "
                + KEY_BANK_SHORT_NAME +" ='"+bankName+"'";
        Cursor cursor = dbgetBankByName.rawQuery(selectQuery,null);
        if(cursor.getCount() > 0) {
            cursor.moveToFirst();
            bank.setId(cursor.getInt(cursor.getColumnIndex(KEY_ID)));
            bank.setShort_name(cursor.getString(cursor.getColumnIndex(KEY_BANK_SHORT_NAME)));
            bank.setBank_name(cursor.getString(cursor.getColumnIndex(KEY_BANK)));
            bank.setSort_code(cursor.getString(cursor.getColumnIndex(KEY_BANK_CODE)));
            bank.setAddress(cursor.getString(cursor.getColumnIndex(KEY_BANK_ADDRESS)));
        }
        dbgetBankByName.close();
        return bank;
    }




    //creates application data
    public long createApp(Apps app){
        SQLiteDatabase dbcreateApp = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(KEY_APP_ID,app.getApp_id());
        values.put(KEY_ROUTE_ID,app.getRoute_id());
        values.put(KEY_APP_AGENT_ID,app.getAgent_id());
        values.put(KEY_APP_TERMINAL_ID,app.getTerminal_id());
        values.put(KEY_APP_ROUTE_NAME,app.getRoute_name());
        values.put(KEY_APP_TERMINAL_NAME,app.getTerminal());
        values.put(KEY_APP_BALANCE,app.getBalance());
        values.put(KEY_APP_STATUS,app.getStatus());

        values.put(KEY_CREATED_AT,getDateTime());
        values.put(KEY_UPDATED_AT,getDateTime());
        long acc_id = dbcreateApp.insert(TABLE_APP, null, values);
        dbcreateApp.close();
        return acc_id;
    }

    /**
     * Updating application info
     */
    public int updateApp(Apps app) {
        SQLiteDatabase dbupdateApp = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_APP_ID,app.getApp_id());
        values.put(KEY_ROUTE_ID,app.getRoute_id());
        values.put(KEY_APP_AGENT_ID,app.getAgent_id());
        values.put(KEY_APP_TERMINAL_ID,app.getTerminal_id());
        values.put(KEY_APP_ROUTE_NAME,app.getRoute_name());
        values.put(KEY_APP_TERMINAL_NAME,app.getTerminal());
        values.put(KEY_APP_BALANCE,app.getBalance());
        values.put(KEY_APP_STATUS,app.getStatus());

        values.put(KEY_CREATED_AT,getDateTime());
        values.put(KEY_UPDATED_AT,getDateTime());
        // updating row
        int k = dbupdateApp.update(TABLE_APP, values, KEY_APP_ID + " = ?",
                new String[] { String.valueOf(app.getApp_id()) });
        dbupdateApp.close();
        return k;
    }


    public Apps getApp(String app_id){
        Apps apps= new Apps();
        String sortCode = "";
        SQLiteDatabase dbgetApp = this.getReadableDatabase();
        String selectQuery = "SELECT * FROM "+TABLE_APP+" WHERE "
                + KEY_APP_ID +" ='"+app_id+"'";
        Cursor cursor = dbgetApp.rawQuery(selectQuery,null);
        if(cursor != null) {
            cursor.moveToFirst();
            apps.setId(cursor.getInt(cursor.getColumnIndex(KEY_ID)));
            apps.setApp_id(cursor.getString(cursor.getColumnIndex(KEY_APP_ID)));
            apps.setRoute_id(cursor.getInt(cursor.getColumnIndex(KEY_ROUTE_ID)));
            apps.setAgent_id(cursor.getString(cursor.getColumnIndex(KEY_APP_AGENT_ID)));
            apps.setTerminal_id(cursor.getInt(cursor.getColumnIndex(KEY_APP_TERMINAL_ID)));
            apps.setRoute_name(cursor.getString(cursor.getColumnIndex(KEY_APP_ROUTE_NAME)));
            apps.setTerminal(cursor.getString(cursor.getColumnIndex(KEY_APP_TERMINAL_NAME)));
            apps.setBalance(cursor.getDouble(cursor.getColumnIndex(KEY_APP_BALANCE)));
            apps.setStatus(cursor.getInt(cursor.getColumnIndex(KEY_APP_STATUS)));
            apps.setCreated_at(cursor.getString(cursor.getColumnIndex(KEY_CREATED_AT)));
            apps.setUpdated_at(cursor.getString(cursor.getColumnIndex(KEY_UPDATED_AT)));
        }
        cursor.close();
        dbgetApp.close();
        return apps;
    }


    public void deleteApps(Apps app) {
        SQLiteDatabase dbdeleteApps = this.getWritableDatabase();
        dbdeleteApps.delete(TABLE_APP, KEY_APP_ID + " = ?",
                new String[] { String.valueOf(app.getApp_id()) });
        dbdeleteApps.close();
    }


    //Create a new transaction

    public Long createTransaction(Transaction transaction){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(KEY_APP_ID,transaction.getApp_id());
        values.put(KEY_TRANS_ACCOUNT,transaction.getAccount_no());
        values.put(KEY_TRANS_AMOUNT,transaction.getTrans_amount());
        values.put(KEY_TRANS_ID,transaction.getTransId());
        values.put(KEY_TRANS_TYPE,transaction.getTrans_type());
        values.put(KEY_TRANS_STATUS,transaction.getStatus());
        values.put(KEY_TRANS_NARRATION,transaction.getNarration());
        values.put(KEY_CREATED_AT,getDateTime());
        values.put(KEY_UPDATED_AT,getDateTime());
        Long trans_row_id = db.insert(TABLE_TRANS,null,values);


        db.close();
        return trans_row_id;
    }

    public long updateTransaction(Transaction transaction){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(KEY_APP_ID,transaction.getApp_id());
        values.put(KEY_TRANS_ACCOUNT,transaction.getAccount_no());
        values.put(KEY_TRANS_AMOUNT,transaction.getTrans_amount());
        values.put(KEY_TRANS_ID,transaction.getTransId());
        values.put(KEY_TRANS_TYPE,transaction.getTrans_type());
        values.put(KEY_TRANS_STATUS,transaction.getStatus());
        values.put(KEY_TRANS_NARRATION,transaction.getNarration());
        values.put(KEY_UPDATED_AT,getDateTime());

        // updating row
        long k = db.update(TABLE_TRANS, values, KEY_ID + " = ?",
                new String[] { String.valueOf(transaction.getId()) });

        db.close();
        return k;
    }


    // Deleting an account
    public void deleteTransaction(Transaction account) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_TRANS, KEY_ID + " = ?",
                new String[] { String.valueOf(account.getId()) });
        db.close();
    }

    public List<Transaction> getAllTransactions(){
        List<Transaction> transList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM "+ TABLE_TRANS;
        Cursor c =db.rawQuery(sql,null);

        if(c != null){
            if(c.moveToFirst()){
                do{
                    Transaction transaction = new Transaction();
                    transaction.setApp_id(c.getString(c.getColumnIndex(KEY_APP_ID)));
                    transaction.setId(c.getInt(c.getColumnIndex(KEY_ID)));
                    transaction.setStatus(c.getInt(c.getColumnIndex(KEY_TRANS_STATUS)));
                    transaction.setAccount_no(c.getString(c.getColumnIndex(KEY_TRANS_ACCOUNT)));
                    transaction.setNarration(c.getString(c.getColumnIndex(KEY_TRANS_NARRATION)));
                    transaction.setTrans_amount(c.getDouble(c.getColumnIndex(KEY_TRANS_AMOUNT)));
                    transaction.setTrans_type(c.getString(c.getColumnIndex(KEY_TRANS_TYPE)));
                    transaction.setCreated_at(c.getString(c.getColumnIndex(KEY_CREATED_AT)));
                    transaction.setUpdated_at(c.getString(c.getColumnIndex(KEY_UPDATED_AT)));
                    transList.add(transaction);
                }while (c.moveToNext());
            }
        }
        c.close();
        db.close();
        return transList;
    }

    public Transaction getTransById(String trans_id){
        Transaction transaction = new Transaction();
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM "+TABLE_TRANS+" WHERE "+KEY_TRANS_ID+ "="+trans_id;
        Cursor c = db.rawQuery(sql,null);

        if(c != null){
            c.moveToFirst();
            transaction.setApp_id(c.getString(c.getColumnIndex(KEY_APP_ID)));
            transaction.setId(c.getInt(c.getColumnIndex(KEY_ID)));
            transaction.setStatus(c.getInt(c.getColumnIndex(KEY_TRANS_STATUS)));
            transaction.setAccount_no(c.getString(c.getColumnIndex(KEY_TRANS_ACCOUNT)));
            transaction.setNarration(c.getString(c.getColumnIndex(KEY_TRANS_NARRATION)));
            transaction.setTrans_amount(c.getDouble(c.getColumnIndex(KEY_TRANS_AMOUNT)));
            transaction.setTrans_type(c.getString(c.getColumnIndex(KEY_TRANS_TYPE)));
            transaction.setCreated_at(c.getString(c.getColumnIndex(KEY_CREATED_AT)));
            transaction.setUpdated_at(c.getString(c.getColumnIndex(KEY_UPDATED_AT)));
        }
        c.close();
        db.close();
        return  transaction;
    }

    public int getLastTransId(){
        int sortCode=0;
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM "+ TABLE_TRANS+" ORDER BY "+KEY_ID+" DESC LIMIT 1";

        Cursor c =db.rawQuery(sql,null);
        if( c.getCount()>0){
            c.moveToFirst();
            sortCode = c.getInt(c.getColumnIndex(KEY_ID));
        }
        if(sortCode !=0){
            c.close();
            db.close();
            return sortCode;
        }else{
            db.close();
            return 1;
        }



    }

    /*
    * TERMINAL Database functionalitiies
    * */
    public long createTerminal(Terminal terminal){
        SQLiteDatabase dbcreateTerminal = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_TERMINAL_ID,terminal.getTerminal_id());
        values.put(KEY_TERMINAL_SHORT_NAME,terminal.getShort_name());
        values.put(KEY_TERMINAL_ROUTE_ID,terminal.getRoute_id());
        values.put(KEY_TERMINAL_NAME,terminal.getName());
        values.put(KEY_TERMINAL_DESCRIPTION,terminal.getDescription());
        values.put(KEY_TERMINAL_GEODATA,terminal.getGeodata());
        values.put(KEY_TERMINAL_DISTANCE,terminal.getDistance());
        values.put(KEY_TERMINAL_TO_FARE,terminal.getOne_way_to_fare());
        values.put(KEY_TERMINAL_FROM_FARE,terminal.getOne_way_from_fare());
        values.put(KEY_CREATED_AT,getDateTime());
        values.put(KEY_UPDATED_AT,getDateTime());
        long acc_id = dbcreateTerminal.insert(TABLE_TERMINALS, null, values);

        dbcreateTerminal.close();
        return acc_id;
    }


    public Terminal getTerminalByName(String terminalName){

        Terminal terminal = new Terminal();
        SQLiteDatabase dbgetTerminalByName = this.getReadableDatabase();
        String selectQuery = "SELECT * FROM "+TABLE_TERMINALS+" WHERE "
                + KEY_TERMINAL_SHORT_NAME +" ='"+terminalName+"'";
        Cursor cursor = dbgetTerminalByName.rawQuery(selectQuery,null);
        if(cursor.getCount() > 0) {
            cursor.moveToFirst();

            terminal.setTerminal_id(cursor.getInt(cursor.getColumnIndex(KEY_TERMINAL_ID)));
            terminal.setShort_name(cursor.getString(cursor.getColumnIndex(KEY_TERMINAL_SHORT_NAME)));
            terminal.setRoute_id(cursor.getInt(cursor.getColumnIndex(KEY_TERMINAL_ROUTE_ID)));
            terminal.setName(cursor.getString(cursor.getColumnIndex(KEY_TERMINAL_NAME)));
            terminal.setDescription(cursor.getString(cursor.getColumnIndex(KEY_TERMINAL_DESCRIPTION)));
            terminal.setDistance(cursor.getString(cursor.getColumnIndex(KEY_TERMINAL_DISTANCE)));
            terminal.setGeodata(cursor.getString(cursor.getColumnIndex(KEY_TERMINAL_GEODATA)));
            terminal.setOne_way_from_fare(cursor.getDouble(cursor.getColumnIndex(KEY_TERMINAL_FROM_FARE)));
            terminal.setOne_way_to_fare(cursor.getDouble(cursor.getColumnIndex(KEY_TERMINAL_TO_FARE)));

        }
        cursor.close();
        dbgetTerminalByName.close();
        return terminal;
    }


    public  List<Terminal> getTerminalsByRouteId(int routeId){

        List<Terminal> terminalList = new ArrayList<Terminal>();
        SQLiteDatabase getTerminalsByRouteId = this.getReadableDatabase();
        String selectQuery = "SELECT * FROM "+TABLE_TERMINALS+" WHERE "
                + KEY_TERMINAL_ROUTE_ID +" ="+routeId;
        Cursor cursor = getTerminalsByRouteId.rawQuery(selectQuery,null);
        if (cursor.moveToFirst()) {
            do {
                Terminal terminal = new Terminal();
                terminal.setId(cursor.getInt(cursor.getColumnIndex(KEY_ID)));
                terminal.setTerminal_id(cursor.getInt(cursor.getColumnIndex(KEY_TERMINAL_ID)));
                terminal.setDescription(cursor.getString(cursor.getColumnIndex(KEY_TERMINAL_DESCRIPTION)));
                terminal.setShort_name(cursor.getString(cursor.getColumnIndex(KEY_TERMINAL_SHORT_NAME)));
                terminal.setName(cursor.getString(cursor.getColumnIndex(KEY_TERMINAL_NAME)));
                terminal.setRoute_id(cursor.getInt(cursor.getColumnIndex(KEY_TERMINAL_ROUTE_ID)));
                terminal.setGeodata(cursor.getString(cursor.getColumnIndex(KEY_TERMINAL_GEODATA)));
                terminal.setDistance(cursor.getString(cursor.getColumnIndex(KEY_TERMINAL_DISTANCE)));
                terminal.setOne_way_to_fare(cursor.getDouble(cursor.getColumnIndex(KEY_TERMINAL_TO_FARE)));
                terminal.setOne_way_from_fare(cursor.getDouble(cursor.getColumnIndex(KEY_TERMINAL_FROM_FARE)));
                // Adding contact to list
                terminalList.add(terminal);
            } while (cursor.moveToNext());
        }
        // return contact list
        cursor.close();
        getTerminalsByRouteId.close();
        return terminalList;
    }

    /**
     * Updating an terminal
     */
    public int updateTerminal(Terminal terminal) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_TERMINAL_ID,terminal.getTerminal_id());
        values.put(KEY_TERMINAL_SHORT_NAME,terminal.getShort_name());
        values.put(KEY_TERMINAL_ROUTE_ID,terminal.getRoute_id());
        values.put(KEY_TERMINAL_NAME,terminal.getName());
        values.put(KEY_TERMINAL_DESCRIPTION,terminal.getDescription());
        values.put(KEY_TERMINAL_GEODATA,terminal.getGeodata());
        values.put(KEY_TERMINAL_DISTANCE,terminal.getDistance());
        values.put(KEY_TERMINAL_TO_FARE,terminal.getOne_way_to_fare());
        values.put(KEY_TERMINAL_FROM_FARE,terminal.getOne_way_from_fare());
        values.put(KEY_UPDATED_AT,getDateTime());
        // updating row
        return db.update(TABLE_TERMINALS, values, KEY_TERMINAL_ID + " = ?",
                new String[] { String.valueOf(terminal.getTerminal_id()) });
    }

    // Deleting an terminal
    public void deleteTerminal(Terminal terminal) {
        SQLiteDatabase dbdeleteTerminal = this.getWritableDatabase();
        dbdeleteTerminal.delete(TABLE_TERMINALS, KEY_TERMINAL_ID + " = ?",
                new String[] { String.valueOf(terminal.getTerminal_id()) });
        dbdeleteTerminal.close();
    }

    public boolean ifExists(Terminal terminal){
        SQLiteDatabase dbiffTerminal = this.getReadableDatabase();
        String selectQuery = "SELECT * FROM "+TABLE_TERMINALS+" WHERE "
                +KEY_TERMINAL_ID + " =" +terminal.getTerminal_id();
        Cursor cursor = dbiffTerminal.rawQuery(selectQuery,null);

        if(cursor.moveToFirst()) {
            cursor.close();
            dbiffTerminal.close();
            return true;
        }else{
            cursor.close();
            dbiffTerminal.close();
            return false;
        }
    }

    public List<Terminal> getAllTerminals() {
        List<Terminal> terminalList = new ArrayList<Terminal>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_TERMINALS;
        SQLiteDatabase dbgetAllTerminals = this.getWritableDatabase();

        Cursor cursor = dbgetAllTerminals.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Terminal terminal = new Terminal();
                terminal.setId(cursor.getInt(cursor.getColumnIndex(KEY_ID)));
                terminal.setTerminal_id(cursor.getInt(cursor.getColumnIndex(KEY_TERMINAL_ID)));
                terminal.setDescription(cursor.getString(cursor.getColumnIndex(KEY_TERMINAL_DESCRIPTION)));
                terminal.setShort_name(cursor.getString(cursor.getColumnIndex(KEY_TERMINAL_SHORT_NAME)));
                terminal.setName(cursor.getString(cursor.getColumnIndex(KEY_TERMINAL_NAME)));
                terminal.setRoute_id(cursor.getInt(cursor.getColumnIndex(KEY_TERMINAL_ROUTE_ID)));
                terminal.setGeodata(cursor.getString(cursor.getColumnIndex(KEY_TERMINAL_GEODATA)));
                terminal.setDistance(cursor.getString(cursor.getColumnIndex(KEY_TERMINAL_DISTANCE)));
                terminal.setOne_way_to_fare(cursor.getDouble(cursor.getColumnIndex(KEY_TERMINAL_TO_FARE)));
                terminal.setOne_way_from_fare(cursor.getDouble(cursor.getColumnIndex(KEY_TERMINAL_FROM_FARE)));
                // Adding contact to list
                terminalList.add(terminal);
            } while (cursor.moveToNext());
        }
        // return contact list
        cursor.close();
        dbgetAllTerminals.close();
        return terminalList;
    }

    /*
    *  Ticket functionalyties
    * */
    public long createTicket(Ticket ticket){
        SQLiteDatabase dbcreateTicket = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(KEY_TICKET_ID,ticket.getTicket_id());
        values.put(KEY_TICKET_SCODE,ticket.getScode());
        values.put(KEY_TICKET_SERIAL_NO, ticket.getSerial_no());
        values.put(KEY_TICKET_TERMINAL_ID, ticket.getTerminal_id());
        values.put(KEY_TICKET_ROUTE_ID,ticket.getRoute_id());
        values.put(KEY_TICKET_BATCH_CODE,ticket.getBatch_code());
        values.put(KEY_TICKET_TICKET_TYPE, ticket.getTicket_type());
        values.put(KEY_TICKET_AMOUNT, ticket.getAmount());
        values.put(KEY_TICKET_STATUS,0);
        values.put(KEY_CREATED_AT,getDateTime());
        values.put(KEY_UPDATED_AT,getDateTime());
        long acc_id = dbcreateTicket.insert(TABLE_TICKET, null, values);

        dbcreateTicket.close();
        return acc_id;
    }

    public Ticket getTicketBySerialNo(String serialCode){
        Ticket ticket = new Ticket();
        SQLiteDatabase dbgetTicketBySerialNo = this.getReadableDatabase();
        String query = "SELECT * FROM "+TABLE_TICKET+" WHERE "+KEY_TICKET_SERIAL_NO+" = '"+serialCode+"'";
        Cursor cursor = dbgetTicketBySerialNo.rawQuery(query,null);
        if(cursor != null){
            if(cursor.moveToFirst()){
                ticket.setId(cursor.getInt(cursor.getColumnIndex(KEY_ID)));
                ticket.setTerminal_id(cursor.getInt(cursor.getColumnIndex(KEY_TICKET_TERMINAL_ID)));
                ticket.setRoute_id(cursor.getInt(cursor.getColumnIndex(KEY_TICKET_ROUTE_ID)));
                ticket.setTicket_id(cursor.getInt(cursor.getColumnIndex(KEY_TICKET_ID)));
                ticket.setScode(cursor.getString(cursor.getColumnIndex(KEY_TICKET_SCODE)));
                ticket.setSerial_no(cursor.getString(cursor.getColumnIndex(KEY_TICKET_SERIAL_NO)));
                ticket.setBatch_code(cursor.getString(cursor.getColumnIndex(KEY_TICKET_BATCH_CODE)));
                ticket.setTicket_type(cursor.getString(cursor.getColumnIndex(KEY_TICKET_TICKET_TYPE)));
                ticket.setAmount(cursor.getInt(cursor.getColumnIndex(KEY_TICKET_AMOUNT)));
                ticket.setStatus(cursor.getInt(cursor.getColumnIndex(KEY_TICKET_STATUS)));
            }
        }
        cursor.close();
        dbgetTicketBySerialNo.close();
        return ticket;
    }

    public Ticket getUnusedTicket(){
        Ticket ticket   = new Ticket();
        SQLiteDatabase dbgetUnusedTicket = this.getReadableDatabase();
        String selectQuery = "SELECT * FROM "+TABLE_TICKET+" WHERE "+KEY_TICKET_STATUS+ "= 0";
        Cursor cursor = dbgetUnusedTicket.rawQuery(selectQuery,null);
        if(cursor != null){
            if (cursor.moveToFirst()) {
                ticket.setId(cursor.getInt(cursor.getColumnIndex(KEY_ID)));
                ticket.setTerminal_id(cursor.getInt(cursor.getColumnIndex(KEY_TICKET_TERMINAL_ID)));
                ticket.setRoute_id(cursor.getInt(cursor.getColumnIndex(KEY_TICKET_ROUTE_ID)));
                ticket.setTicket_id(cursor.getInt(cursor.getColumnIndex(KEY_TICKET_ID)));
                ticket.setScode(cursor.getString(cursor.getColumnIndex(KEY_TICKET_SCODE)));
                ticket.setSerial_no(cursor.getString(cursor.getColumnIndex(KEY_TICKET_SERIAL_NO)));
                ticket.setBatch_code(cursor.getString(cursor.getColumnIndex(KEY_TICKET_BATCH_CODE)));
                ticket.setTicket_type(cursor.getString(cursor.getColumnIndex(KEY_TICKET_TICKET_TYPE)));
                ticket.setAmount(cursor.getInt(cursor.getColumnIndex(KEY_TICKET_AMOUNT)));
                ticket.setStatus(cursor.getInt(cursor.getColumnIndex(KEY_TICKET_STATUS)));
            }
        }
        cursor.close();
        dbgetUnusedTicket.close();
        return ticket;
    }

    public List<Ticket> getUsedTickets(){
        List<Ticket> ticketList = new ArrayList<Ticket>();
        SQLiteDatabase dbgetUsedTickets = this.getReadableDatabase();
        String selectQuery = "SELECT * FROM "+TABLE_TICKET+" WHERE "+KEY_TICKET_STATUS+ "= 1";
        Cursor cursor = dbgetUsedTickets.rawQuery(selectQuery,null);
        if (cursor.moveToFirst()) {
            do {
                Ticket ticket = new Ticket();
                ticket.setId(cursor.getInt(cursor.getColumnIndex(KEY_ID)));
                ticket.setTerminal_id(cursor.getInt(cursor.getColumnIndex(KEY_TICKET_TERMINAL_ID)));
                ticket.setRoute_id(cursor.getInt(cursor.getColumnIndex(KEY_TICKET_ROUTE_ID)));
                ticket.setTicket_id(cursor.getInt(cursor.getColumnIndex(KEY_TICKET_ID)));
                ticket.setScode(cursor.getString(cursor.getColumnIndex(KEY_TICKET_SCODE)));
                ticket.setSerial_no(cursor.getString(cursor.getColumnIndex(KEY_TICKET_SERIAL_NO)));
                ticket.setBatch_code(cursor.getString(cursor.getColumnIndex(KEY_TICKET_BATCH_CODE)));
                ticket.setTicket_type(cursor.getString(cursor.getColumnIndex(KEY_TICKET_TICKET_TYPE)));
                ticket.setAmount(cursor.getInt(cursor.getColumnIndex(KEY_TICKET_AMOUNT)));
                ticket.setStatus(cursor.getInt(cursor.getColumnIndex(KEY_TICKET_STATUS)));

                ticketList.add(ticket);
            } while (cursor.moveToNext());
        }
        // return contact list

        cursor.close();
        dbgetUsedTickets.close();
        return ticketList;
    }
    /**
     * Updating an Ticket
     */
    public long updateTicket(Ticket ticket) {
        SQLiteDatabase dbupdateTicket = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_TICKET_ID,ticket.getTicket_id());
        values.put(KEY_TICKET_SCODE,ticket.getScode());
        values.put(KEY_TICKET_SERIAL_NO,ticket.getSerial_no());
        values.put(KEY_TICKET_TERMINAL_ID,ticket.getTerminal_id());
        values.put(KEY_TICKET_ROUTE_ID,ticket.getRoute_id());
        values.put(KEY_TICKET_BATCH_CODE,ticket.getBatch_code());
        values.put(KEY_TICKET_TICKET_TYPE,ticket.getTicket_type());
        values.put(KEY_TICKET_AMOUNT,ticket.getAmount());
        values.put(KEY_TICKET_STATUS,ticket.getStatus());
        values.put(KEY_UPDATED_AT,getDateTime());
        // updating row
        long k =  dbupdateTicket.update(TABLE_TICKET, values, KEY_ID + " = ?",
                new String[] { String.valueOf(ticket.getId()) });

        dbupdateTicket.close();
        return k;
    }

    // Deleting an TICKET
    public void deleteTicket(Ticket ticket) {
        SQLiteDatabase dbdeleteTicket = this.getWritableDatabase();
        dbdeleteTicket.delete(TABLE_TICKET, KEY_TICKET_ID + " = ?",
                new String[] { String.valueOf(ticket.getTicket_id()) });
        dbdeleteTicket.close();
    }

    public boolean ifExists(Ticket ticket){
        SQLiteDatabase dbiffTicket = this.getReadableDatabase();
        String selectQuery = "SELECT * FROM "+TABLE_TICKET+" WHERE "
                +KEY_TICKET_ID + " =" +ticket.getTicket_id();
        Cursor cursor = dbiffTicket.rawQuery(selectQuery,null);
        if(cursor != null) {
            if (cursor.moveToFirst()) {
                cursor.close();
                dbiffTicket.close();
                return true;
            } else {
                cursor.close();
                dbiffTicket.close();
                return false;
            }
        }else{
            cursor.close();
            dbiffTicket.close();
            return false;
        }
    }

    public List<Ticket> getAllTickets() {
        List<Ticket> ticketList = new ArrayList<Ticket>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_TICKET;
        SQLiteDatabase dbgetAllTickets = this.getWritableDatabase();
        Cursor cursor = dbgetAllTickets.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Ticket ticket = new Ticket();
                ticket.setId(cursor.getInt(cursor.getColumnIndex(KEY_ID)));
                ticket.setTerminal_id(cursor.getInt(cursor.getColumnIndex(KEY_TICKET_TERMINAL_ID)));
                ticket.setRoute_id(cursor.getInt(cursor.getColumnIndex(KEY_TICKET_ROUTE_ID)));
                ticket.setTicket_id(cursor.getInt(cursor.getColumnIndex(KEY_TICKET_ID)));
                ticket.setScode(cursor.getString(cursor.getColumnIndex(KEY_TICKET_SCODE)));
                ticket.setSerial_no(cursor.getString(cursor.getColumnIndex(KEY_TICKET_SERIAL_NO)));
                ticket.setBatch_code(cursor.getString(cursor.getColumnIndex(KEY_TICKET_BATCH_CODE)));
                ticket.setTicket_type(cursor.getString(cursor.getColumnIndex(KEY_TICKET_TICKET_TYPE)));
                ticket.setAmount(cursor.getInt(cursor.getColumnIndex(KEY_TICKET_AMOUNT)));
                ticket.setStatus(cursor.getInt(cursor.getColumnIndex(KEY_TICKET_STATUS)));
                ticketList.add(ticket);
            } while (cursor.moveToNext());
        }
        // return contact list

        cursor.close();
        dbgetAllTickets.close();
        return ticketList;
    }

    /*
    *  Ticketing database functionality
    * */
    public long createTicketing(Ticketing ticketing){
        SQLiteDatabase dbcreateTicketing = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(KEY_TICKETING_ID,ticketing.getTicketing_id());
        values.put(KEY_TICKETING_TRIPE,ticketing.getTripe());
        values.put(KEY_TICKETING_BOARD, ticketing.getBoard_stage());
        values.put(KEY_TICKETING_HIGHLIGHT, ticketing.getHighlight_stage());
        values.put(KEY_TICKETING_FARE,ticketing.getFare());
        values.put(KEY_TICKETING_SERIAL_NO,ticketing.getSerial_no());
        values.put(KEY_TICKETING_SCODE, ticketing.getScode());
        values.put(KEY_TICKETING_QR_CODE, ticketing.getQr_code());
        values.put(KEY_TICKETING_BUS_NO,ticketing.getBus_no());
        values.put(KEY_TICKETING_QTY,ticketing.getQty());
        values.put(KEY_TICKETING_ROUTE, ticketing.getRoute());
        values.put(KEY_TICKETING_DRIVER, ticketing.getDriver());
        values.put(KEY_TICKETING_CONDUCTOR, ticketing.getConductor());
        values.put(KEY_TICKET_STATUS,ticketing.getStatus());
        values.put(KEY_CREATED_AT,getDateTime());
        values.put(KEY_UPDATED_AT,getDateTime());
        long acc_id = dbcreateTicketing.insert(TABLE_TICKETING, null, values);

        dbcreateTicketing.close();
        return acc_id;
    }

    /**
     * Updating an Ticketing
     */
    public long updateTicketing(Ticketing ticketing) {
        SQLiteDatabase dbupdateTicketing = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_TICKETING_ID,ticketing.getTicketing_id());
        values.put(KEY_TICKETING_TRIPE,ticketing.getTripe());
        values.put(KEY_TICKETING_BOARD, ticketing.getBoard_stage());
        values.put(KEY_TICKETING_HIGHLIGHT, ticketing.getHighlight_stage());
        values.put(KEY_TICKETING_FARE,ticketing.getFare());
        values.put(KEY_TICKETING_SERIAL_NO,ticketing.getSerial_no());
        values.put(KEY_TICKETING_SCODE, ticketing.getScode());
        values.put(KEY_TICKETING_QR_CODE, ticketing.getQr_code());
        values.put(KEY_TICKETING_BUS_NO,ticketing.getBus_no());
        values.put(KEY_TICKETING_QTY,ticketing.getQty());
        values.put(KEY_TICKETING_ROUTE, ticketing.getRoute());
        values.put(KEY_TICKETING_DRIVER, ticketing.getDriver());
        values.put(KEY_TICKETING_CONDUCTOR, ticketing.getConductor());
        values.put(KEY_UPDATED_AT,getDateTime());
        values.put(KEY_TICKET_STATUS,ticketing.getStatus());
        // updating row
        long  k = dbupdateTicketing.update(TABLE_TICKETING, values, KEY_TERMINAL_ID + " = ?",
                new String[] { String.valueOf(ticketing.getId())});


        dbupdateTicketing.close();
        return k;
    }

    // Deleting an TICKETING
    public void deleteTicketing(Ticketing ticketing) {
        SQLiteDatabase dbdeleteTicketing = this.getWritableDatabase();
        dbdeleteTicketing.delete(TABLE_TICKETING, KEY_ID + " = ?",
                new String[] { String.valueOf(ticketing.getId()) });
        dbdeleteTicketing.close();
    }

    public boolean ifExistsTicketing(Ticketing ticketing){
        SQLiteDatabase dbiffTicketing = this.getReadableDatabase();
        String selectQuery = "SELECT * FROM "+TABLE_TICKETING+" WHERE "
                + KEY_ID + " =" +ticketing.getId();
        Cursor cursor = dbiffTicketing.rawQuery(selectQuery,null);

        if(cursor.moveToFirst()) {
            cursor.close();
            dbiffTicketing.close();
            return true;
        }else{
            cursor.close();
            dbiffTicketing.close();
            return false;
        }
    }

    public List<Ticketing> getAllTicketings() {
        List<Ticketing> ticketList = new ArrayList<Ticketing>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_TICKETING;
        SQLiteDatabase dbgetAllTicketings = this.getWritableDatabase();
        Cursor cursor = dbgetAllTicketings.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Ticketing ticketing = new Ticketing();
                ticketing.setId(cursor.getInt(cursor.getColumnIndex(KEY_ID)));
                ticketing.setTicketing_id(cursor.getInt(cursor.getColumnIndex(KEY_TICKETING_ID)));
                ticketing.setTripe(cursor.getString(cursor.getColumnIndex(KEY_TICKETING_TRIPE)));
                ticketing.setBoard_stage(cursor.getString(cursor.getColumnIndex(KEY_TICKETING_BOARD)));
                ticketing.setHighlight_stage(cursor.getString(cursor.getColumnIndex(KEY_TICKETING_HIGHLIGHT)));
                ticketing.setFare(cursor.getInt(cursor.getColumnIndex(KEY_TICKETING_FARE)));
                ticketing.setSerial_no(cursor.getString(cursor.getColumnIndex(KEY_TICKETING_SERIAL_NO)));
                ticketing.setScode(cursor.getString(cursor.getColumnIndex(KEY_TICKETING_SCODE)));
                ticketing.setQr_code(cursor.getString(cursor.getColumnIndex(KEY_TICKETING_QR_CODE)));
                ticketing.setQty(cursor.getDouble(cursor.getColumnIndex(KEY_TICKETING_QTY)));
                ticketing.setBus_no(cursor.getString(cursor.getColumnIndex(KEY_TICKETING_BUS_NO)));
                ticketing.setRoute(cursor.getString(cursor.getColumnIndex(KEY_TICKETING_ROUTE)));
                ticketing.setDriver(cursor.getString(cursor.getColumnIndex(KEY_TICKETING_DRIVER)));
                ticketing.setConductor(cursor.getString(cursor.getColumnIndex(KEY_TICKETING_CONDUCTOR)));
                ticketing.setStatus(cursor.getInt(cursor.getColumnIndex(KEY_TICKET_STATUS)));
                ticketList.add(ticketing);
            } while (cursor.moveToNext());
        }
        // return contact list
        cursor.close();
        dbgetAllTicketings.close();
        return ticketList;
    }

    public Ticketing getTicketingByTicketId(String ticketID){
        Ticketing ticketing = new Ticketing();
        SQLiteDatabase dbgetTicketingByTicketId = this.getReadableDatabase();
        String selectQuery = "SELECT * FROM "+TABLE_TICKETING+ " WHERE "+KEY_TICKETING_ID+ " = '"+ticketID+"'";
        Cursor cursor = dbgetTicketingByTicketId.rawQuery(selectQuery,null);
        if(cursor.getCount() > 0) {
            cursor.moveToFirst();
            ticketing.setId(cursor.getInt(cursor.getColumnIndex(KEY_ID)));
            ticketing.setTicketing_id(cursor.getInt(cursor.getColumnIndex(KEY_TICKETING_ID)));
            ticketing.setTripe(cursor.getString(cursor.getColumnIndex(KEY_TICKETING_TRIPE)));
            ticketing.setBoard_stage(cursor.getString(cursor.getColumnIndex(KEY_TICKETING_BOARD)));
            ticketing.setHighlight_stage(cursor.getString(cursor.getColumnIndex(KEY_TICKETING_HIGHLIGHT)));
            ticketing.setFare(cursor.getInt(cursor.getColumnIndex(KEY_TICKETING_FARE)));
            ticketing.setSerial_no(cursor.getString(cursor.getColumnIndex(KEY_TICKETING_SERIAL_NO)));
            ticketing.setScode(cursor.getString(cursor.getColumnIndex(KEY_TICKETING_SCODE)));
            ticketing.setQr_code(cursor.getString(cursor.getColumnIndex(KEY_TICKETING_QR_CODE)));
            ticketing.setQty(cursor.getDouble(cursor.getColumnIndex(KEY_TICKETING_QTY)));
            ticketing.setBus_no(cursor.getString(cursor.getColumnIndex(KEY_TICKETING_BUS_NO)));
            ticketing.setRoute(cursor.getString(cursor.getColumnIndex(KEY_TICKETING_ROUTE)));
            ticketing.setDriver(cursor.getString(cursor.getColumnIndex(KEY_TICKETING_DRIVER)));
            ticketing.setConductor(cursor.getString(cursor.getColumnIndex(KEY_TICKETING_CONDUCTOR)));
            ticketing.setStatus(cursor.getInt(cursor.getColumnIndex(KEY_TICKET_STATUS)));

        }
        cursor.close();
        dbgetTicketingByTicketId.close();
        return ticketing;
    }

    /*
    *  Ticket functionalities
    * */
    public long createBus(Bus bus){
        SQLiteDatabase dbcreateBus = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_BUSES_ROUTE_ID,bus.getRoute_id());
        values.put(KEY_BUSES_BUS_ID,bus.getBus_id());
        values.put(KEY_BUSES_BUS_PLATE_NO, bus.getPlate_no());
        values.put(KEY_BUSES_BUS_DRIVER, bus.getDriver());
        values.put(KEY_BUSES_CONDUCTOR,bus.getConductor());
        values.put(KEY_CREATED_AT,getDateTime());
        values.put(KEY_UPDATED_AT,getDateTime());
        long acc_id = dbcreateBus.insert(TABLE_BUSES, null, values);

        dbcreateBus.close();
        return acc_id;
    }

    public Bus getBusByPlateNo(String plateNo){
        Bus bus = new Bus();
        SQLiteDatabase dbgetBusByPlateNo = this.getReadableDatabase();
        String selectQuery = "SELECT * FROM "+TABLE_BUSES+ " WHERE "+KEY_BUSES_BUS_PLATE_NO+ " = '"+plateNo+"'";
        Cursor cursor = dbgetBusByPlateNo.rawQuery(selectQuery,null);
        if(cursor.getCount() > 0) {
            cursor.moveToFirst();

            bus.setDriver(cursor.getString(cursor.getColumnIndex(KEY_BUSES_BUS_DRIVER)));
            bus.setRoute_id(cursor.getInt(cursor.getColumnIndex(KEY_BUSES_ROUTE_ID)));
            bus.setConductor(cursor.getString(cursor.getColumnIndex(KEY_BUSES_CONDUCTOR)));
            bus.setPlate_no(cursor.getString(cursor.getColumnIndex(KEY_BUSES_BUS_PLATE_NO)));
            bus.setBus_id(cursor.getInt(cursor.getColumnIndex(KEY_BUSES_BUS_ID)));
        }
        cursor.close();
        dbgetBusByPlateNo.close();
        return bus;
    }

    /**
     * Updating an Ticket
     */
    public long updateBus(Bus bus) {
        SQLiteDatabase dbupdateBus = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_BUSES_ROUTE_ID,bus.getRoute_id());
        values.put(KEY_BUSES_BUS_ID,bus.getBus_id());
        values.put(KEY_BUSES_BUS_PLATE_NO, bus.getPlate_no());
        values.put(KEY_BUSES_BUS_DRIVER, bus.getDriver());
        values.put(KEY_BUSES_CONDUCTOR,bus.getConductor());
        values.put(KEY_UPDATED_AT,getDateTime());
        // updating row
        long k = dbupdateBus.update(TABLE_BUSES, values, KEY_BUSES_BUS_ID + " = ?",
                new String[] { String.valueOf(bus.getBus_id()) });

        dbupdateBus.close();
        return k;
    }

    // Deleting an Bus
    public void deleteBus(Bus bus) {
        SQLiteDatabase dbdeleteBus = this.getWritableDatabase();
        dbdeleteBus.delete(TABLE_BUSES, KEY_BUSES_BUS_ID + " = ?",
                new String[] { String.valueOf(bus.getBus_id()) });
        dbdeleteBus.close();
    }

    public boolean ifExistsBus(Bus bus){
        SQLiteDatabase dbiffBus = this.getReadableDatabase();
        String selectQuery = "SELECT * FROM "+TABLE_BUSES+" WHERE "
                +KEY_BUSES_BUS_ID + " =" +bus.getBus_id();
        Cursor cursor = dbiffBus.rawQuery(selectQuery,null);
        if(cursor.moveToFirst()) {
            cursor.close();
            dbiffBus.close();
            return true;
        }else{
            cursor.close();
            dbiffBus.close();
            return false;
        }
    }

    public List<Bus> getAllBuses() {
        List<Bus> busList = new ArrayList<Bus>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_BUSES;
        SQLiteDatabase dbgetAllBuses = this.getWritableDatabase();
        Cursor cursor = dbgetAllBuses.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Bus bus = new Bus();
                //bus.setId(cursor.getInt(cursor.getColumnIndex(KEY_ID)));
                bus.setDriver(cursor.getString(cursor.getColumnIndex(KEY_BUSES_BUS_DRIVER)));
                bus.setConductor(cursor.getString(cursor.getColumnIndex(KEY_BUSES_CONDUCTOR)));
                bus.setBus_id(cursor.getInt(cursor.getColumnIndex(KEY_BUSES_BUS_ID)));
                bus.setPlate_no(cursor.getString(cursor.getColumnIndex(KEY_BUSES_BUS_PLATE_NO)));
                bus.setRoute_id(cursor.getInt(cursor.getColumnIndex(KEY_BUSES_ROUTE_ID)));
                busList.add(bus);
            } while (cursor.moveToNext());
        }
        // return contact list
        cursor.close();
        dbgetAllBuses.close();
        return busList;
    }

    //gets the current date
    public String getDateTime(){
        String myDate;
        Date dt = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        myDate =dateFormat.format(date);
        return myDate;
    }


}
