package lucidiok.motofun04;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.DirectionsApi;

/**
 * Created by lucidiok on 10/3/17.
 */

public class MapViewer implements OnMapReadyCallback {

    private final AppCompatActivity mAppCompatActivity;
    private MapFragment             mMapFragment;
    private GoogleMap               mGoogleMap;
    private LocationManager         mLocationManager;
    protected LocationListener mLocationListener = new android.location.LocationListener() {
        public void onLocationChanged(Location location) {

            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            CameraPosition pos = new CameraPosition(latLng, location.getBearing(), mGoogleMap.getCameraPosition().tilt, 15f/*mGoogleMap.getCameraPosition().zoom*/);
            mGoogleMap.moveCamera(CameraUpdateFactory.newCameraPosition(pos));
            mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(15f));
        }

        public void onStatusChanged(String provider, int status, Bundle extras) { }
        public void onProviderEnabled(String provider) { }
        public void onProviderDisabled(String provider) { }
    };

    public MapViewer(AppCompatActivity appCompatActivity, int mapFragmentId) {
        //super(appCompatActivity);
        mAppCompatActivity = appCompatActivity;
        mMapFragment = (MapFragment) mAppCompatActivity.getFragmentManager()
                .findFragmentById(mapFragmentId);
        mMapFragment.getMapAsync(this);
        mMapFragment.getView().setAlpha(0.5f);
    }

    public void SetDestination(String destination){

    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        try {
            mGoogleMap = googleMap;
            mGoogleMap.setMyLocationEnabled(true);
            mGoogleMap.setTrafficEnabled(true);
            mGoogleMap.setBuildingsEnabled(false);
            mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(15f));
            mLocationManager = (LocationManager)mAppCompatActivity.getSystemService(Context.LOCATION_SERVICE);
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 500, 50, mLocationListener);
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

}
