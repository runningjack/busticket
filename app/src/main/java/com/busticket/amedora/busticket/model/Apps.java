package com.busticket.amedora.busticket.model;

/**
 * Created by Amedora on 7/16/2015.
 */
public class Apps {
    int id;
    String app_id;
    int status;
    String updated_at;
    String created_at;

    public Apps(){}
    public Apps(String app_id, int status){
        this.app_id = app_id;
        this.status = status;
    }

    public Apps(String app_id, int status, String updated_at, String created_at){
        this.app_id = app_id;
        this.status = status;
        this.created_at = created_at;
        this.updated_at = updated_at;
    }

    public int getId(){
        return this.id;
    }

    public String getApp_id(){
        return this.app_id;
    }

    public int getStatus(){
        return status;
    }

    public String getUpdated_at(){
        return this.updated_at;
    }

    public String getCreated_at(){
        return this.created_at;
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
}
