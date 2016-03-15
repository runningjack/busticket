package com.busticket.amedora.busticketsrl.model;

/**
 * Created by Amedora on 12/2/2015.
 */
public class Ticket {
    int id;
    public String scode;
    public String serial_no;
    public int terminal_id;
    public int route_id;
    public long ticket_id;
    public String batch_code;
    public String ticket_type;
    public double amount;
    public String created_at;
    public String updated_at;
    public int status;

    public Ticket(){

    }

    @Override
    public String toString() {
        return new StringBuilder()
                .append("{\"id\":\""+this.getTicket_id()+"\",")
                .append("\"code\":\"" + this.getScode() + "\",")
                .append("\"serial_no\":\""+this.getSerial_no()+"\",")
                .append("\"terminal_id\":\""+this.getTerminal_id()+"\",")
                .append("\"route_id\":\""+this.getRoute_id()+"\",")
                .append("\"ticket_type\":\""+this.getTicket_type()+"\",")
                .append("\"amount\":\"" + this.getAmount() + "\",")
                .append("\"status\":\"" + this.getStatus() + "\",")
                .append("\"stack_id\":\"" + this.getBatch_code() + "\"")
                .append("}").toString();
    }

    public boolean isValid() {
        return batch_code != null && serial_no != null && scode != null;
    }

    public Ticket(String scode, String serial_no, int terminal_id, int route_id, String batch_code, String ticket_type, double amount, long ticket_id, int status){
        this.scode          =   scode;
        this.serial_no      =   serial_no;
        this.terminal_id    =   terminal_id;
        this.route_id       =   route_id;
        this.batch_code     =   batch_code;
        this.ticket_type    =   ticket_type;
        this.amount         =   amount;
        this.ticket_id      =   ticket_id;
        this.status         =   status;
    }

    public void setId(int id ){this.id = id;}
    public void setTicket_id(long ticket_id){this.ticket_id = ticket_id;}
    public void setScode(String scode){this.scode = scode;}
    public void setSerial_no(String serial_no){this.serial_no = serial_no;}
    public void setTerminal_id(int terminal_id){this.terminal_id = terminal_id;}
    public void setRoute_id(int route_id){this.route_id = route_id;}
    public void setBatch_code(String batch_code){this.batch_code = batch_code;}
    public void setTicket_type(String ticket_type){this.ticket_type = ticket_type;}

    public void setStatus(int status) {
        this.status = status;
    }

    public void setAmount(double amount){this.amount = amount;}
    public void setCreated_at(String created_at){this.created_at = created_at;}

    public int getId(){return this.id;}

    public long getTicket_id() {
        return ticket_id;
    }

    public int getTerminal_id(){return this.terminal_id;}

    public int getRoute_id() {
        return route_id;
    }

    public String getScode(){return this.scode;}
    public String getSerial_no(){return this.serial_no;}
    public String getBatch_code(){return this.batch_code;}
    public String getTicket_type(){return this.ticket_type;}
    public String getCreated_at(){return this.created_at;}
    public String getUpdated_at(){return this.updated_at;}

    public int getStatus() {
        return status;
    }

    public double getAmount(){return this.amount;}


}
