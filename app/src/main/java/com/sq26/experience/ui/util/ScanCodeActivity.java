package com.sq26.experience.ui.util;

import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.android.CaptureActivityHandler;
import com.google.zxing.client.android.InactivityTimer;
import com.google.zxing.client.android.ViewfinderView;
import com.google.zxing.client.android.camera.CameraManager;
import com.sq26.experience.R;

import java.io.IOException;
import java.util.Collection;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ScanCodeActivity extends AppCompatActivity implements SurfaceHolder.Callback {

    @BindView(R.id.surfaceView)
    SurfaceView surfaceView;
    @BindView(R.id.viewfinderView)
    ViewfinderView viewfinderView;

    private boolean hasSurface;
    private CameraManager cameraManager;
    private CaptureActivityHandler handler;
    private Collection<BarcodeFormat> decodeFormats;
    private InactivityTimer inactivityTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_code);
        ButterKnife.bind(this);
        hasSurface = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        cameraManager = new CameraManager(getApplication());
        viewfinderView.setCameraManager(cameraManager);
        handler = null;
        decodeFormats = null;

        SurfaceHolder surfaceHolder = surfaceView.getHolder();
        if (hasSurface) {
            // The activity was paused but not stopped, so the surface still exists. Therefore
            // surfaceCreated() won't be called, so init the camera here.
//            initCamera(surfaceHolder);
        } else {
            // Install the callback and wait for surfaceCreated() to init the camera.
            surfaceHolder.addCallback(this);
        }
    }

//    private void initCamera(SurfaceHolder surfaceHolder) {
//        if (surfaceHolder == null) {
//            throw new IllegalStateException("没有提供SurfaceHolder");
//        }
//        if (cameraManager.isOpen()) {
//            Log.w("w", "initCamera（）虽然已经打开 - 晚期SurfaceView回调？");
//            return;
//        }
//        try {
//            cameraManager.openDriver(surfaceHolder);
//            // Creating the handler starts the preview, which can also throw a RuntimeException.
//            if (handler == null) {
//                handler = new CaptureActivityHandler(this, decodeFormats, decodeHints, characterSet, cameraManager);
//            }
//            decodeOrStoreSavedBitmap(null, null);
//        } catch (IOException ioe) {
//            Log.w(TAG, ioe);
//            displayFrameworkBugMessageAndExit();
//        } catch (RuntimeException e) {
//            // Barcode Scanner has seen crashes in the wild of this variety:
//            // java.?lang.?RuntimeException: Fail to connect to camera service
//            Log.w(TAG, "Unexpected error initializing camera", e);
//            displayFrameworkBugMessageAndExit();
//        }
//    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {

    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

    }
}
