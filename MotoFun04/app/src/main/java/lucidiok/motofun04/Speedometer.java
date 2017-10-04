package lucidiok.motofun04;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

/**
 * Created by lucidiok on 10/1/17.
 */

public class Speedometer  {

    public Speedometer(AppCompatActivity appCompatActivity, int textViewId) {
        mAppCompatActivity = appCompatActivity;
        mTextView   = (TextView)mAppCompatActivity.findViewById(R.id.speed);

        if (ActivityCompat.checkSelfPermission(mAppCompatActivity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ) {
            ActivityCompat.requestPermissions(mAppCompatActivity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_CODE);
            return;
        }
        mLocationManager = (LocationManager)mAppCompatActivity.getSystemService(Context.LOCATION_SERVICE);
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 500, 10, mLocationListener);
    }

    private LocationListener mLocationListener = new android.location.LocationListener() {
        public void onLocationChanged(Location location) {
            float speedMPH = location.getSpeed() / 2.236936f;
            int mph = (int)speedMPH;
            mTextView.setText(Integer.toString(mph));
        }

        public void onStatusChanged(String provider, int status, Bundle extras) { }
        public void onProviderEnabled(String provider) { }
        public void onProviderDisabled(String provider) { }
    };

    private static final int PERMISSION_REQUEST_CODE = 1000;
    private AppCompatActivity mAppCompatActivity;
    private TextView mTextView;
    private LocationManager mLocationManager;

}
