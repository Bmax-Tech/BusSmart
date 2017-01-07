package com.bb.hp_pc.bussmartv3;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;

public class MessagesScreen extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    ListView list;
    ArrayList<msg_users> user_listdata = new ArrayList<msg_users>();
    ImageView no_users_img;
    TextView no_users_txt,user_name,user_email;
    ProgressDialog progress;
    Calendar c;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages_screen);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        InitializeItems();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            try {
                Class my_class = Class.forName("com.bb.hp_pc.bussmartv3.MainActivity");
                Intent my_intent = new Intent(MessagesScreen.this, my_class);
                startActivity(my_intent);
                overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
                finish();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.messages_screen, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            try {
                Class my_class = Class.forName("com.bb.hp_pc.bussmartv3.MainActivity");
                Intent my_intent = new Intent(MessagesScreen.this, my_class);
                startActivity(my_intent);
                overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
                finish();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        } else if (id == R.id.nav_drive_mode) {
            try {
                Class my_class = Class.forName("com.bb.hp_pc.bussmartv3.DriveModeScreen");
                Intent my_intent = new Intent(MessagesScreen.this, my_class);
                startActivity(my_intent);
                overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                finish();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        } else if (id == R.id.nav_search_bus) {
            try {
                Class my_class = Class.forName("com.bb.hp_pc.bussmartv3.SearchBusScreen");
                Intent my_intent = new Intent(MessagesScreen.this, my_class);
                startActivity(my_intent);
                overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                finish();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        } else if (id == R.id.nav_map_view) {
            try {
                Class my_class = Class.forName("com.bb.hp_pc.bussmartv3.MapViewScreen");
                Intent my_intent = new Intent(MessagesScreen.this, my_class);
                startActivity(my_intent);
                overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                finish();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        } else if (id == R.id.nav_messages) {

        } else if (id == R.id.nav_add_schedule) {
            try {
                Class my_class = Class.forName("com.bb.hp_pc.bussmartv3.AddSchedulesScreen");
                Intent my_intent = new Intent(MessagesScreen.this, my_class);
                startActivity(my_intent);
                overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                finish();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }else if(id == R.id.nav_exit){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setCancelable(false);
            builder.setMessage("Do you want to Exit?");
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    //******** Stop Background Service
                    stopService(new Intent(getBaseContext(), LocationService.class));
                    //stopService(new Intent(getBaseContext(), MessageService.class));
                    //******** Stop Background Service

                    //if user pressed "yes", then he is allowed to exit from application
                    finish();
                    System.exit(0);
                }
            });
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //if user select "No", just cancel this dialog and continue with app
                    dialog.cancel();
                }
            });
            AlertDialog alert = builder.create();
            alert.show();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    //**********************

    private void InitializeItems(){
        user = new User();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View navView = navigationView.getHeaderView(0);
        user_name = (TextView) navView.findViewById(R.id.user_name);
        user_email = (TextView) navView.findViewById(R.id.user_email);

        user_name.setText(user.get_user_name());
        user_email.setText(user.get_user_email());

        list = (ListView) findViewById(R.id.chat_list);

        User u = new User();

        progress = ProgressDialog.show(this, "", "Listing Users...");
        new Json_class().execute("http://php-eayurveda.rhcloud.com/redirect.php?bussmart_user_list=YES&user_id=" + u.get_user_id()+"&user_bus_route="+u.get_user_bus_route());

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View container, int position, long id) {
                try {
                    User u_temp = new User();
                    msg_users msg_temp = user_listdata.get(position);
                    u_temp.set_r_user(msg_temp.u_name,msg_temp.id,false);

                    //******** Start Background Service
                    startService(new Intent(getBaseContext(), UpdateMessage.class));
                    //******** Start Background Service

                    Intent i = new Intent(getApplicationContext(), ChatScreen.class);
                    startActivity(i);
                    overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                    finish();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
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
                no_users_img.setVisibility(View.VISIBLE);
                no_users_txt.setVisibility(View.VISIBLE);
                progress.dismiss();
            }else{
                try {
                    JSONObject js = new JSONObject(result);

                    if (js != null) {
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
                        }
                        list.setAdapter(new MyCustomBaseAdapter(getApplicationContext(), user_listdata));
                        progress.dismiss();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
    //****  Json_class  ************************

    /*
     *  Custom Array Adapter
     */
    public class MyCustomBaseAdapter extends BaseAdapter {
        private ArrayList<msg_users> searchArrayList;

        private LayoutInflater mInflater;

        public MyCustomBaseAdapter(Context context, ArrayList<msg_users> results) {
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
                convertView = mInflater.inflate(R.layout.message_chat_list_row, null);
                holder = new ViewHolder();

                holder.user_name = (TextView) convertView.findViewById(R.id.user_name);
                holder.user_last_msg = (TextView) convertView.findViewById(R.id.user_last_msg);
                holder.msg_last_date = (TextView) convertView.findViewById(R.id.msg_last_date);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            msg_users temp_user = searchArrayList.get(position);

            holder.user_name.setText(temp_user.u_name);
            holder.user_last_msg.setText(temp_user.u_last_msg);
            holder.msg_last_date.setText(temp_user.u_last_date);

            return convertView;
        }

        public class ViewHolder {
            TextView user_name;
            TextView user_last_msg;
            TextView msg_last_date;
        }


    }
    /*
     *  Custom Array Adapter
     */

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

    //**********************
}
