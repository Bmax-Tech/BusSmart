package com.bb.hp_pc.bussmartv3;

/**
 * Created by HP-PC on 4/23/2016.
 */
public class dbTableMessages {
    int msg_id;
    int to_msg;
    int from_id;
    String msg_text;
    int read_status;
    String date_time;
    String to_name;

    public dbTableMessages(){};

    public dbTableMessages(int p1,int p2,String p3){
        this.to_msg = p1;
        this.from_id = p2;
        this.msg_text = p3;
    }

    public dbTableMessages(int id,int p1,int p2,String p3,int p4,String p5,String p6){
        this.msg_id = id;
        this.to_msg = p1;
        this.from_id = p2;
        this.msg_text = p3;
        this.read_status  = p4;
        this.date_time = p5;
        this.to_name = p6;
    }

    // Setters
    public void setMsg_id(int id){
        this.msg_id = id;
    }
    public void setTo_id(int id){
        this.to_msg = id;
    }
    public void setFrom_id(int id){
        this.from_id = id;
    }
    public void setMsg_text(String msg){
        this.msg_text = msg;
    }
    public void setRead_status(int st){
        this.read_status = st;
    }
    public void setDate_time(String dt){
        this.date_time = dt;
    }
    public void setTo_Name(String nm){ this.to_name = nm; }

    // Getters
    public int getMsg_id(){
        return this.msg_id;
    }
    public int getTo_id(){
        return this.to_msg;
    }
    public int getFrom_id(){
        return this.from_id;
    }
    public String getMsg_text(){
        return this.msg_text;
    }
    public int getRead_status(){
        return this.read_status;
    }
    public String getDate_time(){
        return this.date_time;
    }
    public String getTo_name(){ return this.to_name; }
}
