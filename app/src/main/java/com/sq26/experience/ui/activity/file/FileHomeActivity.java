package com.sq26.experience.ui.activity.file;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.storage.StorageManager;
import android.os.storage.StorageVolume;
import android.text.format.Formatter;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.sq26.experience.R;
import com.sq26.experience.adapter.CommonAdapter;
import com.sq26.experience.adapter.ViewHolder;
import com.sq26.experience.util.permissions.JPermissions;
import com.sq26.experience.util.permissions.PermissionUtil;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FileHomeActivity extends AppCompatActivity {
    //根目录列表视图
    @BindView(R.id.rootFileRecyclerView)
    RecyclerView rootFileRecyclerView;
    //根目录数组
    private JSONArray rootFileArray = new JSONArray();
    //根目录列表视图适配器
    private CommonAdapter rootFileAdapter;
    //上下文
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_home);
        ButterKnife.bind(this);
        context = this;
        //初始化界面
        initView();
        //申请储存权限
        JPermissions.init(this)
                .permissions(PermissionUtil.Group.STORAGE)
                .success(new JPermissions.SuccessCallback() {
                    @Override
                    public void success() {
                        //初始化数据
                        initDate();
                    }
                }).start();
    }

    private void initView() {
        //创建分区列表适配器
        rootFileAdapter = new CommonAdapter(R.layout.item_file_hoem_root, rootFileArray) {
            @Override
            protected void bindViewHolder(ViewHolder viewHolder, JSONObject jsonObject, int position) {
                //设置分区名称
                viewHolder.setText(R.id.name, jsonObject.getString("name"));
                //设置分区储存容量
                viewHolder.setText(R.id.rom, jsonObject.getString("rom"));
                //设置分区类型
                viewHolder.setText(R.id.type, jsonObject.getString("type"));
                //设置点击事件
                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //新建打开界面意图
                        Intent intent = new Intent(context, FileOperateActivity.class);
                        //设置根目录路径
                        intent.putExtra("rootPath", jsonObject.getString("path"));
                        //打开文件操作界面
                        startActivity(intent);
                    }
                });
            }
        };
        //设置分割线
        rootFileRecyclerView.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));
        //设置设配器
        rootFileRecyclerView.setAdapter(rootFileAdapter);
    }

    @SuppressLint("UsableSpace")
    private void initDate() {

        //type值是获取的文件夹的名字,null指应用文件的根目录,/storage/emulated/0/Android/data/com.sq26.experience/files
        File[] files = getExternalFilesDirs(null);

        //获取存储管理服务
        StorageManager storageManager = (StorageManager) getSystemService(Context.STORAGE_SERVICE);
        //遍历分区
        for (int i = 0; i < files.length; i++) {
            //获取根目录file对象,在Android设备装载的储存设备必然会在根目录创建一个名为Android 的文件夹,通过截取Android关键字获取根目录
            File file = new File(files[i].getAbsolutePath().substring(0, files[i].getAbsolutePath().indexOf("Android") - 1));
            //新建一个json对象,分区信息
            JSONObject jsonObject = new JSONObject();
            //获取分区名称
            jsonObject.put("name", file.getName());
            //获取已用大小和总大小
            jsonObject.put("rom", Formatter.formatFileSize(context, file.getUsableSpace()) + "/" + Formatter.formatFileSize(context, file.getTotalSpace()));
            //获取绝对路径
            jsonObject.put("path", file.getAbsolutePath());
            //在Android 7.0或以上系统中可以获取StorageVolume对象,用来获取分区信息
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                //用file对象获取StorageVolume分区信息
                StorageVolume storageVolume = storageManager.getStorageVolume(file);
                //获取是否是主分区,主分区读写权限可以直接申请,其他分区必须走Storage Access Framework 访问共享存储,进行文件操控
                //获取是否是可移动储存设备,u盘和sd卡
                jsonObject.put("type", (storageVolume.isPrimary() ? "是" : "不是") + "主要的共享/外部存储," + (storageVolume.isRemovable() ? "是" : "不是") + "可移动的");
                //将分区信息添加进数组
                rootFileArray.add(jsonObject);
            }
        }
        //刷新分区列表
        rootFileAdapter.notifyDataSetChanged();
    }
}
