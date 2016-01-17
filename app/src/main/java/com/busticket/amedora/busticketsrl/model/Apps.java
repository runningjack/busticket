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
}
