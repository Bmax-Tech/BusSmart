package com.bb.hp_pc.bussmartv3;

import android.app.ActivityManager;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LoadingScreen extends AppCompatActivity {

    TextView loading_status;
    ProgressBar loading_bar;
    Thread t;
    boolean connected;
    int jumpTime = 0;
    boolean new_user=false,response=false;
    String deviceId="";
    double latitude=0,longitude=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Add Status Bar Color programmatically
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.colorAccent));
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading_screen);

        loading();
    }

    private void loading(){
        loading_status = (TextView) findViewById(R.id.loading_status);

        deviceId = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);

        // >>>> Check GPS <<<<
        LocationManager mlocManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);;
        boolean gps_status = mlocManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        //--------------------

        // >>>>  Check Internet Connection <<<<
        connected = false;
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED || connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED)
            connected = true;//we are connected to a network
        else
            connected = false;


        if(connected && gps_status) {
            new Json_class().execute("http://php-eayurveda.rhcloud.com/redirect.php?user_verification=YES&android_id=" + deviceId);
        } else if(!connected){
            loading_status.setText("Check Your Internet Connection");
        }else if(!gps_status){
            loading_status.setText("Enable Your GPS");
        }
    }

    /*
        AsyncTask to Get Details from Server
        Whether User is  registered or Not
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
            //Log.d("RESULT = ", result);
            try {
                if(result.equals("SIGNUP"))
                {
                    try {
                        Class my_class = Class.forName("com.bb.hp_pc.bussmartv3.RegisterScreen");
                        Intent my_intent = new Intent(LoadingScreen.this, my_class);
                        startActivity(my_intent);
                        overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                else
                {
                    //Log.d("RESULT = ",result);
                    JSONObject js = new JSONObject(result);
                    if (js != null) {
                        new_user = false;
                        User u = new User();
                        u.set_android_id(deviceId);
                        u.set_user_name(js.getJSONObject("result").getString("name").toString());
                        u.set_user_id(Integer.parseInt(js.getJSONObject("result").getString("user_id").toString()));
                        u.set_user_bus_route(js.getJSONObject("result").getString("bus_route_number").toString());
                        u.set_user_email(js.getJSONObject("result").getString("email").toString());

                        //******** Start Background Service
                        startService(new Intent(getBaseContext(), LocationService.class));

                        //********* DB **********
                        DataBaseHandler db = new DataBaseHandler(getApplicationContext());
                        /*
                         * Check Owner Table Record
                         */
                        dbTableOwner owner = db.GetOwner();
                        if(owner == null){
                            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                            Date date = new Date();
                            owner = new dbTableOwner();
                            owner.setOwner_id(Integer.parseInt(js.getJSONObject("result").getString("user_id").toString()));
                            owner.setOwner_name(js.getJSONObject("result").getString("name").toString());
                            owner.setOwner_email(js.getJSONObject("result").getString("email").toString());
                            owner.setOwner_android_id(deviceId);
                            owner.setDate_time(dateFormat.format(date));
                            db.AddOwner(owner);
                        }

                        /*
                         * On Application Load Event Check whether
                         * existing Database is available and if found
                         * Delete reset those tables through OnAppLoad (Method -> DataBaseHandler Class)
                         */
                        db.OnAppLoad();
                        //********* DB **********
                        if(isRunning(MessageService.class)){
                            Log.d("Messaging Service = ","RUNNING");
                        }else{
                            Log.d("Messaging Service = ","STARTED");
                            startService(new Intent(getBaseContext(), MessageService.class));
                        }
                        //******** Start Background Service

                        try {
                            Class my_class = Class.forName("com.bb.hp_pc.bussmartv3.MainActivity");
                            Intent my_intent = new Intent(LoadingScreen.this, my_class);
                            startActivity(my_intent);
                            overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                }
                response=true;
            }catch (Exception e){
                e.printStackTrace();
            }
        }


        /*
         * Check Service is Running or Not
         */
        public boolean isRunning(Class<?> serviceClass){
            ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
            for(ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)){
                if(serviceClass.getName().equals(service.service.getClassName())){
                    return true;
                }
            }

            return false;
        }
    }
    /*
        End of AsyncTask
     */

    @Override
    protected void onPause(){
        super.onPause();
        finish();
    }
}
