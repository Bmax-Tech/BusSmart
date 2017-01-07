package com.bb.hp_pc.bussmartv3;

/**
 * Created by HP-PC on 3/12/2016.
 */
public class User {

    private static String android_id="";
    private static String user_email="";
    private static String user_name="";
    private static int user_id=0;
    private static double Longitude =0;
    private static double Latitude=0;
    private static String r_user_name="";
    private static int r_user_id=0;
    private static String user_bus_route="";
    private static boolean r_online=false;

    public void set_android_id(String id){
        android_id = id;
    }

    public void set_user_name(String name){
        user_name = name;
    }

    public void set_user_id(int id){
        user_id = id;
    }

    public void set_user_bus_route(String route){
        user_bus_route = route;
    }

    public void set_location(double lon,double lat){
        Longitude = lon;
        Latitude  = lat;
    }

    public void set_user_email(String e){
        user_email = e;
    }
    public String get_user_email(){
        return user_email;
    }

    public String get_user_android_id(){
        return android_id;
    }

    public String get_user_name(){
        return user_name;
    }

    public Integer get_user_id(){
        return user_id;
    }

    public double get_longitude(){
        return Longitude;
    }

    public double get_latitude(){
        return Latitude;
    }

    public void set_r_user(String user_n,int user_i,boolean on_l){
        r_user_name = user_n;
        r_user_id = user_i;
        r_online = on_l;
    }

    public String get_r_user_name(){
        return r_user_name;
    }

    public int get_r_user_id(){
        return r_user_id;
    }

    public boolean get_r_online(){
        return r_online;
    }

    public String get_user_bus_route(){
        return user_bus_route;
    }

}
