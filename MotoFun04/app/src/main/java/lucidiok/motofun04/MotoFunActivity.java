package lucidiok.motofun04;

import android.graphics.SurfaceTexture;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.TextureView;
import android.view.View;
import android.widget.TextView;

public class MotoFunActivity extends AppCompatActivity implements TextureView.SurfaceTextureListener {

    private CameraPreviewer mCameraPreviewer;
    private Speedometer     mSpeedometer;
    private MapViewer       mMapViewer;
    private TextView        mAdressText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_moto_fun);
        ((TextureView)findViewById(R.id.image)).setSurfaceTextureListener(this);
        mAdressText = (TextView)findViewById(R.id.address);
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int i, int i1) {
        try {
            mCameraPreviewer = new CameraPreviewer(this, R.id.image);
            mSpeedometer     = new Speedometer(this, R.id.speed);
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

    public void onClickDest(View v) {
        mAdressText.setVisibility(View.VISIBLE);
    }
}
