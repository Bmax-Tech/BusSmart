package com.bb.hp_pc.bussmartv3;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class DriveModeScreen extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,OnMapReadyCallback,View.OnClickListener {

    private GoogleMap mMap;
    double latitude=0;
    double longitude=0;
    protected LocationManager locationManager;
    SupportMapFragment mapFragment;
    TextView speed_meeter,user_name,user_email;
    EditText drive_mode_msg;

    private Handler mHandler = new Handler();
    public Context ctx;
    public static final long NOTIFY_INTERVAL = 4000; // 4 Sec
    // timer handling
    private Timer mTimer = null;
    OnMapReadyCallback mapreadycallback;
    MarkerOptions marker_temp=null;
    Boolean add_marker=false;
    User user;
    Location last_location=null;
    TextView driveMsg;
    ImageView QuickReplyBtn;

    //Zoom **********
    //Initialize to a non-valid zoom value
    private float previousZoomLevel = 17;
    private boolean isZooming = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drive_mode_screen);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ctx = this.getApplicationContext();
        Initialize();

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);


        if(mTimer != null) {
            mTimer.cancel();
        } else {
            // recreate new
            mTimer = new Timer();
        }
        // schedule task
        mTimer.scheduleAtFixedRate(new UpdateGPS(), 0, NOTIFY_INTERVAL);

        mapreadycallback = this;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            try {
                mTimer.cancel();
                mTimer.purge();

                Class my_class = Class.forName("com.bb.hp_pc.bussmartv3.MainActivity");
                Intent my_intent = new Intent(DriveModeScreen.this, my_class);
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
        getMenuInflater().inflate(R.menu.drive_mode_screen, menu);
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
                mTimer.cancel();
                mTimer.purge();

                Class my_class = Class.forName("com.bb.hp_pc.bussmartv3.MainActivity");
                Intent my_intent = new Intent(DriveModeScreen.this, my_class);
                startActivity(my_intent);
                overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
                finish();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

        } else if (id == R.id.nav_drive_mode) {

        } else if (id == R.id.nav_search_bus) {
            try {
                mTimer.cancel();
                mTimer.purge();

                Class my_class = Class.forName("com.bb.hp_pc.bussmartv3.SearchBusScreen");
                Intent my_intent = new Intent(DriveModeScreen.this, my_class);
                startActivity(my_intent);
                overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                finish();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        } else if (id == R.id.nav_map_view) {
            try {
                mTimer.cancel();
                mTimer.purge();

                Class my_class = Class.forName("com.bb.hp_pc.bussmartv3.MapViewScreen");
                Intent my_intent = new Intent(DriveModeScreen.this, my_class);
                startActivity(my_intent);
                overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                finish();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        } else if (id == R.id.nav_messages) {
            try {
                mTimer.cancel();
                mTimer.purge();

                Class my_class = Class.forName("com.bb.hp_pc.bussmartv3.MessagesScreen");
                Intent my_intent = new Intent(DriveModeScreen.this, my_class);
                startActivity(my_intent);
                overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                finish();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        } else if (id == R.id.nav_add_schedule) {
            try {
                mTimer.cancel();
                mTimer.purge();

                Class my_class = Class.forName("com.bb.hp_pc.bussmartv3.AddSchedulesScreen");
                Intent my_intent = new Intent(DriveModeScreen.this, my_class);
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

    //*********************************

    public void Initialize(){
        user = new User();

        speed_meeter = (TextView) findViewById(R.id.speed_meeter);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View navView = navigationView.getHeaderView(0);
        user_name = (TextView) navView.findViewById(R.id.user_name);
        user_email = (TextView) navView.findViewById(R.id.user_email);

        user_name.setText(user.get_user_name());
        user_email.setText(user.get_user_email());

        driveMsg = (TextView) findViewById(R.id.driveMsg);
        driveMsg.setOnClickListener(this);

        QuickReplyBtn = (ImageView) findViewById(R.id.QuickReplyBtn);
        QuickReplyBtn.setOnClickListener(this);
    }

    public void disable_btns(){
        drive_mode_msg.setClickable(false);
    }
    public void enable_btns(){
        drive_mode_msg.setClickable(true);
    }

    /*
        Button Click Events
     */
    public void onClick(View v){
        switch (v.getId()){
            case R.id.driveMsg:
                //DriveChatPopUp.setVisibility(View.VISIBLE);
                startActivity(new Intent(DriveModeScreen.this,DriveModePopUp.class));
                break;
            case R.id.QuickReplyBtn:
                //DriveChatPopUp.setVisibility(View.VISIBLE);
                startActivity(new Intent(DriveModeScreen.this,DriveModePopUp.class));
                break;
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if(!add_marker) {
            mMap.addMarker(marker_temp);
            add_marker=true;
            //mMap is an instance of GoogleMap
            mMap.setOnCameraChangeListener(getCameraChangeListener());
        }
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(my_loc));
        LatLng my_loc = new LatLng(latitude,longitude);
        // Construct a CameraPosition focusing on Mountain View and animate the camera to that position.
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(my_loc)            // Sets the center of the map to Mountain View
                .zoom(previousZoomLevel)   // Sets the zoom
                .bearing(0)                // Sets the orientation of the camera to east
                .tilt(50)                  // Sets the tilt of the camera to 30 degrees
                .build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    public GoogleMap.OnCameraChangeListener getCameraChangeListener()
    {
        return new GoogleMap.OnCameraChangeListener()
        {
            @Override
            public void onCameraChange(CameraPosition position)
            {
                //Log.d("Zoom", "Zoom: " + position.zoom);
                if(previousZoomLevel != position.zoom)
                {
                    isZooming = true;
                }
                previousZoomLevel = position.zoom;
            }
        };
    }

    class UpdateGPS extends TimerTask {

        @Override
        public void run() {
            // run on another thread
            mHandler.post(new Runnable() {

                @Override
                public void run() {
                    addLocationListener();
                }

            });
        }

        public void addLocationListener() {
            /**
             * Get Unread Messages from DB
             */
            getUnreadMessages();

            try {
                LocationListener locationListener = new MyLocationListener();
                locationManager = (LocationManager) ctx.getSystemService(Context.LOCATION_SERVICE);
                // getting GPS status
                boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
                // getting network status
                boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
                Location location=null;

                // if GPS Enabled get lat/long using GPS Services
                if (isGPSEnabled) {
                    if (location == null) {
                        Log.d("GPS Enabled", "GPS Enabled");
                        if (locationManager != null) {
                            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            if (location != null) {
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
                            }
                        }
                    }
                }
                if (isNetworkEnabled) {
                    Log.d("Network", "Network");
                    if (locationManager != null) {
                        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (location != null) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                        }
                    }
                }

                if(longitude != 0 && latitude != 0){
                    speed_meeter.setText("" + (int)get_Speed(location));
                    Log.d("Speed = ", "" + get_Speed(location));
                    // Update Server
                    //GPS_Call((float)longitude,(float)latitude);
                    LatLng my_loc = new LatLng(latitude,longitude);
                    marker_temp = new MarkerOptions().position(my_loc).title(user.get_user_name());
                    // Changing marker icon
                    marker_temp.icon(BitmapDescriptorFactory.fromResource(R.drawable.location));
                    mapFragment.getMapAsync(mapreadycallback);
                }else{
                    Toast.makeText(DriveModeScreen.this, "Please Check Your GPS Settings", Toast.LENGTH_LONG).show();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

    }

    public double get_Speed(Location curr){
        double ans=0;
        if (last_location != null) {
            ans = Math.sqrt(Math.pow(curr.getLongitude() - last_location.getLongitude(), 2) + Math.pow(curr.getLatitude() - last_location.getLatitude(), 2)) / (curr.getTime() - this.last_location.getTime());
            //if there is speed from location
        }
        if (curr.hasSpeed()) {
            //get location speed
            ans = curr.getSpeed();
        }
        last_location = curr;

        if(Double.isNaN(ans)){
            ans=0;
        }

        return ans;
    }


    public class MyLocationListener implements android.location.LocationListener
    {
        public void onLocationChanged(Location location) {
            String myLocation = "Latitude = " + location.getLatitude() + " Longitude = " + location.getLongitude();

            //I make a log to see the results
            Log.e("MY CURRENT LOCATION", myLocation);
        }

        public void onStatusChanged(String s, int i, Bundle b) {
        }

        public void onProviderDisabled(String s) {
        }

        public void onProviderEnabled(String s) {
        }
    }

    @Override
    protected void onPause(){
        super.onPause();
    }
    //*********************************

    public int initial=0;
    public boolean status=false;
    public String ComMsg="";

    public void getUnreadMessages(){
        DataBaseHandler db = new DataBaseHandler(getApplicationContext());
        List<dbTableMessages> UnList = db.getUnReadMessages();

        if(UnList.size() > 0){
            if(!status){
                status=true;
                initial = UnList.size();
                ComMsg="";
                for(int i=0;i<UnList.size();i++){
                    dbTableMessages temp = UnList.get(i);
                    ComMsg = ComMsg+"** "+temp.getTo_name()+" - "+temp.getMsg_text()+" || ";
                }
                driveMsg.setText(ComMsg);
            }else{
                if(initial != UnList.size()){
                    initial = UnList.size();
                    ComMsg="";
                    for(int i=0;i<UnList.size();i++){
                        dbTableMessages temp = UnList.get(i);
                        ComMsg = ComMsg+"** "+temp.getTo_name()+" - "+temp.getMsg_text()+" || ";
                    }
                    driveMsg.setText(ComMsg);
                }
            }
        }else{
            driveMsg.setText("No Unread Messages");
        }

    }
}
