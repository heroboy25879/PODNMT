package com.hitachi_tstv.mist.it.pod_nmt;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Tunyaporn on 9/19/2017.
 */

public class UtilityClass {
    private LocationManager locationManager;
    private Context context;
    private String latString, longString, timeString;

    private float percentage;

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
            int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            percentage = (level / (float) scale) * 100;
        }
    };

    String getDeviceID() {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        return telephonyManager.getDeviceId();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    String getSerial() {
        String serial = null;
        try {
            Class<?> c = Class.forName("android.os.SystemProperties");
            Method get = c.getMethod("get", String.class, String.class);
            serial = (String) get.invoke(c, "ril.serialnumber", "unknown");
        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return serial;
    }

    void getBattery() {

        IntentFilter iFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);

        // Register the broadcast receiver
        context.registerReceiver(mBroadcastReceiver, iFilter);
        Log.d("VAL-Tag-GPS", "Battery ==> " + percentage);

    }

    String getDeviceName() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        return bluetoothAdapter.getName();
    }
    public UtilityClass(Context context) {
        this.context = context;
        latString = "Unknown";
        longString = "Unknown";
    }

    private Location requestLocation(String strProvider, String strError) {

        Location location = null;

        if (locationManager.isProviderEnabled(strProvider)) {


            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return null;
            }
            locationManager.requestLocationUpdates(strProvider, 1000, 10, locationListener);
            location = locationManager.getLastKnownLocation(strProvider);

        } else {
            Log.d("VAL-Tag-Util", strError);
        }


        return location;
    }

    private final LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    };

    private void setupLocation() {

        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);


    }   // setupLocation

    String getTimeString() {
        return timeString;
    }

    private double getMeterFromLatLong(double lat1, double lat2, double lng1, double lng2) {
        Math math = null;
        double result, diffLat, diffLong;
        lat1 = lat1 * (math.PI / 180);
        lat2 = lat2 * (math.PI / 180);
        lng1 = lng1 * (math.PI / 180);
        lng2 = lng2 * (math.PI / 180);

        diffLat = lat2 - lat1;
        diffLong = lng2 - lng1;
        result = (math.sin(diffLat / 2) * math.sin(diffLat / 2)) + (math.cos(lat1) * math.cos(lat2) * (math.sin(diffLong / 2) * math.sin(diffLong / 2)));
        result = 2 * math.atan2(math.sqrt(result), math.sqrt(1 - result));
        result = 6371 * result;
        return result;
    }

    String getDistanceMeter(String storeLatString, String storeLngString) {
        String strLat = latString;
        String strLng = longString;


        if (strLat.equals("Unknown") && strLng.equals("Unknown")) {
            Toast.makeText(context, context.getResources().getString(R.string.err_gps1), Toast.LENGTH_SHORT).show();
            return null;
        } else {
            Log.d("VAL-Tag-Util", " ++++++++++Latitude.-> " + strLat + " Longitude.-> " + strLng);

            double lat1, lat2, lng1, lng2;
            lat1 = Double.parseDouble(strLat);
            lng1 = Double.parseDouble(strLng);
            lat2 = Double.parseDouble(storeLatString);
            lng2 = Double.parseDouble(storeLngString);

            Log.d("VAL-Tag-Util", "lat1 ==> " + lat1 + " lng1 ==> " + lng1 + " lat2 ==> " + lat2 + " lng2 ==> " + lng2);

            double result = getMeterFromLatLong(lat1, lat2, lng1, lng2);
            float km = (float) result;
            float m = Float.parseFloat(String.format("%.2f", km)) * 1000;

            return String.valueOf(m);
        }
    }

    String getDistanceKiloMeter(String storeLatString, String storeLngString) {
        String strLat = latString;
        String strLng = longString;


        if (strLat.equals("Unknown") && strLng.equals("Unknown")) {
            Toast.makeText(context, context.getResources().getString(R.string.err_gps1), Toast.LENGTH_SHORT).show();
            return null;
        } else {
            Log.d("VAL-Tag-Util", " ++++++++++Latitude.-> " + strLat + " Longitude.-> " + strLng);

            double lat1, lat2, lng1, lng2;
            lat1 = Double.parseDouble(strLat);
            lng1 = Double.parseDouble(strLng);
            lat2 = Double.parseDouble(storeLatString);
            lng2 = Double.parseDouble(storeLngString);

            Log.d("VAL-Tag-Util", "lat1 ==> " + lat1 + " lng1 ==> " + lng1 + " lat2 ==> " + lat2 + " lng2 ==> " + lng2);

            double result = getMeterFromLatLong(lat1, lat2, lng1, lng2);
            float km = (float) result;

            return String.valueOf(km);
        }
    }

    boolean setLatLong(int rev) {
        boolean b = true;
        boolean result = false;

        do {
            Log.d("VAL-Tag-Util", "Do " + rev);
            String strLat = "Unknown";
            String strLng = "Unknown";
            setupLocation();
            Location networkLocation = requestLocation(LocationManager.NETWORK_PROVIDER, "No Internet");
            if (networkLocation != null) {
                strLat = String.format(new Locale("th"), "%.7f", networkLocation.getLatitude());
                strLng = String.format(new Locale("th"), "%.7f", networkLocation.getLongitude());
            }

            Location gpsLocation = requestLocation(LocationManager.GPS_PROVIDER, "No GPS card");
            if (gpsLocation != null) {
                strLat = String.format(new Locale("th"), "%.7f", gpsLocation.getLatitude());
                strLng = String.format(new Locale("th"), "%.7f", gpsLocation.getLongitude());
            }

            if (strLat.equals("Unknown") && strLng.equals("Unknown") && rev < 10) {

                rev++;
                Log.d("VAL-Tag-Util", "Repeat");
            } else if (strLat.equals("Unknown") && strLng.equals("Unknown") && rev >= 10) {
                //Can't get lat/long
                Log.d("VAL-Tag-Util", "Can't get lat/long");
                rev++;
                b = false;
            } else {
                latString = strLat;
                longString = strLng;
                b = false;
                result = true;
            }
        } while (b);


        return result;

    }

    String getLatString() {
        return latString;
    }

    String getLongString() {
        return longString;
    }

    String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS", Locale.getDefault());
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        Date date = new Date();
        timeString = timeFormat.format(date);
        return dateFormat.format(date);
    }


}