package com.sq26.experience.ui.activity;

import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.facebook.drawee.view.SimpleDraweeView;
import com.sq26.experience.R;
import com.sq26.experience.adapter.CommonAdapter;
import com.sq26.experience.adapter.RecyclerViewAdapter;
import com.sq26.experience.adapter.ViewHolder;
import com.sq26.experience.util.FileUtil;

import java.io.File;
import java.text.SimpleDateFormat;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FileManagementActivity extends AppCompatActivity {

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.rootPath)
    TextView rootPath;

    private CommonAdapter commonAdapter;
    private JSONArray fileArray = new JSONArray();
    private String[] rootStringPaths;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_management);
        ButterKnife.bind(this);

        init();
        Log.d("DIRECTORY_ALARMS", getExternalFilesDir(Environment.DIRECTORY_ALARMS).getAbsolutePath());
//        Log.d("DIRECTORY_AUDIOBOOKS",getExternalFilesDir(Environment.DIRECTORY_AUDIOBOOKS).getAbsolutePath());
        Log.d("DIRECTORY_DCIM", getExternalFilesDir(Environment.DIRECTORY_DCIM).getAbsolutePath());
        Log.d("DIRECTORY_DOCUMENTS", getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS).getAbsolutePath());
        Log.d("DIRECTORY_DOWNLOADS", getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath());
        Log.d("DIRECTORY_MOVIES", getExternalFilesDir(Environment.DIRECTORY_MOVIES).getAbsolutePath());
        Log.d("DIRECTORY_MUSIC", getExternalFilesDir(Environment.DIRECTORY_MUSIC).getAbsolutePath());
        Log.d("DIRECTORY_NOTIFICATIONS", getExternalFilesDir(Environment.DIRECTORY_NOTIFICATIONS).getAbsolutePath());
        Log.d("DIRECTORY_PICTURES", getExternalFilesDir(Environment.DIRECTORY_PICTURES).getAbsolutePath());
        Log.d("DIRECTORY_PODCASTS", getExternalFilesDir(Environment.DIRECTORY_PODCASTS).getAbsolutePath());
        Log.d("DIRECTORY_RINGTONES", getExternalFilesDir(Environment.DIRECTORY_RINGTONES).getAbsolutePath());
//        Log.d("DIRECTORY_SCREENSHOTS",getExternalFilesDir(Environment.DIRECTORY_SCREENSHOTS).getAbsolutePath());

        Log.d("MEDIA_BAD_REMOVAL", getExternalFilesDir(Environment.MEDIA_BAD_REMOVAL).getAbsolutePath());
        Log.d("MEDIA_CHECKING", getExternalFilesDir(Environment.MEDIA_CHECKING).getAbsolutePath());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Log.d("MEDIA_EJECTING", getExternalFilesDir(Environment.MEDIA_EJECTING).getAbsolutePath());
        }
        Log.d("MEDIA_MOUNTED", getExternalFilesDir(Environment.MEDIA_MOUNTED).getAbsolutePath());
        Log.d("MEDIA_MOUNTED_READ_ONLY", getExternalFilesDir(Environment.MEDIA_MOUNTED_READ_ONLY).getAbsolutePath());
        Log.d("MEDIA_NOFS", getExternalFilesDir(Environment.MEDIA_NOFS).getAbsolutePath());
        Log.d("MEDIA_REMOVED", getExternalFilesDir(Environment.MEDIA_REMOVED).getAbsolutePath());
        Log.d("MEDIA_SHARED", getExternalFilesDir(Environment.MEDIA_SHARED).getAbsolutePath());
        Log.d("MEDIA_UNKNOWN", getExternalFilesDir(Environment.MEDIA_UNKNOWN).getAbsolutePath());
        Log.d("MEDIA_UNMOUNTABLE", getExternalFilesDir(Environment.MEDIA_UNMOUNTABLE).getAbsolutePath());
        Log.d("MEDIA_UNMOUNTED", getExternalFilesDir(Environment.MEDIA_UNMOUNTED).getAbsolutePath());
    }

    private void init() {
        commonAdapter = new CommonAdapter(R.layout.item_file, fileArray) {
            @Override
            protected void bindViewHolder(ViewHolder viewHolder, JSONObject jsonObject, int position) {
                viewHolder.setText(R.id.name, jsonObject.getString("name"));
                viewHolder.setText(R.id.remark, jsonObject.getString("remark"));
                viewHolder.setText(R.id.dateTime, jsonObject.getString("dateTime"));
                SimpleDraweeView simpleDraweeView = viewHolder.getView(R.id.simpleDraweeView);
                simpleDraweeView.setImageResource(jsonObject.getInteger("img"));
            }
        };
        commonAdapter.setOnClick(new RecyclerViewAdapter.OnClick() {
            @Override
            public void click(JSONObject jsonObject, int position) {
                String path = jsonObject.getString("path");
                File file = new File(path);
                if (file.isFile()) {
                    new AlertDialog.Builder(FileManagementActivity.this)
                            .setMessage("是否删除")
                            .setPositiveButton("是", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
//                                    file.delete();
                                }
                            }).show();

                } else {
                    rootPath.setText(path);
                    initData(file.listFiles());
                }
            }
        });
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        recyclerView.setAdapter(commonAdapter);

        File[] files = getExternalFilesDirs(Environment.MEDIA_MOUNTED);
        File[] roots = new File[files.length];
        rootStringPaths = new String[files.length];
        for (int i = 0; i < files.length; i++) {
            rootStringPaths[i] = files[i].getAbsolutePath().substring(0, files[i].getAbsolutePath().indexOf("Android") - 1);
            roots[i] = new File(rootStringPaths[i]);
        }
        initData(roots);
    }

    private void initData(File[] files) {
        fileArray.clear();
        JSONObject fileJsonObject;
        for (File f : files) {
            fileJsonObject = new JSONObject();
            fileJsonObject.put("name", f.getName());
            fileJsonObject.put("path", f.getAbsolutePath());
            fileJsonObject.put("dateTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(f.lastModified()));
            if (f.isFile()) {
                fileJsonObject.put("img", R.drawable.ic_insert_drive_file_black_24dp);
                fileJsonObject.put("remark", FileUtil.getFileSizeStr(f.length()));
            } else {
                fileJsonObject.put("img", R.drawable.ic_folder_open_black_24dp);
                if (f.list() != null) {
                    fileJsonObject.put("remark", f.list().length + "个项目");
                } else {
                    Log.d("空数组", fileJsonObject.getString("path"));
                    fileJsonObject.put("remark", "null" + "个项目");
                    continue;
                }
            }
            fileArray.add(fileJsonObject);
        }
        commonAdapter.notifyDataSetChanged();
    }
    //在做android开发对sd操作时，最好是sd卡处于Environment.MEDIA_MOUNTED状态时，对sd卡上的文件进行操作，其他状态不宜进行操作
    //注册StorageEventListener来监听sd卡状态
    //StorageEventListener中有onStorageStateChanged（）方法，当sd卡状态改变时，此方法会调用，对各状态的判断一般会用到Environment类，
    // 此类中包含的有关sd卡状态的常量有：
    //Environment中的各种type
//    Environment.MEDIA_BAD_REMOVAL;//表明SDCard 被卸载前己被移除(用户未到手机设置中手动卸载sd卡，直接拨出之后的状态 )
//    Environment.MEDIA_CHECKING://表明对象正在磁盘检查 
//    Environment.MEDIA_MOUNTED://表明sd对象是存在并具有读/写权限 
//    Environment.MEDIA_MOUNTED_READ_ONLY://表明对象权限为只读 
//    Environment.MEDIA_NOFS://表明对象为空白或正在使用不受支持的文件系统 
//    Environment.MEDIA_REMOVED://如果不存在 SDCard 返回 (用户手动卸载，然后将sd卡从手机取出之后的状态 )
//    Environment.MEDIA_SHARED://如果 SDCard 未安装 ，并通过 USB 大容量存储共享 返回 (手机直接连接到电脑作为u盘使用之后的状态 )
//    Environment.MEDIA_UNMOUNTABLE://返回 SDCard 不可被安装 如果 SDCard 是存在但不可以被安装 (sd卡无法安装)
//    Environment.MEDIA_UNMOUNTED://返回 SDCard 已卸掉如果 SDCard 是存在但是没有被安装 (sd卡已卸载,还在手机上装者,还没有安装)


    private boolean isRootPath(String path) {
        boolean b = false;
        for (String s : rootStringPaths) {
            if (s.equals(path))
                b = true;
        }
        return b;
    }

    @Override
    public void onBackPressed() {
        if (rootPath.getText().toString().isEmpty()) {
            super.onBackPressed();
            return;
        }
        if (isRootPath(rootPath.getText().toString())) {
            super.onBackPressed();
            return;

        }
        File root = new File(rootPath.getText().toString()).getParentFile();
        rootPath.setText(root.getAbsolutePath());
        initData(root.listFiles());

    }
}
