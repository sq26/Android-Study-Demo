package com.sq26.experience.ui.activity.file;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.format.Formatter;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.documentfile.provider.DocumentFile;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.sq26.experience.R;
import com.sq26.experience.adapter.CommonAdapter;
import com.sq26.experience.adapter.ViewHolder;
import com.sq26.experience.util.AppUtil;
import com.sq26.experience.util.permissions.JPermissions;
import com.sq26.experience.util.permissions.PermissionUtil;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class FileHomeActivity extends AppCompatActivity {
    //根目录列表视图
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    //根目录列表视图
    @BindView(R.id.rootFileRecyclerView)
    RecyclerView rootFileRecyclerView;
    //根目录数组
    private JSONArray rootFileArray = new JSONArray();
    //根目录列表视图适配器
    private CommonAdapter rootFileAdapter;
    //上下文
    private Context context;
    //其他储存的写权限申请码
    final private int DOCUMENT_TREE_CODE = 0;

    private JSONArray authorizeUriArray = new JSONArray();

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
        toolbar.setTitle(getString(R.string.file_management));
        setSupportActionBar(toolbar);
        //创建分区列表适配器
        rootFileAdapter = new CommonAdapter(R.layout.item_file_hoem_root, rootFileArray) {
            @Override
            public void bindViewHolder(ViewHolder viewHolder, JSONObject jsonObject, int position, Object payload) {
                //设置分区名称
                viewHolder.setText(R.id.name, jsonObject.getString("name"));
                //设置分区储存容量
                viewHolder.setText(R.id.rom, jsonObject.getString("rom"));
                //设置分区类型
                viewHolder.setText(R.id.path, jsonObject.getString("path"));
                //是否能读
                viewHolder.setText(R.id.canRead, jsonObject.getString("canRead"));
                //是否能写
                viewHolder.setText(R.id.canWrite, jsonObject.getString("canWrite"));
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

    @OnClick({R.id.image, R.id.video, R.id.music})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.image:
                startActivity(new Intent(this, FileImageActivity.class));
                break;
            case R.id.video:
                break;
            case R.id.music:
                break;
        }
    }

    private void initDate() {

        //type值是获取的文件夹的名字,null指应用文件的根目录,/storage/emulated/0/Android/data/com.sq26.experience/files
        File[] files = getExternalFilesDirs(null);
        rootFileArray.clear();
        //遍历分区
        for (int i = 0; i < files.length; i++) {
            //获取根目录file对象,在Android设备装载的储存设备必然会在根目录创建一个名为Android 的文件夹,通过截取Android关键字获取根目录
            DocumentFile documentFile = DocumentFile.fromFile(files[i]);
            //新建一个json对象,分区信息
            JSONObject jsonObject = new JSONObject();
            //获取分区名称
            jsonObject.put("name", documentFile.getName());
            //获取已用大小和总大小
            jsonObject.put("rom", Formatter.formatFileSize(context, documentFile.length()));
            //获取绝对路径
            jsonObject.put("path", files[i].getAbsolutePath());
            //获取是否有读权限
            jsonObject.put("canRead", documentFile.canRead());
            //获取是否有写权限
            jsonObject.put("canWrite", documentFile.canWrite());
            //添加入储存空间列表
            rootFileArray.add(jsonObject);
        }

        //获取所有已有授权的路径和url的键值对
        String authorizeUriArrayJson = AppUtil.loadPrivateFile(context, "authorizeUriArrayJson.json");
        //判断有没有保存过键值对
        if (!authorizeUriArrayJson.isEmpty()) {
            //设置到私有对象
            authorizeUriArray.clear();
            authorizeUriArray.addAll(JSON.parseArray(authorizeUriArrayJson));
        }

        for (String urlString : authorizeUriArray.toJavaList(String.class)) {
            DocumentFile documentFile = DocumentFile.fromTreeUri(this, Uri.parse(urlString));
            //新建一个json对象,分区信息
            JSONObject jsonObject = new JSONObject();
            //获取分区名称
            jsonObject.put("name", documentFile.getName());
            //获取已用大小和总大小
            jsonObject.put("rom", Formatter.formatFileSize(context, documentFile.length()));
            //获取绝对路径
            jsonObject.put("path", documentFile.getUri().toString());
            //获取是否有读权限
            jsonObject.put("canRead", documentFile.canRead());
            //获取是否有写权限
            jsonObject.put("canWrite", documentFile.canWrite());
            //添加入储存空间列表
            rootFileArray.add(jsonObject);
        }
        //刷新分区列表
        rootFileAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == DOCUMENT_TREE_CODE) {//授予打开的文档树永久性的读写权限
                getContentResolver().takePersistableUriPermission(data.getData(), Intent.FLAG_GRANT_READ_URI_PERMISSION |
                        Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

                authorizeUriArray.add(data.getData().toString());
                AppUtil.savePrivateFile(context, "authorizeUriArrayJson.json", authorizeUriArray.toJSONString());
                initDate();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.action_add:
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
                //标识同时获取其子目录的读写权限
                intent.addFlags(Intent.FLAG_GRANT_PREFIX_URI_PERMISSION);
                startActivityForResult(intent, DOCUMENT_TREE_CODE);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}
