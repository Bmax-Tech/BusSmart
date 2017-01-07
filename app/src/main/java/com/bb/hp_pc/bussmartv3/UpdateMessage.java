package com.bb.hp_pc.bussmartv3;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.TimerTask;

/**
 * Created by HP-PC on 4/24/2016.
 */
public class UpdateMessage extends Service {
    DataBaseHandler db;
    User user = new User();
    Handler mHandler;
    public Context ctx;

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public void onCreate()
    {
        super.onCreate();

        db = new DataBaseHandler(getApplicationContext());

        ctx = this.getApplicationContext();

        if(db.SelectToFromMessage(user.get_user_id(),user.get_r_user_id())){
            Log.d("UpdateService = ","YES");
            // Send request to update read_status on remote DataBase
            new Json_class().execute("http://php-eayurveda.rhcloud.com/redirect.php?update_read_status=YES&user_id=" + user.get_user_id()+"&partner_id="+user.get_r_user_id());
        }else{
            //******** Stop Background Service
            stopService(new Intent(getBaseContext(), UpdateMessage.class));
            //******** Stop Background Service
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    /*
        AsyncTask to Get Details from Server
        Get Chat Messages
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
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            //Log.d("Result (UpdateMsg) = ", result);
            if(result.equals("OK")){
                // Delete messsages from local DataBase
                db.DeleteMessage(user.get_r_user_id());
            }

            //******** Stop Background Service
            stopService(new Intent(getBaseContext(), UpdateMessage.class));
            //******** Stop Background Service
        }
    }
    /*
        End of AsyncTask
     */
}
