package lucidiok.motofun04;

import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.TextureView;
import android.widget.TextView;

public class MotoFunActivity extends AppCompatActivity implements TextureView.SurfaceTextureListener{

    private TextureView mTextureView;
    private CameraPreviewer mCameraPreviewer;
    private TextView mSpeedView;
    private Speedometer mSpeedometer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_moto_fun);
        //ConstraintLayout layout = (ConstraintLayout)findViewById(R.id.main);
        mSpeedView = (TextView)findViewById(R.id.speed);
        mSpeedView.setElevation(10);
        //layout.addView(mSpeedView);
        mTextureView = (TextureView)findViewById(R.id.image);
        mTextureView.setSurfaceTextureListener(this);
        mTextureView.setElevation(0);
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int i, int i1) {
        try {
            mCameraPreviewer = new CameraPreviewer(this, mTextureView);
            mSpeedometer = new Speedometer(this, mSpeedView);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int i, int i1) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
        return true;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mSpeedometer != null){
            mSpeedometer.Start();
        }
        if (mCameraPreviewer != null) {
            try {
                mCameraPreviewer.Start();
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        }
    }
}
