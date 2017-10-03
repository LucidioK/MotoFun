package lucidiok.motofun04;

import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.TextureView;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

public class MotoFunActivity extends AppCompatActivity implements TextureView.SurfaceTextureListener {

    private TextureView     mTextureView;
    private CameraPreviewer mCameraPreviewer;
    private TextView        mSpeedView;
    private Speedometer     mSpeedometer;
    private MapViewer       mMapViewer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_moto_fun);
        mSpeedView   = (TextView)findViewById(R.id.speed);
        mTextureView = (TextureView)findViewById(R.id.image);
        mTextureView.setSurfaceTextureListener(this);

    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int i, int i1) {
        try {
            mCameraPreviewer = new CameraPreviewer(this, mTextureView);
            mSpeedometer     = new Speedometer(this, mSpeedView);
            mMapViewer       = new MapViewer(this, R.id.map);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int i, int i1) { }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) { return true; }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) { }

    @Override
    public void onResume() {
        super.onResume();

        if (mCameraPreviewer != null) {
            try {
                mCameraPreviewer.Start();
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        }
    }

}
