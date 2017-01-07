package com.bb.hp_pc.bussmartv3;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by HP-PC on 4/23/2016.
 */
public class DataBaseHandler extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "BusSmartV3";

    /* Define DataBase Table Names */
    private static final String TABLE_OWNER = "owner";
    private static final String TABLE_MESSAGES = "messages";

    /* Define Each Table Columns Names */

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Table => owner
    private static final String KEY_ID_OWN = "own_id";
    private static final String KEY_NAME_OWN = "own_name";
    private static final String KEY_EMAIL_OWN = "email_own";
    private static final String KEY_ANDROID_ID_OWN = "android_id_own";
    private static final String KEY_TIME_OWN = "date_time";

    /* Define DataBase Table Creation Commands */
    private static final String CREATE_TABLE_OWNER = "CREATE TABLE "+TABLE_OWNER+" ("+KEY_ID_OWN+" INTEGER PRIMARY KEY,"+KEY_NAME_OWN+" TEXT,"+KEY_EMAIL_OWN+" TEXT,"+KEY_ANDROID_ID_OWN+" TEXT"+KEY_TIME_OWN+" DATETIME)";
    // Table => messages
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Table => messages
    private static final String KEY_ID_MSG = "msg_id";
    private static final String KEY_TO_MSG = "to_id";
    private static final String KEY_FROM_MSG = "from_id";
    private static final String KEY_TEXT_MSG = "message";
    private static final String KEY_READ_STATUS_MSG = "read_status";
    private static final String KEY_TIME_MSG = "date_time";
    private static final String KEY_TO_MSG_NAME = "to_name";

    /* Define DataBase Table Creation Commands */
    private static final String CREATE_TABLE_MESSAGES = "CREATE TABLE "+TABLE_MESSAGES+" ("+KEY_ID_MSG+" INTEGER PRIMARY KEY,"+KEY_TO_MSG+" INTEGER,"+KEY_FROM_MSG+" INTEGER,"+KEY_TEXT_MSG+" TEXT,"+KEY_READ_STATUS_MSG+" INTEGER,"+KEY_TIME_MSG+" DATETIME,"+KEY_TO_MSG_NAME+" TEXT)";
    // Table => messages
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    /*
     * Constant Methods
     */
    public DataBaseHandler(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /* Create DataBase Tables */
    @Override
    public void onCreate(SQLiteDatabase db){
        // Create Tables on DB
        db.execSQL(CREATE_TABLE_MESSAGES);
        db.execSQL(CREATE_TABLE_OWNER);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db,int oldVersion,int newVirsion){
        // On upgrade drop older table
        // => Define all tables Drop statements here
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_MESSAGES);
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_OWNER);

        // Create new tables
        onCreate(db);
    }

    public void OnAppLoad(){
        SQLiteDatabase db = this.getWritableDatabase();
        // => Define all tables Drop statements here
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_MESSAGES);

        // Create new tables
        db.execSQL(CREATE_TABLE_MESSAGES);
    }
    /*
     * Constant Methods
     */

    /*
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     * Define Tables Specific Methods
     */

    /*
     *  Owner ------------------------
     */

    // Insert Record into Owner Table
    public void AddOwner(dbTableOwner db_table){
        Log.d("OWNER = ","CREATED");
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_ID_OWN,db_table.getOwner_id());
        values.put(KEY_NAME_OWN,db_table.getOwner_name());
        values.put(KEY_EMAIL_OWN,db_table.getOwner_email());
        values.put(KEY_ANDROID_ID_OWN,db_table.getOwner_android_id());

        // insert Row
        db.insert(TABLE_OWNER,null,values);
        db.close();
    }

    // Read Record from Owner Table
    public dbTableOwner GetOwner(){
        dbTableOwner owner = new dbTableOwner();
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_OWNER, null);
            if (cursor.moveToFirst()) {
                do {
                    owner.setOwner_id(Integer.parseInt(cursor.getString(0)));
                    owner.setOwner_name(cursor.getString(1));
                    owner.setOwner_email(cursor.getString(2));
                    owner.setOwner_android_id(cursor.getString(3));
                } while (cursor.moveToNext());

                db.close();
                return owner;
            }
        }catch (SQLiteException e){
            db.close();
            if (e.getMessage().toString().contains("no such table")){
                return null;
            }
        }
        return null;
    }


    /*
     *  Messages ------------------------
     */

    // Insert Row into Messages Table
    public void AddMessage(dbTableMessages db_table){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_ID_MSG,db_table.getMsg_id());
        values.put(KEY_TO_MSG,db_table.getTo_id());
        values.put(KEY_FROM_MSG,db_table.getFrom_id());
        values.put(KEY_TEXT_MSG,db_table.getMsg_text());
        values.put(KEY_READ_STATUS_MSG,db_table.getRead_status());
        values.put(KEY_TIME_MSG,db_table.getDate_time());
        values.put(KEY_TO_MSG_NAME,db_table.getTo_name());

        // insert Row
        db.insert(TABLE_MESSAGES,null,values);
        db.close();
    }

    // Delete Row from Messages Table
    public void DeleteMessage(int from_id){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_MESSAGES, KEY_FROM_MSG + " = ?", new String[]{String.valueOf(from_id)});
        db.close();
    }

    // Get UnRead Messages From Messages Table
    public List<dbTableMessages> getUnReadMessages(){
        List<dbTableMessages> msgList = new ArrayList<dbTableMessages>();
        // Query
        String SQL = "SELECT * FROM "+TABLE_MESSAGES+" WHERE "+KEY_READ_STATUS_MSG+"=0 ORDER BY "+KEY_TIME_MSG+" DESC";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(SQL,null);

        if(cursor.moveToFirst()){
            do{
                dbTableMessages temp = new dbTableMessages();
                temp.setMsg_id(Integer.parseInt(cursor.getString(0)));
                temp.setTo_id(Integer.parseInt(cursor.getString(1)));
                temp.setFrom_id(Integer.parseInt(cursor.getString(2)));
                temp.setMsg_text(cursor.getString(3));
                temp.setRead_status(Integer.parseInt(cursor.getString(4)));
                temp.setDate_time(cursor.getString(5));
                temp.setTo_Name(cursor.getString(6));

                msgList.add(temp);
            }while (cursor.moveToNext());
        }
        db.close();
        return msgList;
    }

    // Get UnRead Messages for User From Messages Table
    public List<dbTableMessages> getUserUnReadMessages(int user_id){
        List<dbTableMessages> msgList = new ArrayList<dbTableMessages>();
        // Query
        String SQL = "SELECT * FROM "+TABLE_MESSAGES+" WHERE "+KEY_FROM_MSG+"="+user_id+" AND "+KEY_READ_STATUS_MSG+"=0";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(SQL,null);

        if(cursor.moveToFirst()){
            do{
                dbTableMessages temp = new dbTableMessages();
                temp.setMsg_id(Integer.parseInt(cursor.getString(0)));
                temp.setTo_id(Integer.parseInt(cursor.getString(1)));
                temp.setFrom_id(Integer.parseInt(cursor.getString(2)));
                temp.setMsg_text(cursor.getString(3));
                temp.setRead_status(Integer.parseInt(cursor.getString(4)));
                temp.setDate_time(cursor.getString(5));
                temp.setTo_Name(cursor.getString(6));

                msgList.add(temp);
            }while (cursor.moveToNext());
        }
        db.close();
        return msgList;
    }

    // Update ReadStatus of Message
    public void UpdateReadStatus(List<dbTableMessages> list){
        SQLiteDatabase db = this.getWritableDatabase();

        for (Iterator<dbTableMessages> i = list.iterator();i.hasNext();) {
            dbTableMessages temp = i.next();
            ContentValues values = new ContentValues();
            values.put(KEY_READ_STATUS_MSG,1);

            db.update(TABLE_MESSAGES,values,KEY_ID_MSG +" = ?",new String[]{String.valueOf(temp.getMsg_id())});
        }

        db.close();
    }

    // Select from Messages Table
    public boolean SelectMessage(int msg_id){
        boolean av;
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery("SELECT "+KEY_ID_MSG+" FROM "+TABLE_MESSAGES+" WHERE "+KEY_ID_MSG+" = "+msg_id,null);
        if(cursor.moveToFirst()){
            av = true;
        }else{
            av = false;
        }
        db.close();
        return av;
    }

    // Select (TO) and (FROM) from Messages Table
    public boolean SelectToFromMessage(int to_id,int from_id){
        boolean av;
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery("SELECT "+KEY_ID_MSG+" FROM "+TABLE_MESSAGES+" WHERE "+KEY_TO_MSG+" = "+to_id+" AND "+KEY_FROM_MSG+" = "+from_id,null);
        if(cursor.moveToFirst()){
            Log.d("SELECT FROM TO = ","TRUE");
            av = true;
        }else{
            Log.d("SELECT FROM TO = ","FALSE");
            av = false;
        }
        db.close();
        return av;
    }

    /*
     * Define Tables Specific Methods
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     */

}
