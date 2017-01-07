package com.bb.hp_pc.bussmartv3;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
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
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class MapViewScreen extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,OnMapReadyCallback {

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

    ArrayList<Loc_Class> Location_list = new ArrayList<>();

    //Zoom **********
    //Initialize to a non-valid zoom value
    private float previousZoomLevel = 15;
    private boolean isZooming = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_view_screen);
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
                Intent my_intent = new Intent(MapViewScreen.this, my_class);
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
        getMenuInflater().inflate(R.menu.map_view_screen, menu);
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
                Intent my_intent = new Intent(MapViewScreen.this, my_class);
                startActivity(my_intent);
                overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                finish();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        } else if (id == R.id.nav_drive_mode) {
            try {
                mTimer.cancel();
                mTimer.purge();

                Class my_class = Class.forName("com.bb.hp_pc.bussmartv3.DriveModeScreen");
                Intent my_intent = new Intent(MapViewScreen.this, my_class);
                startActivity(my_intent);
                overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                finish();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        } else if (id == R.id.nav_search_bus) {
            try {
                mTimer.cancel();
                mTimer.purge();

                Class my_class = Class.forName("com.bb.hp_pc.bussmartv3.SearchBusScreen");
                Intent my_intent = new Intent(MapViewScreen.this, my_class);
                startActivity(my_intent);
                overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                finish();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        } else if (id == R.id.nav_map_view) {

        } else if (id == R.id.nav_messages) {
            try {
                mTimer.cancel();
                mTimer.purge();

                Class my_class = Class.forName("com.bb.hp_pc.bussmartv3.MessagesScreen");
                Intent my_intent = new Intent(MapViewScreen.this, my_class);
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
                Intent my_intent = new Intent(MapViewScreen.this, my_class);
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

                    mTimer.cancel();
                    mTimer.purge();

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

    public void Initialize(){
        user = new User();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View navView = navigationView.getHeaderView(0);
        user_name = (TextView) navView.findViewById(R.id.user_name);
        user_email = (TextView) navView.findViewById(R.id.user_email);

        user_name.setText(user.get_user_name());
        user_email.setText(user.get_user_email());

        // Temp
        Loc_Class t = new Loc_Class();

        t.user_name = "Salika Irushan";
        t.longitude_val = 79.9437;
        t.latitude_val = 6.976107;
        Location_list.add(t);

        Loc_Class t2 = new Loc_Class();
        t2.user_name = "Praveen";
        t2.longitude_val = 79.845955;
        t2.latitude_val = 6.938028;
        Location_list.add(t2);

        Loc_Class t3 = new Loc_Class();
        t3.user_name = "Ashen";
        t3.longitude_val = 79.87851;
        t3.latitude_val = 6.944022;
        Location_list.add(t3);
        // Temp
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

        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(my_loc));
        LatLng my_loc = new LatLng(latitude,longitude);
        if(!add_marker) {
            mMap.addMarker(marker_temp);
            add_marker = true;
            //mMap is an instance of GoogleMap
            mMap.setOnCameraChangeListener(getCameraChangeListener());

            // Instantiates a new CircleOptions object and defines the center and radius
            CircleOptions circleOptions = new CircleOptions()
                    .center(my_loc)
                    .radius(500)
                    .strokeColor(Color.rgb(255, 118, 0))
                    .fillColor(Color.argb(50, 0, 98, 255)); // In meters

            // Get back the mutable Circle
            Circle circle = mMap.addCircle(circleOptions);

            //**** TEMP  *******
            for (int i = 0; i < Location_list.size(); i++) {
                Loc_Class temp = (Loc_Class) Location_list.get(i);
                LatLng loc = new LatLng(temp.latitude_val, temp.longitude_val);

                MarkerOptions m_temp = new MarkerOptions().position(loc).title(temp.user_name);
                // Changing marker icon
                m_temp.icon(BitmapDescriptorFactory.fromResource(R.drawable.other_users_pin));
                mMap.addMarker(m_temp);
            }
            //**** TEMP  *******


            // Construct a CameraPosition focusing on Mountain View and animate the camera to that position.
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(my_loc)            // Sets the center of the map to Mountain View
                    .zoom(previousZoomLevel)   // Sets the zoom
                    .bearing(0)                // Sets the orientation of the camera to east
                    .tilt(50)                  // Sets the tilt of the camera to 30 degrees
                    .build();
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }
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
                    // Update Server
                    //GPS_Call((float)longitude,(float)latitude);
                    LatLng my_loc = new LatLng(latitude,longitude);
                    marker_temp = new MarkerOptions().position(my_loc).title(user.get_user_name());
                    // Changing marker icon
                    marker_temp.icon(BitmapDescriptorFactory.fromResource(R.drawable.location));
                    mapFragment.getMapAsync(mapreadycallback);
                }else{
                    Toast.makeText(MapViewScreen.this, "Please Check Your GPS Settings", Toast.LENGTH_LONG).show();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

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

    public class Loc_Class{
        public String user_name="";
        public double longitude_val=0;
        public double latitude_val=0;

        public Loc_Class(){

        }

    }

}
