package com.example.wifindhotspot;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {


    private WifiManager wifiManager;
    private WifiConfiguration wifiConfiguration;
    private WifiManager.LocalOnlyHotspotReservation hotspotReservation = null;
    TextView tv ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv = findViewById(R.id.textView);
        checkLocationPermission();

        wifiManager = ((WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE));

        wifiConfiguration = new WifiConfiguration();


    }







    @Override
    protected void onResume() {
        super.onResume();
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

        }
    }

    public void turnOnWifi(View view) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            Intent intent = new Intent(Settings.Panel.ACTION_WIFI);

            if (!wifiManager.isWifiEnabled())
                startActivityForResult(intent, 1);


        } else {
            wifiManager.setWifiEnabled(true);
        }
    }

    public void turnOffWifi(View view) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            Intent intent = new Intent(Settings.Panel.ACTION_WIFI);

            if (wifiManager.isWifiEnabled())
                startActivityForResult(intent, 1);

        } else {
            wifiManager.setWifiEnabled(false);
        }
    }

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(this)
                        .setTitle("R.string.title_location_permission")
                        .setMessage("R.string.text_location_permission")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(MainActivity.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION);
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {


                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.

                }
                return;
            }

        }
    }

    public void turnOffHotspot(View view){
        if(hotspotReservation!= null)
            hotspotReservation.close();
        hotspotReservation=null;
        tv.setText("CLOSE HOTSPOT");

    }

    public void turnOnHotspot(View view) {

        //WifiManager manager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Log.i("here","it comes here 1");

        if(hotspotReservation == null) {
            tv.setText("yoo HOTSPOT");

            wifiManager.startLocalOnlyHotspot(new
                                                      WifiManager.LocalOnlyHotspotCallback() {
                                                          @Override
                                                          public void onStarted(WifiManager.LocalOnlyHotspotReservation reservation) {
                                                              super.onStarted(reservation);

                                                              Log.i("here", "it comes here 2");
                                                              hotspotReservation = reservation;

                                                              String key = hotspotReservation.getWifiConfiguration().preSharedKey;
                                                              String ussid = hotspotReservation.getWifiConfiguration().SSID;
                                                              System.out.println("KEY: " + key);
                                                              System.out.println("USSID: " + ussid);

                                                              System.out.println("STARTED THE HOTSPOT");

                                                              tv.setText("key :- " + key + "\n USSID:- " + ussid);

//                                                          WifiConfiguration conf = hotspotReservation.getWifiConfiguration();
//
//                                                          conf.SSID = "\"" +  "ANDROIDHOTSPOT" + "\"";
//                                                          conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
//                                                          conf.preSharedKey = "\""+ "123456789" +"\"";
//                                                          conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
//
//                                                          wifiManager.addNetwork(conf);

//                                                          hotspotReservation.getWifiConfiguration().SSID = "ANDROIDHOTSPOT";
//                                                          String randomUUID = UUID.randomUUID().toString();
//                                                          hotspotReservation.getWifiConfiguration().preSharedKey = randomUUID.substring(0,9);
//
//                                                          wifiManager.addNetwork(hotspotReservation.getWifiConfiguration());
//





                                                          }

                                                          @Override
                                                          public void onStopped() {
                                                              super.onStopped();
                                                              System.out.println("STOPPED THE HOTSPOT");
                                                          }

                                                          @Override
                                                          public void onFailed(int reason) {
                                                              super.onFailed(reason);
                                                              System.out.println("FAILED THE HOTSPOT");
                                                          }
                                                      }, new Handler());
        }
        Log.i("here","it comes here 3");


    }


    public void goToClient(View view) {
        Intent i = new Intent(getApplicationContext(),ClientActivity.class);
        startActivity(i);
    }

    public void goToServer(View view) {
        Intent i = new Intent(getApplicationContext(),ServerActivity.class);
        startActivity(i);
    }
}