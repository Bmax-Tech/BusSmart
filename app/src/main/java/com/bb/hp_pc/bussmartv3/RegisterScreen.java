package com.bb.hp_pc.bussmartv3;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
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

public class RegisterScreen extends AppCompatActivity implements View.OnClickListener,AdapterView.OnItemSelectedListener {

    Spinner spinner_1;
    Button submit_btn;
    String ph_no_1="077";
    EditText bus_route_number,name,email,ph_no_2;
    boolean new_user=false;
    String deviceId="";
    final Context context = this;
    ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_screen);
        Initialize();
    }

    private void Initialize(){
        deviceId = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);

        bus_route_number = (EditText) findViewById(R.id.bus_route_number);
        name = (EditText) findViewById(R.id.user_reg_name);
        email = (EditText) findViewById(R.id.user_reg_email);
        ph_no_2 = (EditText) findViewById(R.id.user_reg_tel_2);

        submit_btn = (Button) findViewById(R.id.reg_submit_btn);
        submit_btn.setOnClickListener(this);
        spinner_1 = (Spinner) findViewById(R.id.spinner_1);

        List<String> list = new ArrayList<>();
        list.add("077");
        list.add("071");
        list.add("072");
        list.add("075");
        list.add("078");

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_1.setAdapter(dataAdapter);

        spinner_1.setOnItemSelectedListener(this);
    }

    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.reg_submit_btn:
                if(check_reg()){
                    progress = ProgressDialog.show(this, "", "Registering...");
                    new Json_class().execute("http://php-eayurveda.rhcloud.com/redirect.php?reg_user=YES&bus_route_num="+bus_route_number.getText().toString().replaceAll("\\s","")+"&name="+name.getText().toString().replaceAll(" ", "+")+"&email="+email.getText().toString().replaceAll("\\s","")+"&tel_1="+ph_no_1+"&tel_2="+ph_no_2.getText().toString().replaceAll("\\s","")+"&android_id=" + deviceId);
                }
                break;
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // On selecting a spinner item
        ph_no_1 = parent.getItemAtPosition(position).toString();
    }

    public void onNothingSelected(AdapterView<?> arg0) {
    }

    private boolean check_reg(){
        if(bus_route_number.getText().toString().equals("")){
            Toast.makeText(this, "Enter Bus Route Number", Toast.LENGTH_LONG).show();
            return false;
        }else if(name.getText().toString().equals("")){
            Toast.makeText(this, "Enter Name", Toast.LENGTH_LONG).show();
            return false;
        }else if(email.getText().toString().equals("")){
            Toast.makeText(this, "Enter Email", Toast.LENGTH_LONG).show();
            return false;
        }else if( ph_no_2.getText().toString().equals("") || ph_no_2.getText().toString().length() != 7){
            Toast.makeText(this, "Enter Valid Phone Number", Toast.LENGTH_LONG).show();
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
            try {
                if(!result.equals("ERROR"))
                {
                    //Log.d("RESULT = ", result);
                    JSONObject js = new JSONObject(result);
                    if (js != null) {
                        progress.dismiss();
                        new_user = false;
                        User u = new User();
                        u.set_android_id(deviceId);
                        u.set_user_name(name.getText().toString());
                        u.set_user_id(Integer.parseInt(js.getJSONObject("result").getString("user_id").toString()));
                        u.set_user_bus_route(bus_route_number.getText().toString());
                        u.set_user_email(email.getText().toString());

                        //******** Start Background Service
                        startService(new Intent(getBaseContext(), LocationService.class));
                        //******** Start Background Service

                        Intent i = new Intent(getApplicationContext(),MainActivity.class);
                        startActivity(i);
                        overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
                        finish();
                    }
                }
                else
                {
                    Toast.makeText(context, "Registration Error, Please try again later ", Toast.LENGTH_LONG).show();
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
    /*
        End of AsyncTask
     */

    @Override
    protected void onPause(){
        super.onPause();
    }
}
