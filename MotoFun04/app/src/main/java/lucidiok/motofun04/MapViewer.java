package lucidiok.motofun04;

import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;

/**
 * Created by lucidiok on 10/3/17.
 */

public class MapViewer implements OnMapReadyCallback {
    private final AppCompatActivity mAppCompatActivity;
    private MapFragment             mMapFragment;
    private GoogleMap               mGoogleMap;

    public MapViewer(AppCompatActivity appCompatActivity, int mapFragmentId) {
        //super(appCompatActivity);
        mAppCompatActivity = appCompatActivity;
        mMapFragment = (MapFragment) mAppCompatActivity.getFragmentManager()
                .findFragmentById(mapFragmentId);
        mMapFragment.getMapAsync(this);
        mMapFragment.getView().setAlpha(0.5f);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        try {
            mGoogleMap = googleMap;
            mGoogleMap.setMyLocationEnabled(true);
        }catch (SecurityException e) {
            e.printStackTrace();
        }
    }

}
