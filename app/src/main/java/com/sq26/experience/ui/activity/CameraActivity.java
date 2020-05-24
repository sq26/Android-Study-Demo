package com.sq26.experience.ui.activity;

import android.content.Context;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.Size;
import android.view.TextureView;

import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.sq26.experience.R;
import com.sq26.experience.util.SharedPreferencesUtil;
import com.sq26.experience.util.permissions.JPermissions;
import com.sq26.experience.util.permissions.PermissionUtil;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CameraActivity extends AppCompatActivity {


    @BindView(R.id.textureView)
    TextureView textureView;
    //相机服务
    private CameraManager cameraManager;
    //相机设备
    private CameraDevice cameraDevice;
    //相机id
    private String cameraId = "";
    //上下文
    private Context context;
    //相机特性
    private CameraCharacteristics cameraCharacteristics = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        ButterKnife.bind(this);
        context = this;
        initView();
    }

    private void initView() {
        textureView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
            @Override
            public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int i, int i1) {
                //当TextureView准备使用SurfaceTexture 时调用
                requestPermissions();
            }

            @Override
            public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int i, int i1) {
                //SurfaceTexture的缓冲区大小更改时调用。
            }

            @Override
            public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
                //当指定的SurfaceTexture对象即将被销毁时调用。
                //如果返回true，则调用此方法后，表面纹理内不应进行任何渲染。如果返回false，则需要程序调用surfaceTexture.release();来释放,大多数应用程序应返回true。

                return true;
            }

            @Override
            public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
                //SurfaceTexture通过更新指定的值 时调用SurfaceTexture#updateTexImage()。

            }
        });
    }

    private void requestPermissions() {
        //申请相机权限
        JPermissions.init(context)
                .permissions(PermissionUtil.Group.CAMERA)
                .success(new JPermissions.SuccessCallback() {
                    @Override
                    public void success() {
                        //授权成功,初始化相机
                        initCamera();
                    }
                })
                .failure(new JPermissions.FailureCallback() {
                    @Override
                    public void failure(String[] successArray, String[] failureArray, String[] noPromptArray) {

                    }
                }).start();
    }

    private void initCamera() {
        //获取相机服务
        cameraManager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
        //判断相机服务不为null
        if (cameraManager != null) {
            //获取上次使用的相机id
            cameraId = SharedPreferencesUtil.getString(context, "cameraId");
            //判断如果之前有没有使用过相机
            if (cameraId.isEmpty()) {
                //没有使用
                try {
                    //判断是否有相机
                    if (cameraManager.getCameraIdList().length > 0) {
                        //有相机,取第一个相机id
                        cameraId = cameraManager.getCameraIdList()[0];
                        //初始化相机参数
                        initCameraParams();
                    } else {
                        //这个设备没有相机
                        Log.d("Camera", "这个设备没有相机");
                    }
                } catch (CameraAccessException e) {
                    e.printStackTrace();
                    //获取相机列表失败
                    Log.d("Camera", "获取相机列表失败");
                }
            } else {
                //使用过初始化相机参数
                initCameraParams();
            }
        } else {
            //无法使用相机服务
            Log.d("Camera", "无法使用相机服务");
        }
    }

    //初始化相机参数
    private void initCameraParams() {
        //CameraCharacteristics用于查询相机特性
        cameraCharacteristics = null;
        try {
            //根据cameraId获取
            cameraCharacteristics = cameraManager.getCameraCharacteristics(cameraId);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
        if (cameraCharacteristics != null) {
            //相机api支持的级别
            Integer INFO_SUPPORTED_HARDWARE_LEVEL = cameraCharacteristics.get(CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL);
            //分为5个等级,不管是那个等级都无法使用所有的api,最好是单独查询
            if (INFO_SUPPORTED_HARDWARE_LEVEL != null)
                switch (INFO_SUPPORTED_HARDWARE_LEVEL) {
                    case CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_LEGACY:
                        Log.d("level", "设备在较旧的Android设备上以向后兼容模式运行，并且功能非常有限。");
                        break;
                    case CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_LIMITED:
                        Log.d("level", "设备代表基准功能集，并且还可能包含作为的子集的其他功能");
                        break;
                    case CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_FULL:
                        //设备还支持对传感器，闪光灯，镜头和后处理设置进行逐帧手动控制，以及高速率的图像捕获。
                        Log.d("level", "全方位的硬件支持，允许手动控制全高清的摄像、支持连拍模式以及其他新特性。");
                        break;
                    case CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_3:
                        //设备还支持YUV重新处理和RAW图像捕获，以及其他输出流配置。
                        break;
                    case CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_EXTERNAL:
                        //设备类似于LIMITED设备，但有一些例外，例如未报告某些传感器或镜头信息或帧速率较不稳定
                        break;
                }

            for (CameraCharacteristics.Key<?> key : cameraCharacteristics.getKeys())
                if (key.getName().equals(CameraCharacteristics.LENS_FACING.getName())) {
                    Log.d("level", "支持摄像头翻方向");
                    init_LENS_FACING();
                } else if (key.getName().equals(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP.getName())) {
                    init_SCALER_STREAM_CONFIGURATION_MAP();
                }
        }


    }

    //初始化摄像头方向设置和信息
    private void init_LENS_FACING() {
        //摄像头的方向
        Integer lensFacing = cameraCharacteristics.get(CameraCharacteristics.LENS_FACING);
        if (lensFacing != null)
            switch (lensFacing) {
                case CameraCharacteristics.LENS_FACING_FRONT:
                    Log.d("lensFacing", "前置摄像头");
                    break;
                case CameraCharacteristics.LENS_FACING_BACK:
                    Log.d("lensFacing", "后置摄像头");
                    break;

            }
    }

    //初始化摄像头拍照方向设置和信息
    private void init_SENSOR_ORIENTATION() {
        //摄像头拍照方向。
        //顺时针旋转的度数；总是90的倍数
        //有效值范围：0、90、180、270
        Integer sensorOrientation = cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION);
        Log.d("sensorOrientation", "摄像头拍照方向" + sensorOrientation);
    }

    //初始化是否支持闪光灯设置和信息
    private void init_FLASH_INFO_AVAILABLE() {
        //是否支持闪光灯。
        Boolean flashInfoAvailable = cameraCharacteristics.get(CameraCharacteristics.FLASH_INFO_AVAILABLE);
        if (flashInfoAvailable != null)
            if (flashInfoAvailable)
                Log.d("lensFacing", "闪光灯可用");
            else
                Log.d("lensFacing", "闪光灯不可用");
        else
            Log.d("lensFacing", "闪光灯不可用");

    }

    //初始化该摄像头设备支持的可用流配置；还包括每种格式/尺寸组合的最小帧时长和停顿时长。
    private void init_SCALER_STREAM_CONFIGURATION_MAP() {
        //获取支持的可用流配置
        StreamConfigurationMap scalerStreamConfigurationMap = cameraCharacteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
        if (scalerStreamConfigurationMap != null) {
            int[] outputFormats = scalerStreamConfigurationMap.getOutputFormats();
            for (int format : outputFormats) {
                switch (format) {
                    //Android密集深度图像格式。
                    case ImageFormat.DEPTH16://1144402265
                        Log.d("format", "DEPTH16");
                        break;
                    // 深度增强的压缩JPEG格式。
                    case ImageFormat.DEPTH_JPEG://1768253795
                        Log.d("format", "DEPTH_JPEG");
                        break;
                    // Android稀疏深度点云格式。
                    case ImageFormat.DEPTH_POINT_CLOUD://257
                        Log.d("format", "DEPTH_POINT_CLOUD");
                        break;
                    // 多平面Android RGBA格式
                    //此格式是通用RGBA格式，能够描述大多数RGBA格式，每个颜色样本有8位。
                    case ImageFormat.FLEX_RGBA_8888://42
                        Log.d("format", "FLEX_RGBA_8888");
                        break;
                    // 多平面Android RGB格式
                    //此格式是通用RGB格式，能够描述大多数RGB格式，每个颜色样本有8位。
                    case ImageFormat.FLEX_RGB_888://41
                        Log.d("format", "FLEX_RGB_888");
                        break;
                    // 压缩的HEIC格式。
                    case ImageFormat.HEIC://1212500294
                        Log.d("format", "HEIC");
                        break;
                    //压缩的JPEG格式。
                    case ImageFormat.JPEG://256
                        Log.d("format", "JPEG");
                        break;
                    // YCbCr格式，用于视频。
                    case ImageFormat.NV16://16
                        Log.d("format", "NV16");
                        break;
                    // 用于图像的YCrCb格式，使用NV21编码格式。
                    case ImageFormat.NV21://17
                        Log.d("format", "NV21");
                        break;
                    //Android私有不透明图像格式。
                    case ImageFormat.PRIVATE://34
                        Log.d("format", "PRIVATE");
                        break;
                    // Android 10位原始格式
                    //这是一种单平面，每像素10位，密集包装（每行）且未经处理的格式，通常表示来自图像传感器的原始Bayer图案图像。
                    case ImageFormat.RAW10://37
                        Log.d("format", "RAW10");
                        break;
                    // Android 12位原始格式
                    //这是一种单平面，每像素12位，密集包装（每行）且未经处理的格式，通常表示来自图像传感器的原始Bayer图案图像。
                    case ImageFormat.RAW12://38
                        Log.d("format", "RAW12");
                        break;
                    // 专用的原始相机传感器图像格式，具有实现依赖像素布局的单通道图像。
                    case ImageFormat.RAW_PRIVATE://36
                        Log.d("format", "RAW_PRIVATE");
                        break;
                    // 一般原始相机传感器的图像格式，通常代​​表单通道拜耳马赛克图像。
                    case ImageFormat.RAW_SENSOR://32
                        Log.d("format", "RAW_SENSOR");
                        break;
                    // 用于编码为RGB_565的图片的RGB格式。
                    case ImageFormat.RGB_565://4
                        Log.d("format", "RGB_565");
                        break;
                    // 未知
                    case ImageFormat.UNKNOWN://0
                        Log.d("format", "UNKNOWN");
                        break;
                    // Android Y8格式。
                    case ImageFormat.Y8://538982489
                        Log.d("format", "Y8");
                        break;
                    // 多平面Android YUV 420格式
                    //此格式是通用的YCbCr格式，能够描述任何4：2：0色度采样的平面或半平面缓冲区（但不完全交织），每个颜色样本有8位。
                    case ImageFormat.YUV_420_888://35
                        Log.d("format", "YUV_420_888");
                        break;
                    //多平面Android YUV 422格式
                    //此格式是通用的YCbCr格式，能够描述任何4：2：2色度二次采样（平面，半平面或交错）格式，每个颜色样本有8位。
                    case ImageFormat.YUV_422_888://39
                        Log.d("format", "YUV_422_888");
                        break;
                    //多平面Android YUV 444格式
                    //此格式是通用的YCbCr格式，能够描述任何4：4：4（平面，半平面或交错）格式，每个颜色样本有8位。
                    case ImageFormat.YUV_444_888://40
                        Log.d("format", "YUV_444_888");
                        break;
                    //用于图像的YCbCr格式，使用YUYV（YUY2）编码格式。
                    case ImageFormat.YUY2://20
                        Log.d("format", "YUY2");
                        break;
                    //Android YUV格式
                    case ImageFormat.YV12://842094169
                        Log.d("format", "YV12");
                        break;
                }

                Size[] sizes = scalerStreamConfigurationMap.getOutputSizes(format);
                for (Size size : sizes) {
                    Log.d(size.getWidth() + "", size.getHeight() + "");
                }
            }
            Log.d("SurfaceTexture", "----------------------------------------------");
            Size[] sizes = scalerStreamConfigurationMap.getOutputSizes(SurfaceTexture.class);
            for (Size size : sizes) {
                Log.d(size.getWidth() + "", size.getHeight() + "");
            }
        }
    }
}
