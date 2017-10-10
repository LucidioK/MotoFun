package lucidiok.motofun04;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.DirectionsApi;
import com.google.maps.DistanceMatrixApi;
import com.google.maps.GeoApiContext;
import com.google.maps.errors.ApiException;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsStep;

import java.io.IOException;

/**
 * Created by lucidiok on 10/3/17.
 */

public class MapViewer implements OnMapReadyCallback {

    private final AppCompatActivity mAppCompatActivity;
    private MapFragment             mMapFragment;
    private GoogleMap               mGoogleMap;
    private LocationManager         mLocationManager;
    private GeoApiContext           mGeoApiContext;
    private LatLng                  mCurrentLatLong;
    private DirectionsStep[]        mDirectionsSteps;
    private TextView                mNextDirectionStep;

    protected LocationListener mLocationListener = new android.location.LocationListener() {
        public void onLocationChanged(Location location) {

            mCurrentLatLong = new LatLng(location.getLatitude(), location.getLongitude());
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(mCurrentLatLong));
            CameraPosition pos = new CameraPosition(mCurrentLatLong, location.getBearing(), mGoogleMap.getCameraPosition().tilt, 15f/*mGoogleMap.getCameraPosition().zoom*/);
            mGoogleMap.moveCamera(CameraUpdateFactory.newCameraPosition(pos));
            mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(15f));
            if (mDirectionsSteps != null){
                displayNextStep();
            }
        }

        public void onStatusChanged(String provider, int status, Bundle extras) { }
        public void onProviderEnabled(String provider) { }
        public void onProviderDisabled(String provider) { }
    };

    private void displayNextStep() {
        double mininumDistance = (double)Long.MAX_VALUE;
        int nearestStep = -1;
        for (int i = 0; i < mDirectionsSteps.length; i++) {
            double dist = distance(mCurrentLatLong, new LatLng(mDirectionsSteps[0].endLocation.lat, mDirectionsSteps[0].endLocation.lng));
            if (dist < mininumDistance) {
                mininumDistance = dist;
                nearestStep = i;
            }
        }
        mNextDirectionStep.setVisibility(View.VISIBLE);
        mNextDirectionStep.setText((CharSequence) mDirectionsSteps[nearestStep].htmlInstructions);
    }

    private double distance(LatLng p1, LatLng p2) {
        double dx2 = Math.pow(Math.abs(p1.latitude - p2.latitude), 2);
        double dy2 = Math.pow(Math.abs(p1.longitude - p2.longitude), 2);
        return Math.sqrt(dx2 + dy2);
    }

    public MapViewer(AppCompatActivity appCompatActivity, int mapFragmentId, int nextDirectionStepId) {
        //super(appCompatActivity);
        mAppCompatActivity = appCompatActivity;
        mNextDirectionStep = (TextView)mAppCompatActivity.findViewById(nextDirectionStepId);
        mMapFragment = (MapFragment) mAppCompatActivity.getFragmentManager()
                .findFragmentById(mapFragmentId);
        mMapFragment.getMapAsync(this);
        mMapFragment.getView().setAlpha(0.5f);
    }

    public void StartDestination(String destination) throws InterruptedException, ApiException, IOException {
        //GeoApiContext geoApiContext = new GeoApiContext();
        final String origin = mCurrentLatLong.latitude + "," + mCurrentLatLong.longitude;
        DirectionsResult result =
            DirectionsApi.getDirections(mGeoApiContext, origin, destination).await();
        mDirectionsSteps = result.routes[0].legs[0].steps;
    }

    public void StopDestination(){
        mDirectionsSteps = null;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        try {
            mGoogleMap = googleMap;
            mGoogleMap.setMyLocationEnabled(true);
            mGoogleMap.setTrafficEnabled(true);
            mGoogleMap.setBuildingsEnabled(false);
            mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(15f));
            mGoogleMap.getUiSettings().setCompassEnabled(true);
            mGoogleMap.getUiSettings().setMapToolbarEnabled(true);
            mLocationManager = (LocationManager)mAppCompatActivity.getSystemService(Context.LOCATION_SERVICE);
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 500, 50, mLocationListener);
            mGeoApiContext = new GeoApiContext.Builder().apiKey("AIzaSyD_iJCQ-XVvHJ9qryEycY3Scfb37DWYtg0").build();
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

}
