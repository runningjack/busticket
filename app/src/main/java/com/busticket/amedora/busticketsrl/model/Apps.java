package com.busticket.amedora.busticketsrl.model;

/**
 * Created by Amedora on 7/16/2015.
 */
public class Apps {
    int id;
    String app_id;
    int status;
    String updated_at;
    String created_at;
    String route_name;
    double balance;
    int route_id;
    int terminal_id;
    String terminal;
    String agent_id;
    String password;
    int is_logged_in;
    String agent_code;
    int busID;
    String licenceNo;
    int scheduleID;
    String driverLname;
    String driverFname;
    int driverLoggedIn;
    int driverID;
    int tripCount;
    String driverPassword;
    int appMode;

    public Apps(){}
    public Apps(String app_id, int status){
        this.app_id = app_id;

        this.status = status;
    }

    public int getId(){
        return this.id;
    }

    public String getApp_id(){
        return this.app_id;
    }

    public int getStatus(){
        return this.status;
    }

    public String getRoute_name(){return this.route_name;}

    public int getRoute_id(){ return this.route_id;}

    public int getTerminal_id(){return  this.terminal_id;}

    public String getTerminal() {
        return terminal;
    }

    public int getDriverID() {
        return driverID;
    }

    public String getUpdated_at(){
        return this.updated_at;
    }

    public String getCreated_at(){
        return this.created_at;
    }

    public double getBalance() {
        return balance;
    }

    public String getAgent_id() {
        return agent_id;
    }

    public String getPassword() {
        return password;
    }

    public int getIs_logged_in() {
        return is_logged_in;
    }

    public int getBusID() {
        return busID;
    }

    public int getDriverLoggedIn() {
        return driverLoggedIn;
    }

    public String getDriverFname() {
        return driverFname;
    }

    public String getDriverLname() {
        return driverLname;
    }

    public String getLicenceNo() {
        return licenceNo;
    }

    public int getScheduleID() {
        return scheduleID;
    }

    public String getAgent_code() {
        return agent_code;
    }

    public int getTripCount() {
        return tripCount;
    }

    public String getDriverPassword() {
        return driverPassword;
    }

    public int getAppMode() {
        return appMode;
    }

    public void setId(int id){
        this.id = id;
    }

    public void setApp_id(String app_id){
        this.app_id = app_id;
    }

    public void setStatus(int status){
        this.status = status;
    }

    public void setUpdated_at(String updated_at){
        this.updated_at = updated_at;
    }

    public void setCreated_at(String created_at){
        this.created_at = created_at;
    }

    public void setDriverLoggedIn(int driverLoggedIn) {
        this.driverLoggedIn = driverLoggedIn;
    }

    public void setRoute_name(String bank_name){ this.route_name = bank_name; }

    public void setRoute_id(int route_id){ this.route_id = route_id; }

    public void setTerminal_id(int terminal_id){ this.terminal_id = terminal_id; }

    public void setTerminal(String terminal) {
        this.terminal = terminal;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public void setAgent_id(String agent_id) {
        this.agent_id = agent_id;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setIs_logged_in(int is_logged_in) {
        this.is_logged_in = is_logged_in;
    }

    public void setAgent_code(String agent_code) {
        this.agent_code = agent_code;
    }

    public void setBusID(int busID) {
        this.busID = busID;
    }

    public void setDriverID(int driverID) {
        this.driverID = driverID;
    }
    public void setLicenceNo(String licenceNo) {
        this.licenceNo = licenceNo;
    }
    public void setScheduleID(int scheduleID) {
        this.scheduleID = scheduleID;
    }
    public void setDriverFname(String driverFname) {
        this.driverFname = driverFname;
    }
    public void setDriverLname(String driverLname) {
        this.driverLname = driverLname;
    }
    public void setTripCount(int tripCount) {
        this.tripCount = tripCount;
    }
    public void setDriverPassword(String driverPassword) {
        this.driverPassword = driverPassword;
    }

    public void setAppMode(int appMode) {
        this.appMode = appMode;
    }
}
