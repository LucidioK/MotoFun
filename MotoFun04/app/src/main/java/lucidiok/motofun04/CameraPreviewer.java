package lucidiok.motofun04;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Size;
import android.view.Surface;
import android.view.TextureView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by lucidiok on 10/1/17.
 */

public class CameraPreviewer  {
    private AppCompatActivity mAppCompatActivity;
    private TextureView mTextureView;
    private CameraDevice mCamera;
    private CaptureRequest.Builder mCaptureRequestBuilder;
    private CameraCaptureSession mCameraCaptureSessions;
    private CameraCharacteristics mCameraCharacteristics;
    private Handler mBackgroundHandler;
    private HandlerThread mBackgroundThread;
    private Size mImageDimension;
    private int mDisplayRotation;
    private CameraManager mCameraManager;
    private String mCameraId;

    public CameraPreviewer(AppCompatActivity appCompatActivity, int textureViewId) throws CameraAccessException {
        mAppCompatActivity = appCompatActivity;
        mTextureView = (TextureView)mAppCompatActivity.findViewById(textureViewId);
        mCameraManager = (CameraManager) appCompatActivity.getSystemService(Context.CAMERA_SERVICE);
        mCameraId = getFrontFacingCameraId();
        mCameraCharacteristics = mCameraManager.getCameraCharacteristics(mCameraId);
        mImageDimension = getImageDimension();
        mDisplayRotation = appCompatActivity.getWindowManager().getDefaultDisplay().getRotation();
        openSensor();
    }

    private void openSensor() throws CameraAccessException {
        if (ActivityCompat.checkSelfPermission(mAppCompatActivity, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(mAppCompatActivity, new String[]{Manifest.permission.CAMERA}, 200/*REQUEST_CAMERA_PERMISSION*/);
        else
            mCameraManager.openCamera(mCameraId, mStateCallback, null);
    }

    private String getFrontFacingCameraId() {

        try {
            for (final String cameraId : mCameraManager.getCameraIdList()) {
                CameraCharacteristics characteristics = mCameraManager.getCameraCharacteristics(cameraId);
                int cOrientation = characteristics.get(CameraCharacteristics.LENS_FACING);
                if (cOrientation == CameraCharacteristics.LENS_FACING_FRONT) return cameraId;
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Size getImageDimension() {
        float aspect = mTextureView.getWidth() / mTextureView.getHeight();
        boolean textureViewOrientation = aspect >= 1;
        List<Size> ls = new ArrayList<Size>(Arrays.asList(mCameraCharacteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP).getOutputSizes(SurfaceTexture.class)));
        Size selectedSize = new Size(0, 0);
        float lowestDelta = Float.MAX_VALUE;
        for (Size s : ls) {
            boolean thisSizeOrientation = (s.getWidth() / s.getHeight()) >= 1;
            float thisDelta = Math.abs(mTextureView.getWidth() - s.getWidth());
            if (textureViewOrientation == thisSizeOrientation && thisDelta < lowestDelta) {
                selectedSize = s;
                lowestDelta = thisDelta;
            }
        }
        return selectedSize;
    }

    private final CameraDevice.StateCallback mStateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(CameraDevice camera) {
            mCamera = camera;

            try {
                Surface surface = getSurface();
                createCaptureRequest(surface);
                createCaptureSession(surface);
                setImageLayout();
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        }

        private void createCaptureRequest(Surface surface) throws CameraAccessException {
            mCaptureRequestBuilder = mCamera.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            mCaptureRequestBuilder.addTarget(surface);
        }

        @NonNull
        private Surface getSurface() {
            SurfaceTexture texture = mTextureView.getSurfaceTexture();
            assert texture != null;
            texture.setDefaultBufferSize(mImageDimension.getWidth(), mImageDimension.getHeight());
            return new Surface(texture);
        }

        private void setImageLayout() {
            Matrix matrix = new Matrix();
            final float viewWidth = mTextureView.getWidth();
            final float viewHeight = mTextureView.getHeight();
            RectF viewRect = new RectF(0, 0, viewWidth, viewHeight);

            if (Surface.ROTATION_90 == mDisplayRotation || Surface.ROTATION_270 == mDisplayRotation) {
                final float sx = viewHeight / mImageDimension.getWidth();
                final float sy = viewWidth / mImageDimension.getHeight();
                matrix.postScale(sx, sy, viewRect.centerX(), viewRect.centerY());
                matrix.postRotate(90 * (mDisplayRotation - 2), viewRect.centerX(), viewRect.centerY());
            } else if (Surface.ROTATION_180 == mDisplayRotation) {
                matrix.postRotate(180, viewRect.centerX(), viewRect.centerY());
            }
            mTextureView.setTransform(matrix);
            mTextureView.requestLayout();
        }

        private void createCaptureSession(Surface surface) throws CameraAccessException {
            mCamera.createCaptureSession(Arrays.asList(surface), new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(@NonNull CameraCaptureSession cameraCaptureSession) {
                    if (null == mCamera) {
                        return;
                    }
                    // When the session is ready, we start displaying the preview.
                    mCameraCaptureSessions = cameraCaptureSession;
                    updatePreview();
                }

                @Override
                public void onConfigureFailed(@NonNull CameraCaptureSession cameraCaptureSession) {
                    Toast.makeText(mAppCompatActivity, "Configuration change", Toast.LENGTH_SHORT).show();
                }
            }, null);
        }

        @Override
        public void onDisconnected(CameraDevice camera) { mCamera.close(); }

        @Override
        public void onError(CameraDevice camera, int error) { mCamera.close(); mCamera = null; }

    };

    private void updatePreview() {
        if (null == mCamera) {
            Log.e("xuxu", "updatePreview error, return");
        }
        mCaptureRequestBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
        try {
            mCameraCaptureSessions.setRepeatingRequest(mCaptureRequestBuilder.build(), null, mBackgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private TextureView.SurfaceTextureListener mTextureListener = new TextureView.SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
            try {
                openSensor();
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        }
        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) { }
        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) { return false; }
        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surface) { }
    };
}