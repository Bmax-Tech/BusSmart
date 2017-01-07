package com.bb.hp_pc.bussmartv3;

/**
 * Created by HP-PC on 4/24/2016.
 */
public class dbTableOwner {

    int owner_id;
    String owner_name;
    String owner_email;
    String owner_android_id;
    String date_time;

    public dbTableOwner(){};

    public dbTableOwner(String p1,String p2,String p3){
        this.owner_name = p1;
        this.owner_email = p2;
        this.owner_email = p3;
    }

    public dbTableOwner(int id,String p1,String p2,String p3,String p4){
        this.owner_id = id;
        this.owner_name = p1;
        this.owner_email = p2;
        this.owner_android_id = p3;
        this.date_time = p4;
    }

    // Setters
    public void setOwner_id(int id){
        this.owner_id = id;
    }
    public void setOwner_name(String name){
        this.owner_name = name;
    }
    public void setOwner_email(String email){
        this.owner_email = email;
    }
    public void setOwner_android_id(String android_id){
        this.owner_android_id = android_id;
    }
    public void setDate_time(String dt){
        this.date_time = dt;
    }

    // Getters
    public int getOwner_id(){
        return this.owner_id;
    }
    public String getOwner_name(){
        return this.owner_name;
    }
    public String getOwner_email(){
        return this.owner_email;
    }
    public String getOwner_android_id(){
        return this.owner_android_id;
    }
    public String getDate_time(){
        return this.date_time;
    }
}
