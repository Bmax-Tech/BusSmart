package com.bb.hp_pc.bussmartv3;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
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


public class SearchFragmentTwo extends Fragment {
    ArrayList<bus> bus_listdata = new ArrayList<bus>();
    ProgressDialog progress;
    ListView list;
    LinearLayout hidden_layout_2;
    Calendar c;
    User user = new User();
    View root;
    Boolean pre_load=false;

    public SearchFragmentTwo() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_search_fragment_two, container, false);
        InitializeItems();

        return root;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser){
        super.setUserVisibleHint(isVisibleToUser);
        if(isVisibleToUser && !pre_load){
            progress = ProgressDialog.show(root.getContext(), "", "Fetching Results...");
            new Json_class().execute("http://php-eayurveda.rhcloud.com/redirect.php?bus_results=YES&route_no="+user.get_user_bus_route()+"&current_loc=&destination_loc=");
        }
    }

    private void InitializeItems(){
        list = (ListView) root.findViewById(R.id.search_result_list_2);
        hidden_layout_2 = (LinearLayout) root.findViewById(R.id.hidden_layout_2);
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

            if(!result.equals("null")) {
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
                            String b_datetime = js.getJSONObject("bus_" + i).getString("added_date").toString();

                            bus b = new bus(id, b_num, b_type, b_source, b_destination, b_departure, b_reach, b_driver, b_datetime);
                            bus_listdata.add(b);
                        }
                    }
                    list.setAdapter(new MyCustomBaseAdapter(root.getContext(), bus_listdata));
                    progress.dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }else{
                hidden_layout_2.setVisibility(root.VISIBLE);
                progress.dismiss();
            }
            pre_load=true;
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
                convertView = mInflater.inflate(R.layout.search_result_row_two, null);
                holder = new ViewHolder();

                holder.bus_number = (TextView) convertView.findViewById(R.id.bus_number);
                holder.bus_type = (TextView) convertView.findViewById(R.id.bus_type);
                holder.bus_departure_time = (TextView) convertView.findViewById(R.id.departure_time);
                holder.bus_reach_time = (TextView) convertView.findViewById(R.id.reach_time);
                holder.posted_date = (TextView) convertView.findViewById(R.id.posted_date);
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
            holder.posted_date.setText((temp_b.bus_add_datetime).substring(0,10));

            if(temp_b.bus_type.equals("AC")){
                holder.bus_number.setTextColor(Color.rgb(5, 66, 177));
                holder.layout_result.setBackgroundResource(R.drawable.gradient_color_row_search_1);
            }else if(temp_b.bus_type.equals("Standard")){
                holder.bus_number.setTextColor(Color.rgb(5, 177, 76));
                holder.layout_result.setBackgroundResource(R.drawable.gradient_color_row_search_2);
            }else if(temp_b.bus_type.equals("Semi Luxury")){
                holder.bus_number.setTextColor(Color.rgb(239, 100, 0));
                holder.layout_result.setBackgroundResource(R.drawable.gradient_color_row_search_3);
            }else if(temp_b.bus_type.equals("Luxury")){
                holder.bus_number.setTextColor(Color.rgb(239, 0, 12));
                holder.layout_result.setBackgroundResource(R.drawable.gradient_color_row_search_4);
            }

            return convertView;
        }

        public class ViewHolder {
            TextView bus_number,bus_type,bus_departure_time,bus_reach_time,posted_date,bus_driver;
            LinearLayout layout_result;
        }
    }
    /*
     *  Custom Array Adapter
     */

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
        public String bus_add_datetime;

        public bus(int p1,String p2,String p3,String p4,String p5,String p6,String p7,String p8,String p9){
            id = p1;
            bus_no = p2;
            bus_type = p3;
            bus_source = p4;
            bus_destination = p5;
            bus_departure = p6;
            bus_reach = p7;
            bus_driver = p8;
            bus_add_datetime = p9;
        }
    }
    //**************************************

}
