package com.example.gps_tracker;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

import static android.app.Service.START_STICKY;

public class MainActivity extends AppCompatActivity implements LocationListener {
    TextView latlong;
    Button getLocationBtn,getTableBtn;
    LocationManager locationManager;

    int lat, longi;
    MyDataBase myDataBase;
    RecyclerView rv;
    MyAdapter adapter;
    LinearLayoutManager manager;
    Cursor c;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myDataBase = new MyDataBase(this);
        myDataBase.open();
        myDataBase.querylocation();
        rv = findViewById(R.id.recycler);
        manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rv.setLayoutManager(manager);
        adapter = new MyAdapter();
        rv.setAdapter(adapter);

        getTableBtn = findViewById(R.id.gettableBtn);
        latlong = findViewById(R.id.t);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        getLocationBtn = findViewById(R.id.getLocationBtn);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 5, this);

//        getLocationBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                getLocation();
//            }
//        });

        final Handler handler = new Handler();
        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        try {

                            myDataBase.insertlatlong(lat,longi);
                            c = myDataBase.querylocation();
                            adapter.notifyDataSetChanged();
                        } catch (Exception e) {
                            Log.d("joan", e.toString());
                        }
                    }
                });
            }
        };
        timer.schedule(task, 0, 10000); //execute in every 50000 ms


//        getTableBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        });
    }

    //    void getLocation() {
//        try {
//            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 5, this);
//        }
//        catch(SecurityException e) {
//            e.printStackTrace();
//        }
//    }



    @Override
    public void onLocationChanged(Location location) {
        latlong.setText("Current Location: " + location.getLatitude() + ", " + location.getLongitude());
        lat = (int) location.getLatitude();
        longi = (int) location.getLongitude();
        myDataBase.insertlatlong(lat,longi);
        Toast.makeText(MainActivity.this, "inserted", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.d("Sus_Check : ", "Status Changed");
    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.d("Sus_Check : ", "Provider Enabled");
    }

    @Override
    public void onProviderDisabled(String provider) {
        Toast.makeText(MainActivity.this, "Please Enable GPS and Internet", Toast.LENGTH_SHORT).show();

    }

    public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder>{

        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View v = getLayoutInflater().inflate(R.layout.row,viewGroup,false);
            ViewHolder vh = new ViewHolder(v);
            return vh;
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
            c.moveToPosition(i);
            int lat1 = c.getInt(1);
            int long1 = c.getInt(2);
            viewHolder.lati.setText(lat1+"");
            viewHolder.longiti.setText(long1+"");
        }

        @Override
        public int getItemCount() {
            if (c != null) {
                return c.getCount();
            }
            return 0;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView lati,longiti;
            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                lati = itemView.findViewById(R.id.lattext);
                longiti = itemView.findViewById(R.id.longtext);
            }
        }
    }

}