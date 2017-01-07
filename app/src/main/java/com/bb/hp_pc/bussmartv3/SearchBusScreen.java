package com.bb.hp_pc.bussmartv3;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
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
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class SearchBusScreen extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,View.OnClickListener {

    ArrayList<bus> bus_listdata = new ArrayList<bus>();
    ProgressDialog progress;
    ListView list;
    LinearLayout search_box;
    ImageView search_btn;
    EditText current_loc,destination_loc;
    TextView user_email,user_name;
    Calendar c;
    User user = new User();

    //~~~~~~~~~~~~~
    private TabLayout tabLayout;
    private ViewPager viewPager;
    //~~~~~~~~~~~~~

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_bus_screen);
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
        Initialize();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            try {
                Class my_class = Class.forName("com.bb.hp_pc.bussmartv3.MainActivity");
                Intent my_intent = new Intent(SearchBusScreen.this, my_class);
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
        getMenuInflater().inflate(R.menu.search_bus_screen, menu);
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
                Intent my_intent = new Intent(SearchBusScreen.this, my_class);
                startActivity(my_intent);
                overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
                finish();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        } else if (id == R.id.nav_drive_mode) {
            try {
                Class my_class = Class.forName("com.bb.hp_pc.bussmartv3.DriveModeScreen");
                Intent my_intent = new Intent(SearchBusScreen.this, my_class);
                startActivity(my_intent);
                overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                finish();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        } else if (id == R.id.nav_search_bus) {

        } else if (id == R.id.nav_map_view) {
            try {
                Class my_class = Class.forName("com.bb.hp_pc.bussmartv3.MapViewScreen");
                Intent my_intent = new Intent(SearchBusScreen.this, my_class);
                startActivity(my_intent);
                overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                finish();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        } else if (id == R.id.nav_messages) {
            try {
                Class my_class = Class.forName("com.bb.hp_pc.bussmartv3.MessagesScreen");
                Intent my_intent = new Intent(SearchBusScreen.this, my_class);
                startActivity(my_intent);
                overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                finish();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        } else if (id == R.id.nav_add_schedule) {
            try {
                Class my_class = Class.forName("com.bb.hp_pc.bussmartv3.AddSchedulesScreen");
                Intent my_intent = new Intent(SearchBusScreen.this, my_class);
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

    //**************************************
    public void Initialize(){
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
    }

    //~~~~~~~~~~~~~~~~~~ TABS
    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new SearchFragmentOne(), "Today Schedules");
        adapter.addFragment(new SearchFragmentTwo(), "All Schedules");
        viewPager.setAdapter(adapter);
    }
    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
    //~~~~~~~~~~~~~~~~~~ TABS

    private void InitializeItems(){
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View navView = navigationView.getHeaderView(0);
        user_name = (TextView) navView.findViewById(R.id.user_name);
        user_email = (TextView) navView.findViewById(R.id.user_email);

        user_name.setText(user.get_user_name());
        user_email.setText(user.get_user_email());
    }

    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.search_btn:
                ViewGroup.LayoutParams params = search_box.getLayoutParams();
                params.height = 260;
                search_box.setLayoutParams(params);
                break;
        }
    }

    @Override
    protected void onPause(){
        super.onPause();
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

            try {
                JSONObject js = new JSONObject(result);

                if (js != null) {
                    for (int i = 0; i < js.length(); i++) {
                        int id = Integer.parseInt(js.getJSONObject("bus_" + i).getString("id").toString());
                        String b_num = js.getJSONObject("bus_" + i).getString("bus_number").toString();
                        String b_type = js.getJSONObject("bus_" + i).getString("bus_type").toString();
                        String b_source = js.getJSONObject("bus_" + i).getString("source").toString();
                        String b_destination = js.getJSONObject("bus_" + i).getString("destination").toString();
                        String b_departure = js.getJSONObject("bus_" + i).getString("departure").toString();
                        String b_reach = js.getJSONObject("bus_" + i).getString("reach").toString();
                        String b_driver = js.getJSONObject("bus_" + i).getString("driver_name").toString();

                        bus b = new bus(id, b_num, b_type, b_source, b_destination, b_departure, b_reach, b_driver);
                        bus_listdata.add(b);
                    }
                }
                list.setAdapter(new MyCustomBaseAdapter(getApplicationContext(), bus_listdata));
                progress.dismiss();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    //****  Json_class  ************************

    /*
     *  Custom Array Adapter
     */
    public class MyCustomBaseAdapter extends BaseAdapter {
        private ArrayList<bus> searchArrayList;

        private LayoutInflater mInflater;

        public MyCustomBaseAdapter(Context context, ArrayList<bus> results) {
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
                convertView = mInflater.inflate(R.layout.search_result_row_one, null);
                holder = new ViewHolder();

                holder.bus_number = (TextView) convertView.findViewById(R.id.bus_number);
                holder.bus_type = (TextView) convertView.findViewById(R.id.bus_type);
                holder.bus_departure_time = (TextView) convertView.findViewById(R.id.departure_time);
                holder.bus_reach_time = (TextView) convertView.findViewById(R.id.reach_time);
                holder.bus_time_rem = (TextView) convertView.findViewById(R.id.time_rem);
                holder.bus_driver = (TextView) convertView.findViewById(R.id.driver_name);
                holder.layout_result = (LinearLayout) convertView.findViewById(R.id.result_row);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            bus temp_b = searchArrayList.get(position);

            holder.bus_number.setText(temp_b.bus_no);
            holder.bus_type.setText(temp_b.bus_type);
            holder.bus_driver.setText(temp_b.bus_driver);
            holder.bus_departure_time.setText(temp_b.bus_departure);
            holder.bus_reach_time.setText(temp_b.bus_reach);
            holder.bus_time_rem.setText(get_time_diff(temp_b.bus_departure));

            if(temp_b.bus_type.equals("AC")){
                holder.bus_number.setTextColor(Color.rgb(5, 66, 177));
                holder.layout_result.setBackgroundColor(Color.rgb(5,66,177));
            }else{
                holder.bus_number.setTextColor(Color.rgb(5, 177, 76));
                holder.layout_result.setBackgroundColor(Color.rgb(5, 177, 76));
            }

            return convertView;
        }

        public class ViewHolder {
            TextView bus_number,bus_type,bus_departure_time,bus_reach_time,bus_time_rem,bus_driver;
            LinearLayout layout_result;
        }
    }
    /*
     *  Custom Array Adapter
     */

    /* Time Ranger */
    private String get_time_diff(String dep){
        c = Calendar.getInstance();
        int hours = c.get(Calendar.HOUR_OF_DAY);
        int minutes = c.get(Calendar.MINUTE);
        int seconds = c.get(Calendar.SECOND);
        String hr="",min="",sec="";
        if (hours < 10) {
            hr = "0" + hours;
        } else {
            hr = "" + hours;
        }
        if (minutes < 10) {
            min = "0" + minutes;
        } else {
            min = "" + minutes;
        }
        if(seconds < 10){
            sec = "0"+seconds;
        }else{
            sec = ""+seconds;
        }
        String[] time2 = dep.split(" ");
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
        long diffMinutes=0,diffHours=0;
        try {
            Date date1 = format.parse(hr + ":" + min + ":" + sec);
            Date date2 = format.parse(time2[0]+":00");
            diffMinutes = (date2.getTime() - date1.getTime()) / (60 * 1000) % 60;
            diffHours = (date2.getTime() - date1.getTime()) / (60 * 60 * 1000) % 24;

        }catch (ParseException e){
            e.printStackTrace();
        }

        return Long.toString(diffMinutes + (diffHours * 60));
    }
    /* Time Ranger */

    // Custom Bus Class
    public class bus{
        public int id;
        public String bus_no;
        public String bus_type;
        public String bus_source;
        public String bus_destination;
        public String bus_departure;
        public String bus_reach;
        public String bus_driver;

        public bus(int p1,String p2,String p3,String p4,String p5,String p6,String p7,String p8){
            id = p1;
            bus_no = p2;
            bus_type = p3;
            bus_source = p4;
            bus_destination = p5;
            bus_departure = p6;
            bus_reach = p7;
            bus_driver = p8;
        }
    }
    //**************************************
}
