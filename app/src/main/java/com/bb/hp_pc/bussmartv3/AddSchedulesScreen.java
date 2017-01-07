package com.bb.hp_pc.bussmartv3;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class AddSchedulesScreen extends AppCompatActivity implements View.OnClickListener,AdapterView.OnItemSelectedListener{

    Spinner bus_type;
    String selected_bus_type,time_pick_type;
    Button time_pick_ok_btn,add_schedule_submit_btn;
    LinearLayout time_picker_box;
    EditText bus_departure_time,bus_reach_time;
    TimePicker time_picker;
    ProgressDialog progress;
    EditText bus_number,bus_driver,bus_source,bus_destination;
    User user = new User();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_schedules_screen);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        Initialize();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                try {
                    Class my_class = Class.forName("com.bb.hp_pc.bussmartv3.MainActivity");
                    Intent my_intent = new Intent(AddSchedulesScreen.this, my_class);
                    startActivity(my_intent);
                    overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
                    finish();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                break;
        }
        return true;
    }

    //***************************

    public void Initialize(){
        bus_type = (Spinner) findViewById(R.id.bus_type);

        List<String> list = new ArrayList<>();
        list.add("AC");
        list.add("Standard");
        list.add("Semi Luxury");
        list.add("Luxury");

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        bus_type.setAdapter(dataAdapter);

        bus_type.setOnItemSelectedListener(this);

        time_picker = (TimePicker) findViewById(R.id.time_picker);
        time_picker_box = (LinearLayout) findViewById(R.id.time_picker_box);
        time_pick_ok_btn = (Button) findViewById(R.id.time_pick_ok_btn);
        time_pick_ok_btn.setOnClickListener(this);
        bus_departure_time = (EditText) findViewById(R.id.bus_departure_time);
        bus_departure_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                time_pick_type="DEP";
                time_picker_box.setVisibility(View.VISIBLE);
            }
        });
        bus_reach_time = (EditText) findViewById(R.id.bus_reach_time);
        bus_reach_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                time_pick_type="REACH";
                time_picker_box.setVisibility(View.VISIBLE);
            }
        });

        add_schedule_submit_btn = (Button) findViewById(R.id.add_schedule_submit_btn);
        add_schedule_submit_btn.setOnClickListener(this);

        // EditFields
        bus_number = (EditText) findViewById(R.id.bus_number);
        bus_driver = (EditText) findViewById(R.id.bus_driver);
        bus_source = (EditText) findViewById(R.id.bus_source);
        bus_destination = (EditText) findViewById(R.id.bus_destination);
    }

    /*
        Button Click Events
     */
    public void onClick(View v){
        switch (v.getId()){
            case (R.id.time_pick_ok_btn):
                time_picker_box.setVisibility(View.INVISIBLE);
                String time_s = into_two(time_picker.getCurrentHour())+":"+into_two(time_picker.getCurrentMinute())+" "+AM_PM(time_picker.getCurrentHour());
                if(time_pick_type.equals("DEP")){
                    bus_departure_time.setText(time_s);
                }else{
                    bus_reach_time.setText(time_s);
                }
                //Log.d("TimePick = ",time_picker.getCurrentHour().toString());
                break;
            case (R.id.add_schedule_submit_btn):
                if(validate_submition()){
                    progress = ProgressDialog.show(this, "", "Add Schedule...");
                    new Json_class().execute("http://php-eayurveda.rhcloud.com/redirect.php?add_schedule=YES&bus_route_num=" + user.get_user_bus_route() + "&bus_number=" + bus_number.getText().toString().replaceAll(" ", "+") + "&bus_type=" + bus_type.getSelectedItem().toString().replaceAll(" ", "+") + "&bus_driver=" + bus_driver.getText().toString().replaceAll(" ", "+") + "&bus_source=" + bus_source.getText().toString().replaceAll(" ", "+")+ "&bus_destination=" + bus_destination.getText().toString().replaceAll(" ", "+") + "&bus_dep_time=" + bus_departure_time.getText().toString().replaceAll(" ", "+")+"&bus_reach_time=" + bus_reach_time.getText().toString().replaceAll(" ", "+"));
                }
                break;
        }
    }

    public String AM_PM(int hours){
        String AM_PM="";
        if(hours > 12){
            AM_PM="PM";
        }else{
            AM_PM="AM";
        }
        return AM_PM;
    }

    public String into_two(int p){
        String res="";
        if(p<10){
            res = "0"+p;
        }else{
            res = ""+p;
        }
        return res;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // On selecting a spinner item
        switch (view.getId()){
            case (R.id.bus_type):
                selected_bus_type = parent.getItemAtPosition(position).toString();
                break;
        }

    }

    public void onNothingSelected(AdapterView<?> arg0) {
    }

    @Override
    public void onBackPressed() {
        try {
            Class my_class = Class.forName("com.bb.hp_pc.bussmartv3.MainActivity");
            Intent my_intent = new Intent(AddSchedulesScreen.this, my_class);
            startActivity(my_intent);
            overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
            finish();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public boolean validate_submition(){
        if(bus_number.getText().toString().equals("") || bus_driver.getText().toString().equals("") || bus_source.getText().toString().equals("") || bus_destination.getText().toString().equals("") || bus_departure_time.getText().toString().equals("") || bus_reach_time.getText().toString().equals("")){
            if(bus_number.getText().toString().equals("")){
                Toast.makeText(getApplicationContext(), "Enter Bus Number", Toast.LENGTH_SHORT).show();
            }
            else if(bus_driver.getText().toString().equals("")){
                Toast.makeText(getApplicationContext(),"Enter Bus Driver`s Name",Toast.LENGTH_SHORT).show();
            }
            else if(bus_source.getText().toString().equals("")){
                Toast.makeText(getApplicationContext(),"Enter Bus Starting Point",Toast.LENGTH_SHORT).show();
            }
            else if(bus_destination.getText().toString().equals("")){
                Toast.makeText(getApplicationContext(),"Enter Bus Ending Point",Toast.LENGTH_SHORT).show();
            }
            else if(bus_departure_time.getText().toString().equals("")){
                Toast.makeText(getApplicationContext(),"Enter Bus Departure Time",Toast.LENGTH_SHORT).show();
            }
            else if(bus_reach_time.getText().toString().equals("")){
                Toast.makeText(getApplicationContext(),"Enter Bus Reach Time",Toast.LENGTH_SHORT).show();
            }

            return false;
        }else {
            return true;
        }
    }

    /*
        AsyncTask to Register User
        This will send User`s registration details via Get Method
     */
    public class Json_class extends AsyncTask<String,Void, String> {

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
        protected void onPostExecute(String result){
            super.onPostExecute(result);
            Log.d("Result = ", result);
            progress.dismiss();
            Toast.makeText(getApplicationContext(),"Add Schedule Successful",Toast.LENGTH_SHORT).show();

            // Reset TestFields
            bus_number.setText("");
            bus_driver.setText("");
            bus_destination.setText("");
            bus_source.setText("");
            bus_departure_time.setText("");
            bus_reach_time.setText("");
        }
    }
    /*
        End of AsyncTask
     */

    //***************************

}
