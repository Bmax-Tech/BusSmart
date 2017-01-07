package com.bb.hp_pc.bussmartv3;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        View.OnClickListener{

    /*
     * Attributes Declaration
     */
    Button side_menu_btn;
    ImageView side_menu_close_btn;
    TextView time_box,day_1_box,day_2_box,month_box,user_name,user_email;
    ImageButton drive_mode_btn,search_bus_btn,map_view_btn,message_btn;
    LinearLayout side_menu,side_menu_in,side_menu_btn_1,side_menu_btn_2,side_menu_btn_3,side_menu_btn_4,side_menu_btn_5,side_menu_ept;
    Thread t;
    String hr="",min="",sec="";
    Boolean btn_click=false;
    Calendar c;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
        StartClock();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setCancelable(false);
            builder.setMessage("Do you want to Exit?");
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    btn_click=true;
                    t.interrupt();

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
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
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
            /*btn_click=true;
            t.interrupt();
            Intent intent = new Intent(getApplicationContext(),UpdateProfile.class);
            startActivity(intent);
            overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
            finish();*/
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

        } else if (id == R.id.nav_drive_mode) {
            try {
                btn_click=true;
                t.interrupt();
                Class my_class = Class.forName("com.bb.hp_pc.bussmartv3.DriveModeScreen");
                Intent my_intent = new Intent(MainActivity.this, my_class);
                startActivity(my_intent);
                overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                finish();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

        } else if (id == R.id.nav_search_bus) {
            try {
                btn_click=true;
                t.interrupt();
                Class my_class = Class.forName("com.bb.hp_pc.bussmartv3.SearchBusScreen");
                Intent my_intent = new Intent(MainActivity.this, my_class);
                startActivity(my_intent);
                overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                finish();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        } else if (id == R.id.nav_map_view) {
            try {
                btn_click=true;
                t.interrupt();
                Class my_class = Class.forName("com.bb.hp_pc.bussmartv3.MapViewScreen");
                Intent my_intent = new Intent(MainActivity.this, my_class);
                startActivity(my_intent);
                overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                finish();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        } else if (id == R.id.nav_messages) {
            try {
                btn_click=true;
                t.interrupt();
                Class my_class = Class.forName("com.bb.hp_pc.bussmartv3.MessagesScreen");
                Intent my_intent = new Intent(MainActivity.this, my_class);
                startActivity(my_intent);
                overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                finish();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        } else if (id == R.id.nav_add_schedule) {
            try {
                btn_click=true;
                t.interrupt();
                Class my_class = Class.forName("com.bb.hp_pc.bussmartv3.AddSchedulesScreen");
                Intent my_intent = new Intent(MainActivity.this, my_class);
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
                    btn_click=true;
                    t.interrupt();

                    //******** Stop Background Service
                    stopService(new Intent(getBaseContext(), LocationService.class));
                    stopService(new Intent(getBaseContext(), MessageService.class));
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

    public void onFragmentInteraction(Uri uri){

    }

    // -- Old Imports
    private void InitializeItems(){
        time_box = (TextView) findViewById(R.id.time_box);
        day_1_box = (TextView) findViewById(R.id.day_1_box);
        day_2_box = (TextView) findViewById(R.id.day_2_box);
        month_box = (TextView) findViewById(R.id.month_box);
        drive_mode_btn = (ImageButton) findViewById(R.id.drive_mode_btn);
        search_bus_btn = (ImageButton) findViewById(R.id.search_bus_btn);
        map_view_btn = (ImageButton) findViewById(R.id.map_view_btn);
        message_btn = (ImageButton) findViewById(R.id.message_btn);

        // Set Button Click Listeners
        drive_mode_btn.setOnClickListener(this);
        search_bus_btn.setOnClickListener(this);
        map_view_btn.setOnClickListener(this);
        message_btn.setOnClickListener(this);

        // Define
        Calendar c = Calendar.getInstance();
        day_1_box.setText(getDay(c));
        day_2_box.setText(getDate(c));
        month_box.setText(getMonth());

        user = new User();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View navView = navigationView.getHeaderView(0);
        user_name = (TextView) navView.findViewById(R.id.user_name);
        user_email = (TextView) navView.findViewById(R.id.user_email);

        user_name.setText(user.get_user_name());
        user_email.setText(user.get_user_email());
    }

    public void disable_btns(){
        drive_mode_btn.setClickable(false);
        search_bus_btn.setClickable(false);
        map_view_btn.setClickable(false);
        message_btn.setClickable(false);
    }
    public void enable_btns(){
        drive_mode_btn.setClickable(true);
        search_bus_btn.setClickable(true);
        map_view_btn.setClickable(true);
        message_btn.setClickable(true);
    }

    /*
        Button Click Events
     */
    public void onClick(View v){
        switch (v.getId()){
            case (R.id.drive_mode_btn):

                try {
                    btn_click=true;
                    t.interrupt();
                    Class my_class = Class.forName("com.bb.hp_pc.bussmartv3.DriveModeScreen");
                    Intent my_intent = new Intent(MainActivity.this, my_class);
                    startActivity(my_intent);
                    overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                    finish();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }

                break;
            case (R.id.search_bus_btn):

                try {
                    btn_click=true;
                    t.interrupt();
                    Class my_class = Class.forName("com.bb.hp_pc.bussmartv3.SearchBusScreen");
                    Intent my_intent = new Intent(MainActivity.this, my_class);
                    startActivity(my_intent);
                    overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                    finish();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }

                break;
            case (R.id.map_view_btn):

                try {
                    btn_click=true;
                    t.interrupt();
                    Class my_class = Class.forName("com.bb.hp_pc.bussmartv3.MapViewScreen");
                    Intent my_intent = new Intent(MainActivity.this, my_class);
                    startActivity(my_intent);
                    overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                    finish();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }

                break;
            case (R.id.message_btn):

                try {
                    btn_click=true;
                    t.interrupt();
                    Class my_class = Class.forName("com.bb.hp_pc.bussmartv3.MessagesScreen");
                    Intent my_intent = new Intent(MainActivity.this, my_class);
                    startActivity(my_intent);
                    overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                    finish();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }

                break;
        }
    }

    private void StartClock(){
        /* Time Box Thread */
        t = new Thread(new Runnable() {
            @Override
            public void run() {
                while (!btn_click) {
                    try {
                        c = Calendar.getInstance();
                        int hours = c.get(Calendar.HOUR_OF_DAY);
                        int minutes = c.get(Calendar.MINUTE);
                        int seconds = c.get(Calendar.SECOND);
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
                        time_box.post(new Runnable() {
                            @Override
                            public void run() {
                                time_box.setText(hr + " : " + min+" : "+sec);
                            }
                        });
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        //e.printStackTrace();
                    }
                }
            }
        });
        t.start();
        /* Time Box Thread */
    }

    /*
        This function will return current day
     */
    private String getDay(Calendar c){
        String weekDay="";
        int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);

        if (Calendar.MONDAY == dayOfWeek) weekDay = "Monday";
        else if (Calendar.TUESDAY == dayOfWeek) weekDay = "Tuesday";
        else if (Calendar.WEDNESDAY == dayOfWeek) weekDay = "Wednesday";
        else if (Calendar.THURSDAY == dayOfWeek) weekDay = "Thursday";
        else if (Calendar.FRIDAY == dayOfWeek) weekDay = "Friday";
        else if (Calendar.SATURDAY == dayOfWeek) weekDay = "Saturday";
        else if (Calendar.SUNDAY == dayOfWeek) weekDay = "Sunday";
        return weekDay;
    }

    /*
        This function will return current date
     */
    private String getDate(Calendar c){
        String date="";
        if(c.get(Calendar.DATE) < 10) {
            date = "0" + c.get(Calendar.DATE);
        }else{
            date = "" + c.get(Calendar.DATE);
        }
        return date;
    }

    /*
        This function will return current month
     */
    private String getMonth(){
        String month="";
        DateFormat dateFormat = new SimpleDateFormat("MM");
        Date date = new Date();
        final String[] month_list = new String[]{"January", "February", "March", "April", "May", "June", "July", "Augest", "September", "October", "November", "December"};
        month = month_list[Integer.parseInt(dateFormat.format(date))-1];

        return month;
    }

    /*@Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setMessage("Do you want to Exit?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                btn_click=true;
                t.interrupt();

                /*//******** Stop Background Service
                stopService(new Intent(getBaseContext(), Location_Service.class));
                /*//******** Stop Background Service

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
    }*/

    @Override
    protected void onPause(){
        super.onPause();
    }

    // -- Old Imports
}
