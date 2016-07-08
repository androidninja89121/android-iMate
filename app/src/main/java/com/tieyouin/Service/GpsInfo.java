package com.tieyouin.Service;

import android.app.AlertDialog;
import android.app.Service;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.util.Log;

import com.tieyouin.Preference.PrefConst;
import com.tieyouin.Preference.UserPreference;
import com.tieyouin.utils.Common;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Created by Morning on 11/28/2015.
 */
public class GpsInfo extends Service implements LocationListener {

    // flag for gps state
    boolean isGPSEnabled = false;

    // flag for network status
    boolean isNetworkEnabled = false;

    boolean isGetLocation = false;

    Location location;
    double latitude, longitude;

    // the minimum distance to change update in metres
    private static final long MIN_DISTANCE_UPDATES = 10;

    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_UPDATES = 1000 * 60 * 1;

    // delcares a location manager
    protected LocationManager locationManager;

//    public GpsInfo(Context context) {
//
//        this._context = context;
//
//        // get last known location
//        getLastLocation();
//    }

    @Override
    public void onCreate() {
        super.onCreate();
        getLastLocation();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.d("Gps Info Service", "Service running...");
        return super.onStartCommand(intent, flags, startId);
    }

    private Location getLastLocation()  {

        try {
            locationManager = (LocationManager)getSystemService(LOCATION_SERVICE);

            isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

            isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if(!isGPSEnabled && !isNetworkEnabled){

                showSettingAlert();

            }else {
                this.isGetLocation = true;
                if(isNetworkEnabled){
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_UPDATES, MIN_DISTANCE_UPDATES, this);
                    if(locationManager != null){
                        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if(location != null){
                            //save the latitude, longitudeDURATION
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                        }
                    }
                }
                if(isGPSEnabled){
                    if(location == null){
                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                                MIN_TIME_UPDATES, MIN_DISTANCE_UPDATES, this);
                        if(locationManager != null){
                            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            if(location != null){
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
                            }
                        }
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return location;
    }

    // showing alert dialog
    /*GPS Alert Dialog*/
    public void showSettingAlert(){

        AlertDialog.Builder alertDialog =  new AlertDialog.Builder(this);

        alertDialog.setTitle("GPS Setting");
        alertDialog.setMessage("Would you go to GPS Setting Window ?");
        alertDialog.setPositiveButton("Setting",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        GpsInfo.this.startActivity(intent);
                    }
                });
        alertDialog.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int which){
                        dialog.cancel();
                    }
                });
        alertDialog.show();
    }

    /*Latitude value*/
    public double getLattitude(){
        if(location != null){
            latitude = location.getLatitude();
        }
        return latitude;
    }

    /*Longitude value*/
    public double getLongitude(){
        if(location != null){
            longitude = location.getLongitude();
        }
        return longitude;
    }

    public boolean isGetLocation(){ return this.isGetLocation;}

    @Override
    public void onLocationChanged(Location location) {
        // TOTO Auto-generated method stub

        if(location != null)
            this.location = location;

        UserPreference.getInstance().putSharedPreference(GpsInfo.this,
                PrefConst.PREF_USER_LAT, String.format("%.1f", location.getLatitude()));
        UserPreference.getInstance().putSharedPreference(GpsInfo.this,
                PrefConst.PREF_USER_LON, String.format("%.1f", location.getLongitude()));

        /*----------to get City-Name from coordinates ------------- */
        String cityName=null;
        String countryName = null;
        Geocoder gcd = new Geocoder(getBaseContext(),
                Locale.getDefault());
        List<Address> addresses;
        try {
            addresses = gcd.getFromLocation(location.getLatitude(), location.getLongitude(), 1);

            if (addresses.size() > 0)
                System.out.println(addresses.get(0).getLocality());

            cityName=addresses.get(0).getLocality();
            countryName = addresses.get(0).getCountryName();

            Common.user_city = cityName;
            Common.user_country = countryName;

            UserPreference.getInstance().putSharedPreference(GpsInfo.this,
                    PrefConst.PREF_USER_CITY, cityName);
            UserPreference.getInstance().putSharedPreference(GpsInfo.this,
                    PrefConst.PREF_USER_COUNTRY, countryName);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
