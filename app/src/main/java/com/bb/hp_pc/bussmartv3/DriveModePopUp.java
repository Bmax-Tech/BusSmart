package com.bb.hp_pc.bussmartv3;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by HP-PC on 5/5/2016.
 */
public class DriveModePopUp extends Activity implements View.OnClickListener{

    ImageView popUpClose;
    Spinner UsersSpinner;
    List<String> list;
    ArrayList<msg_users> user_listdata = new ArrayList<msg_users>();
    ProgressDialog progress;
    TextView UserMessage;
    Button reply_btn_1,reply_btn_2,reply_btn_3,reply_btn_4;
    String reply_text="";
    int SelectedUserID=-1,GlobPos=-1;
    User u = new User();
    Context ctx;

    @Override
    protected void onCreate(Bundle saveInstanceState){
        super.onCreate(saveInstanceState);
        setContentView(R.layout.drive_mode_pop_up);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int)(width*.8),(int)(height*0.6));

        Initialize();
    }

    public void Initialize(){

        popUpClose = (ImageView) findViewById(R.id.popUpClose);
        popUpClose.setOnClickListener(this);

        reply_btn_1 = (Button) findViewById(R.id.reply_btn_1);
        reply_btn_2 = (Button) findViewById(R.id.reply_btn_2);
        reply_btn_3 = (Button) findViewById(R.id.reply_btn_3);
        reply_btn_4 = (Button) findViewById(R.id.reply_btn_4);

        reply_btn_1.setOnClickListener(this);
        reply_btn_2.setOnClickListener(this);
        reply_btn_3.setOnClickListener(this);
        reply_btn_4.setOnClickListener(this);

        UserMessage = (TextView) findViewById(R.id.UserMessage);

        UsersSpinner = (Spinner) findViewById(R.id.UsersSpinner);
        UsersSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position != 0){
                    GlobPos = (position-1);
                    msg_users temp  = user_listdata.get((position - 1));
                    UserMessage.setText(temp.u_last_msg);
                    SelectedUserID = temp.id;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        progress = ProgressDialog.show(this, "", "Listing Users...");
        new Json_class().execute("http://php-eayurveda.rhcloud.com/redirect.php?bussmart_user_list=YES&user_id=" + u.get_user_id()+"&user_bus_route="+u.get_user_bus_route());
    }

    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.popUpClose:
                this.finish();
                break;
            case R.id.reply_btn_1:
                reply_text = "Go Faster";
                QuickReply();
                break;
            case R.id.reply_btn_2:
                reply_text = "Call Me";
                QuickReply();
                break;
            case R.id.reply_btn_3:
                reply_text = "Ok";
                QuickReply();
                break;
            case R.id.reply_btn_4:
                reply_text = "I love it";
                QuickReply();
                break;
        }
        Log.d("Reply = ",reply_text);
    }

    //****  Json_class  ************************
    public class Json_class extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... parms) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(parms[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                InputStream stream = connection.getInputStream();

                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuffer buffer = new StringBuffer();

                String line = "";
                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }
                return buffer.toString();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            //Log.d("Result = ", result);
            if(result.equals("NO_USERS")){
                progress.dismiss();
            }else{
                try {
                    JSONObject js = new JSONObject(result);

                    if (js != null) {
                        list = new ArrayList<>();
                        list.add("Select User"); // list 0 index

                        for (int i = 0; i < js.length(); i++) {
                            int u_id = Integer.parseInt(js.getJSONObject("user_" + i).getString("user_id").toString());
                            String u_name = js.getJSONObject("user_" + i).getString("name").toString();
                            String u_last_msg = "";
                            String u_last_date = "";
                            if (!(js.getJSONObject("user_" + i).getString("last_date").toString().equals("null"))) {
                                u_last_msg = js.getJSONObject("user_" + i).getString("last_msg").toString();
                                u_last_date = (js.getJSONObject("user_" + i).getString("last_date").toString()).substring(0, 10);
                            } else {
                                u_last_msg = "";
                                u_last_date = "";
                            }

                            msg_users user = new msg_users(u_id, u_name, u_last_msg, u_last_date);
                            user_listdata.add(user);
                            list.add(u_name);
                        }

                        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getApplicationContext(),R.layout.spinner_item,android.R.id.text1,list);
                        //dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        UsersSpinner.setAdapter(dataAdapter);

                        progress.dismiss();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
    //****  Json_class  ************************

    // Custom Bus Class
    public class msg_users{
        public int id;
        public String u_name;
        public String u_last_msg;
        public String u_last_date;

        public msg_users(int p1,String p2,String p3,String p4){
            id = p1;
            u_name = p2;
            u_last_msg = p3;
            u_last_date = p4;
        }
    }

    public void QuickReply(){
        if(SelectedUserID != -1) {
            msg_users temp = user_listdata.get(GlobPos);
            u.set_r_user(temp.u_name,temp.id,false);

            //******** Start Background Service
            startService(new Intent(getBaseContext(), UpdateMessage.class));
            //******** Start Background Service

            new Json_Send_Message_class().execute("http://php-eayurveda.rhcloud.com/redirect.php?send_messages=YES&user_id=" + u.get_user_id() + "&partner_id=" + SelectedUserID + "&msg=" + reply_text.replaceAll(" ", "+"));
        }else{
            Toast.makeText(getApplicationContext(),"Select User",Toast.LENGTH_SHORT).show();
        }
    }

    public void FinishApp(){
        this.finish();
    }

    /*
        AsyncTask to Get Details from Server
        Get Chat Messages
     */
    public class Json_Send_Message_class extends AsyncTask<String,Void, String> {

        @Override
        protected String doInBackground(String... parms) {
            StringBuffer buffer = new StringBuffer();
            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try{
                URL url = new URL(parms[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                InputStream stream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(stream));
                String line = "";
                while((line = reader.readLine()) != null){
                    buffer.append(line);
                }
                return buffer.toString();
            }catch(MalformedURLException e){
                e.printStackTrace();
            }catch (IOException e){
                e.printStackTrace();
            } finally {
                if (connection != null){
                    connection.disconnect();
                }
                try{
                    if(reader != null){
                        reader.close();
                    }
                }catch (IOException e){
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            FinishApp();
        }
    }
    /*
        End of AsyncTask
     */

    //****************************************************************

}
