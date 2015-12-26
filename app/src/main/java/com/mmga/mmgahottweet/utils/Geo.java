package com.mmga.mmgahottweet.utils;


import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;

import com.mmga.mmgahottweet.data.Constant;

public class Geo {
    static LocationManager locationManager;
    static String provider = LocationManager.NETWORK_PROVIDER;

    public static String getGeocode(Context context) {
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ToastUtil.showShort("permission not granted");
        }
        Location location = locationManager.getLastKnownLocation(provider);

        String geoCode = ""+location.getLatitude() + "," + location.getLongitude() + "," + Constant.GEO_SPHERE;
        LogUtil.d("geocode = " + geoCode);

        return geoCode;
    }

}
