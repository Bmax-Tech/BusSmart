package com.bb.hp_pc.bussmartv3;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
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
import java.util.Timer;
import java.util.TimerTask;

public class ChatScreen extends AppCompatActivity implements View.OnClickListener{

    ListView list;
    EditText message_text_box;
    ArrayList<chat_msg> msg_listdata = new ArrayList<chat_msg>();
    ProgressDialog progress;
    User user = new User();;
    Button send_btn;
    ImageView no_msg_img;
    TextView no_msg_txt;

    Integer unread_count=0;
    Integer last_unread_id=0; // Hold last Unread ID
    // constant
    public static final long NOTIFY_INTERVAL = 20000; // 20 Seconds
    // run on another Thread to avoid crash
    private Handler mHandler = new Handler();
    // timer handling
    private Timer mTimer = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_screen);
        setTitle(user.get_r_user_name());

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        InitializeItems();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                try {
                    if(mTimer != null) {
                        mTimer.cancel();
                        mTimer.purge();
                    }

                    Class my_class = Class.forName("com.bb.hp_pc.bussmartv3.MessagesScreen");
                    Intent my_intent = new Intent(ChatScreen.this, my_class);
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

    private void InitializeItems(){
        message_text_box = (EditText) findViewById(R.id.message_text_box);
        send_btn = (Button) findViewById(R.id.send_btn);
        send_btn.setOnClickListener(this);
        no_msg_img = (ImageView) findViewById(R.id.no_msg_img);
        no_msg_txt = (TextView) findViewById(R.id.no_msg_txt);

        list = (ListView) findViewById(R.id.chat_messages_list);

        progress = ProgressDialog.show(this,"", "Fetching Messages...");
        Log.d("R User ID = ", "" + user.get_r_user_id());
        new Json_class().execute("http://php-eayurveda.rhcloud.com/redirect.php?get_messages=YES&user_id=" + user.get_user_id() + "&partner_id=" + user.get_r_user_id());
    }

    @Override
    public void onClick(View v){
        switch (v.getId()){
            case (R.id.send_btn):
                // Send Message
                new Json_Send_Message_class().execute("http://php-eayurveda.rhcloud.com/redirect.php?send_messages=YES&user_id=" + user.get_user_id() + "&partner_id=" + user.get_r_user_id() + "&msg=" + message_text_box.getText().toString().replaceAll(" ", "+"));
                message_text_box.setText("");
                break;
        }
    }

    @Override
    public void onBackPressed() {
        try {
            if(mTimer != null) {
                mTimer.cancel();
                mTimer.purge();
            }

            Class my_class = Class.forName("com.bb.hp_pc.bussmartv3.MessagesScreen");
            Intent my_intent = new Intent(ChatScreen.this, my_class);
            startActivity(my_intent);
            overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
            finish();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause(){
        super.onPause();
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
            //Log.d("Result = ", result);
            msg_listdata.clear();
            if(!result.equals("[]")) {
                no_msg_img.setVisibility(View.INVISIBLE);
                no_msg_txt.setVisibility(View.INVISIBLE);
                try {
                    JSONObject js = new JSONObject(result);

                    if (js != null) {
                        for (int i = 0; i < js.length(); i++) {
                            last_unread_id = Integer.parseInt(js.getJSONObject("msg_" + i).getString("id").toString());
                            int msg_t = Integer.parseInt(js.getJSONObject("msg_" + i).getString("msg_to").toString());
                            int msg_f = Integer.parseInt(js.getJSONObject("msg_" + i).getString("msg_from").toString());
                            String msg_text = js.getJSONObject("msg_" + i).getString("msg_text").toString();
                            String msg_date_time = js.getJSONObject("msg_" + i).getString("msg_date_time").toString();

                            // Check Unread Count
                            int read = Integer.parseInt(js.getJSONObject("msg_" + i).getString("read_status").toString());
                            if(read == 0 && msg_t == user.get_user_id()){
                                unread_count++;
                            }
                            // Check Unread Count

                            chat_msg msg = new chat_msg(msg_t, msg_f, msg_text, msg_date_time);
                            msg_listdata.add(msg);
                        }
                    }
                    list.setAdapter(new MyCustomBaseAdapter(getApplicationContext(), msg_listdata));
                    progress.dismiss();

                    // Refresh Messages
                    if(mTimer == null) {
                        RefreshMessages();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }else{
                no_msg_img.setVisibility(View.VISIBLE);
                no_msg_txt.setVisibility(View.VISIBLE);
                progress.dismiss();
            }
        }
    }
    /*
        End of AsyncTask
     */

    public class chat_msg{
        public int to_id;
        public int from_id;
        public String msg_text;
        public String date_time;

        public chat_msg(int p1,int p2,String p3,String p4){
            to_id=p1;
            from_id=p2;
            msg_text=p3;
            date_time=p4;
        }
    }

    //****************************************************************

    //*******************************************************
    //******  Get Messages  *********************************

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
            // Reload Messages Again
            new Json_class().execute("http://php-eayurveda.rhcloud.com/redirect.php?get_messages=YES&user_id=" + user.get_user_id() + "&partner_id=" + user.get_r_user_id());
        }
    }
    /*
        End of AsyncTask
     */

    //****************************************************************



    /*
     *  Custom Array Adapter
     */
    public class MyCustomBaseAdapter extends BaseAdapter {
        private ArrayList<chat_msg> searchArrayList;

        private LayoutInflater mInflater;

        public MyCustomBaseAdapter(Context context, ArrayList<chat_msg> results) {
            searchArrayList = results;
            mInflater = LayoutInflater.from(context);
        }

        public int getCount() {
            return searchArrayList.size();
        }

        public Object getItem(int position) {
            return searchArrayList.get(position);
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.message_chat_row, null);
                holder = new ViewHolder();

                holder.icon = (ImageView) convertView.findViewById(R.id.user_icon);
                holder.lay_user = (LinearLayout) convertView.findViewById(R.id.user_lay_out);
                holder.lay_msg = (LinearLayout) convertView.findViewById(R.id.msg_lay_out);
                holder.text = (TextView) convertView.findViewById(R.id.text);
                holder.date_time = (TextView) convertView.findViewById(R.id.date_time);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            chat_msg temp_msg = searchArrayList.get(position);
            holder.text.setText(temp_msg.msg_text);

            if(temp_msg.from_id == user.get_user_id()){
                //holder.icon.setBackgroundResource(R.drawable.msg_icon_2);
                holder.lay_user.setGravity(Gravity.RIGHT);
                holder.lay_msg.setGravity(Gravity.RIGHT);
                holder.date_time.setGravity(Gravity.RIGHT);
                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)holder.text.getLayoutParams();
                params.setMargins(0, 0, 20, 0); //substitute parameters for left, top, right, bottom
                holder.text.setLayoutParams(params);
                holder.text.setBackgroundColor(Color.rgb(255, 255, 255));
            }else {
                holder.lay_user.setGravity(Gravity.LEFT);
                holder.lay_msg.setGravity(Gravity.LEFT);
                holder.date_time.setGravity(Gravity.LEFT);
                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) holder.text.getLayoutParams();
                params.setMargins(20, 0, 0, 0); //substitute parameters for left, top, right, bottom
                holder.text.setLayoutParams(params);
                holder.text.setBackgroundColor(Color.rgb(255, 209, 84));
            }

            return convertView;
        }

        public class ViewHolder {
            LinearLayout lay_user;
            LinearLayout lay_msg;
            TextView text;
            TextView date_time;
            ImageView icon;
        }


    }
    /*
     *  Custom Array Adapter
     */

    // Refresh Messages
    public void RefreshMessages(){
        if(mTimer != null) {
            mTimer.cancel();
        } else {
            // recreate new
            mTimer = new Timer();
        }
        // schedule task
        mTimer.scheduleAtFixedRate(new ReFreshMessages(), 0, NOTIFY_INTERVAL);
    }

    class ReFreshMessages extends TimerTask {
        DataBaseHandler db = new DataBaseHandler(getApplicationContext());

        @Override
        public void run() {
            // run on another thread
            mHandler.post(new Runnable() {

                @Override
                public void run() {
                    LoadMessages();
                }

            });
        }

        public void LoadMessages() {
            List<dbTableMessages> db_un_read = db.getUserUnReadMessages(user.get_r_user_id());
            Log.d("DB unread = ",""+db_un_read.size());
            Log.d("Local unread = ",""+unread_count);

            if(db_un_read.size() != unread_count){
                Log.d("UNREAD CHAT SCREEN = ", "FOUND");
                dbTableMessages last_msg = db_un_read.get((db_un_read.size()-1)); // Get Last un read message
                if(last_unread_id != last_msg.getMsg_id()) {
                    for (int i = 0; i < db_un_read.size(); i++) {
                        dbTableMessages temp = db_un_read.get(i);
                        chat_msg msg = new chat_msg(temp.getTo_id(), temp.getFrom_id(), temp.getMsg_text(), temp.getDate_time());
                        msg_listdata.add(msg);
                    }
                    list.setAdapter(new MyCustomBaseAdapter(getApplicationContext(), msg_listdata));
                    //******** Start Background Service
                    startService(new Intent(getBaseContext(), UpdateMessage.class));
                    //******** Start Background Service
                }
            }else{
                Log.d("UNREAD CHAT SCREEN = ","NOT-FOUND");
            }
        }
    }
    // Refresh Messages
}
