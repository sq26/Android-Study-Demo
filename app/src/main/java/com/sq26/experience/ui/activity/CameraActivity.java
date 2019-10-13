package com.sq26.experience.ui.activity;

import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.TextureView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.sq26.experience.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CameraActivity extends AppCompatActivity {


    @BindView(R.id.textureView)
    TextureView textureView;

    private CameraManager cameraManager;
    private CameraDevice mCameraDevice;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        ButterKnife.bind(this);

        cameraManager = (CameraManager) getSystemService(CAMERA_SERVICE);

        try {
            assert cameraManager != null;
            for (String id : cameraManager.getCameraIdList()) {
                CameraCharacteristics cameraCharacteristics = cameraManager.getCameraCharacteristics(id);
                Log.d("id", id);
                //支持的级别
                int level = cameraCharacteristics.get(CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL);
                Log.d("type", "支持的级别" + level);
                switch (level) {
                    case CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_FULL:
                        Log.d("level", "全方位的硬件支持，允许手动控制全高清的摄像、支持连拍模式以及其他新特性。");
                        break;
                    case CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_LIMITED:
                        Log.d("level", "有限支持，这个需要单独查询。");
                        break;
                    case CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_LEGACY:
                        Log.d("level", "所有设备都会支持，也就是和过时的Camera API支持的特性是一致的。");
                        break;
                }
                //摄像头的方向
                int lensFacing = cameraCharacteristics.get(CameraCharacteristics.LENS_FACING);
                switch (lensFacing) {
                    case CameraCharacteristics.LENS_FACING_FRONT:
                        Log.d("lensFacing", "前置摄像头");
                        break;
                    case CameraCharacteristics.LENS_FACING_BACK:
                        Log.d("lensFacing", "后置摄像头");
                        break;

                }
                //摄像头拍照方向。
                int sensorOrientation = cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION);
                Log.d("sensorOrientation", "摄像头拍照方向" + sensorOrientation);
                //是否支持闪光灯。
                boolean flashInfoAvailable = cameraCharacteristics.get(CameraCharacteristics.FLASH_INFO_AVAILABLE);
                if (flashInfoAvailable)
                    Log.d("lensFacing", "闪光灯可用");
                else
                    Log.d("lensFacing", "闪光灯不可用");

            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
        textureView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
            @Override
            public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int i, int i1) {

            }

            @Override
            public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int i, int i1) {

            }

            @Override
            public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
                return false;
            }

            @Override
            public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {

            }
        });
    }

}
