package com.busticket.amedora.busticketsrl.model;

/**
 * Created by Amedora on 12/2/2015.
 */

public class Ticketing {
    public int id;
    public long ticketing_id;
    public String tripe;
    public String board_stage;
    public String highlight_stage;
    public double fare;
    public String serial_no;
    public String scode;
    public String qr_code;
    public String bus_no;
    public double qty;
    public String route;
    public String driver;
    public String conductor;
    public int status;

    public Ticketing(){

    }

    public void setId(int id){this.id = id;}

    public void setTicketing_id(long ticketing_id) {
        this.ticketing_id = ticketing_id;
    }

    public void setTripe(String tripe){ this.tripe = tripe;}
    public void setBoard_stage(String board_stage){ this.board_stage = board_stage;}
    public void setHighlight_stage(String highlight_stage){this.highlight_stage = highlight_stage;}
    public void setFare(double fare){this.fare = fare;}
    public void setSerial_no(String serial_no){this.serial_no = serial_no;}
    public void setScode(String scode){this.scode = scode;}
    public void setQr_code(String qr_code){this.qr_code = qr_code;}
    public void setBus_no(String bus_no){this.bus_no = bus_no;}
    public void setQty(double qty){this.qty = qty;}
    public void setRoute(String route){this.route = route;}
    public void setDriver(String driver){this.driver = driver;}
    public void setConductor(String conductor){this.conductor = conductor;}

    public void setStatus(int status) {
        this.status = status;
    }

    public int getId(){return this.id;}

    public long getTicketing_id() {
        return ticketing_id;
    }

    public String getTripe(){return this.tripe;}
    public String getBoard_stage(){return this.board_stage;}
    public String getHighlight_stage(){return this.highlight_stage;}
    public String getSerial_no(){return this.serial_no;}
    public String getScode(){return this.scode;}
    public String getQr_code(){return this.qr_code;}
    public String getBus_no(){ return this.bus_no;}
    public String getRoute(){return this.route;}
    public String getDriver(){return this.driver;}
    public String getConductor(){ return this.conductor;}

    public double getQty() {
        return qty;
    }

    public double getFare() {
        return fare;
    }

    public int getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return new StringBuilder()
                .append("{\"id\":\"" + this.getId() + "\",")
                .append("\"scode\":\"" + this.getScode() + "\",")
                .append("\"serial_no\":\""+this.getSerial_no()+"\",")
                .append("\"trip\":\""+this.getTripe()+"\",")
                .append("\"ticket_id\":\"" + this.getTicketing_id() + "\",")
                .append("\"highlight_stage\":\""+this.getHighlight_stage() + "\",")
                .append("\"board_stage\":\"" + this.getBoard_stage() + "\",")
                .append("\"amount\":\"" + this.getFare() + "\",")
                .append("\"bus_plate_no\":\"" + this.getBus_no() + "\",")
                .append("\"qty\":\"" + this.getQty() + "\",")
                .append("\"status\":\"" + this.getStatus() + "\",")
                .append("\"route_name\":\"" + this.getRoute() + "\",")
                .append("\"driver\":\"" + this.getDriver() + "\",")
                .append("\"conductor\":\"" + this.getConductor() + "\"")
                .append("}").toString();
    }
}
