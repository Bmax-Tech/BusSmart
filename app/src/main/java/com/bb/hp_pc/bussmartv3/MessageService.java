package com.bb.hp_pc.bussmartv3;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

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
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by HP-PC on 4/23/2016.
 */
public class MessageService extends Service {

    DataBaseHandler db;
    ArrayList<chat_msg> msg_listdata = new ArrayList<chat_msg>();
    // constant
    public static final long NOTIFY_INTERVAL = 30000; // 30 Seconds
    // run on another Thread to avoid crash
    private Handler mHandler = new Handler();
    // timer handling
    private Timer mTimer = null;

    User user = new User();

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

        //*** Get USER from DB
        dbTableOwner owner = db.GetOwner();
        user.set_user_id(owner.getOwner_id());
        user.set_user_name(owner.getOwner_name());
        user.set_user_email(owner.getOwner_email());
        user.set_android_id(owner.getOwner_android_id());
        //*** Get USER from DB

        if(mTimer != null) {
            mTimer.cancel();
        } else {
            // recreate new
            mTimer = new Timer();
        }
        // schedule task
        mTimer.scheduleAtFixedRate(new LoadMessagesFromServer(), 0, NOTIFY_INTERVAL);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    class LoadMessagesFromServer extends TimerTask {

        @Override
        public void run() {
            // run on another thread
            mHandler.post(new Runnable() {

                @Override
                public void run() {
                    // Check For internet Connectivity
                    if(CheckConnection()) {
                        LoadMessages();
                    }
                }

            });
        }

        public void LoadMessages() {
            new Json_class().execute("http://php-eayurveda.rhcloud.com/redirect.php?get_no_read_msg=YES&user_id=" + user.get_user_id());
        }

    }

    // Check Internet Connection
    public boolean CheckConnection(){
        boolean connected = false;
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED || connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED)
            connected = true;//we are connected to a network
        else
            connected = false;

        return connected;
    }

    //*******************************************************
    //******  Get Messages  *********************************

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
            Log.d("Result = ", result);
            msg_listdata.clear();// Clear List
            Boolean NEW_MSG = false; // For notification

            if(!result.equals("NO_READ_MSG")) {
                try {
                    JSONObject js = new JSONObject(result);

                    if (js != null) {
                        for (int i = 0; i < js.length(); i++) {
                            int msg_id = Integer.parseInt(js.getJSONObject("msg_" + i).getString("id").toString());
                            int msg_t = Integer.parseInt(js.getJSONObject("msg_" + i).getString("msg_to").toString());
                            int msg_f = Integer.parseInt(js.getJSONObject("msg_" + i).getString("msg_from").toString());
                            String msg_text = js.getJSONObject("msg_" + i).getString("msg_text").toString();
                            int msg_read_status = Integer.parseInt(js.getJSONObject("msg_" + i).getString("read_status").toString());
                            String msg_date_time = js.getJSONObject("msg_" + i).getString("msg_date_time").toString();
                            String msg_name = js.getJSONObject("msg_"+i).getString("name").toString();

                            // Add to Notifications stack
                            dbTableMessages msg = new dbTableMessages(msg_id,msg_t, msg_f, msg_text, msg_read_status, msg_date_time,msg_name);
                            msg_listdata.add(new chat_msg(msg_f, msg_name, msg_text));

                            /*
                             * First Check if Exist from Local DB
                             */
                            if(db.SelectMessage(msg_id)){
                                Log.d("EXISTING = ","YES");
                                if(!NEW_MSG) {
                                    NEW_MSG = false;
                                }
                            }else{
                                NEW_MSG = true;
                                db.AddMessage(msg);
                            }
                        }
                    }

                    // Make Notification if NEW_MSG  = true
                    if(NEW_MSG){
                        Notification();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }else{

            }

            List<dbTableMessages> temp = db.getUnReadMessages();
            Log.d("Size TEMP = ", "" + temp.size());
        }
    }
    /*
        End of AsyncTask
     */

    public class chat_msg{
        public int from_id;
        public String msg_name;
        public String msg_text;

        public chat_msg(int p1,String p2,String p3){
            from_id=p1;
            msg_name=p2;
            msg_text=p3;
        }
    }

    //*********  Notification Service (Testing)  *************
    public void Notification(){
        NotificationCompat.InboxStyle style = new NotificationCompat.InboxStyle();
        for(int i=0;i<msg_listdata.size();i++){
            chat_msg temp = msg_listdata.get(i);
            style.addLine(temp.msg_name+" - "+temp.msg_text);
        }
        style.setBigContentTitle(msg_listdata.size()+" new message").setSummaryText(user.get_user_name());

        Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
        mBuilder.setLargeIcon(largeIcon);
        mBuilder.setSmallIcon(R.drawable.user_image);
        mBuilder.setContentTitle(msg_listdata.size() + " new message");
        mBuilder.setStyle(style);
        mBuilder.setGroupSummary(true);
        mBuilder.setSound(Uri.parse("android.resource://" + ctx.getPackageName() + "/" + R.raw.notification_sound));

        Intent notificationIntent = new Intent(this.getApplicationContext(),LoadingScreen.class);
        PendingIntent rePendingIntent = PendingIntent.getActivity(this.getApplicationContext(),0,notificationIntent,0);
        mBuilder.setContentIntent(rePendingIntent);

        NotificationManager mNotifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mNotifyMgr.notify(001,mBuilder.build());

    }
    //********************************************************

}
