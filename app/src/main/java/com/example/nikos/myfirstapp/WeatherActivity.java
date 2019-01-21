package com.example.nikos.myfirstapp;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.AnimationDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;



import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import java.util.List;
import java.util.Locale;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;


public class WeatherActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private GoogleApiClient googleApiClient;
    private FusedLocationProviderClient fusedLocationProviderClient;

    private TextView textView, tempTextView;
    private ImageView weatherImg;

    public static final int RequestPermissionCode = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        textView = findViewById(R.id.locationTxt);
        tempTextView = findViewById(R.id.weatherTempTxt);
        weatherImg = findViewById(R.id.weatherImg);


        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

    }

    @Override
    protected void onStart() {
        super.onStart();
        googleApiClient.connect();
    }

    @Override
    protected void onStop() {
        if (googleApiClient.isConnected()) {
            googleApiClient.disconnect();
        }
        super.onStop();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

        Log.e("WeatherActivity", "Connection Faild:" + connectionResult.getErrorCode());

    }

    @Override
    public void onConnectionSuspended(int i) {

        Log.e("WeatherActivity", "Connection suspended");

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        if (ActivityCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            requestPermissions();

            startActivity(getIntent());
        }
        fusedLocationProviderClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {

                   Intent i = new Intent(WeatherActivity.this, MainActivity.class);

                    @Override
                    public void onSuccess(Location location) {

                        String loc = "";
                        String state ;

                        if (location != null) {
                            double latitude = location.getLatitude();
                            double longitude = location.getLongitude();

                            Geocoder geocoder = new Geocoder(WeatherActivity.this, Locale.getDefault());
                            try {
                                List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
                                List<Address> addresses1 = geocoder.getFromLocation(latitude,longitude,1);
                                if (addresses.size() > 0)

                                    loc = String.valueOf(addresses.get(0).getLocality());
                                    state = String.valueOf(addresses1.get(0).getCountryCode());

                                    //i.putExtra("location", loc);
                                  // i.putExtra("state",state);

                                    find_weather(loc,state);

                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                        }
                    }
                });
    }

    private void requestPermissions() {

        ActivityCompat.requestPermissions(WeatherActivity.this, new
                String[]{ACCESS_FINE_LOCATION}, RequestPermissionCode);


    }


    public void find_weather(String location, String state) {
        final Intent intent = new Intent(WeatherActivity.this, MainActivity.class);

        String url = "https://api.apixu.com/v1/current.json?key=17685f37e32843729dd205128180312&q=" + location+","+ state;
        //final String urlimage = "http://openweathermap.org/img/w/";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {
                    JSONObject location_object = response.getJSONObject("location");
                    JSONObject current_object = response.getJSONObject("current");
                    JSONObject condition = (JSONObject) current_object.get("condition");
                    String temp = String.valueOf(current_object.getDouble("temp_c"));
                    String city = location_object.getString("name");
                    String img = condition.getString("icon");

                    intent.putExtra("location", temp);

                    String imgurl = "http:"+img;
                    loadImageFromUrl(imgurl);
                    textView.setText(city);


                    tempTextView.setText(String.valueOf(temp));

                } catch (JSONException e) {

                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }


        );

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(jsonObjectRequest);

    }

    private void loadImageFromUrl(String url) {



        Picasso.with(this).load(url)
                .into(weatherImg, new com.squareup.picasso.Callback() {


                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError() {

                    }
                });


    }
}
