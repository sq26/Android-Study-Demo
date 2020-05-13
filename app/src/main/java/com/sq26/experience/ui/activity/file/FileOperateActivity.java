package com.sq26.experience.ui.activity.file;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.documentfile.provider.DocumentFile;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.facebook.drawee.view.SimpleDraweeView;
import com.sq26.experience.R;
import com.sq26.experience.adapter.RecyclerViewListAdapter;
import com.sq26.experience.adapter.ViewHolder;
import com.sq26.experience.util.AppUtil;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FileOperateActivity extends AppCompatActivity {
    //文件列表视图
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    //文件列表适配器
    private RecyclerViewListAdapter<DocumentFile> fileListAdapter;
    //文件列表
    private List<DocumentFile> fileList = new ArrayList<>();
    //已有授权的路径和url的键值对
    private JSONObject rootPathAndUriMap = new JSONObject();
    //上下文
    private Context context;
    //其他储存的写权限申请码
    private final int DOCUMENT_TREE_CODE = 0;
    //主储存的根目录路径
    private String rootPath = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_management);
        ButterKnife.bind(this);
        context = this;
        //获取根目录路径
        rootPath = getIntent().getStringExtra("rootPath");
        //获取所有已有授权的路径和url的键值对
        String rootPathAndUriMapJson = AppUtil.loadPrivateFile(context, "rootPathAndUriMap.json");
        //判断有没有保存过键值对
        if (!rootPathAndUriMapJson.isEmpty())
            //设置到私有对象
            rootPathAndUriMap.putAll(JSON.parseObject(rootPathAndUriMapJson));
        //初始化视图
        initView();

        Log.d("DIRECTORY_MUSIC", getExternalFilesDir(Environment.DIRECTORY_MUSIC).getAbsolutePath());
        Log.d("DIRECTORY_PODCASTS", getExternalFilesDir(Environment.DIRECTORY_PODCASTS).getAbsolutePath());
        Log.d("DIRECTORY_RINGTONES", getExternalFilesDir(Environment.DIRECTORY_RINGTONES).getAbsolutePath());
        Log.d("DIRECTORY_ALARMS", getExternalFilesDir(Environment.DIRECTORY_ALARMS).getAbsolutePath());
        Log.d("DIRECTORY_NOTIFICATIONS", getExternalFilesDir(Environment.DIRECTORY_NOTIFICATIONS).getAbsolutePath());
        Log.d("DIRECTORY_PICTURES", getExternalFilesDir(Environment.DIRECTORY_PICTURES).getAbsolutePath());
        Log.d("DIRECTORY_MOVIES", getExternalFilesDir(Environment.DIRECTORY_MOVIES).getAbsolutePath());
        Log.d("NULL", getExternalFilesDir(null).getAbsolutePath());
    }

    private void initView() {
        //文件列表适配器
        fileListAdapter = new RecyclerViewListAdapter<DocumentFile>(R.layout.item_file, fileList) {
            @Override
            protected void createViewHolder(ViewHolder viewHolder, DocumentFile item, int position) {
                viewHolder.setText(R.id.name, item.getName());
                viewHolder.setTextColor(R.id.name, item.canWrite() ? Color.GREEN : Color.RED);
                viewHolder.setText(R.id.dateTime, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(item.lastModified()));
                SimpleDraweeView simpleDraweeView = viewHolder.getView(R.id.simpleDraweeView);
                if (item.isFile()) {
                    simpleDraweeView.setImageResource(R.drawable.ic_insert_drive_file_black_24dp);
                    viewHolder.setText(R.id.remark, Formatter.formatFileSize(context, item.length()));
                } else {
                    simpleDraweeView.setImageResource(R.drawable.ic_folder_open_black_24dp);
                    viewHolder.setText(R.id.remark, item.listFiles().length + "个项目");
                }
                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (item.isFile()) {
                            new AlertDialog.Builder(FileOperateActivity.this)
                                    .setMessage("是否删除")
                                    .setPositiveButton("是", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            if (item.canWrite()) {
                                                Log.d("删除", "有权限");
                                                item.delete();
                                                fileList.remove(item);
                                                fileListAdapter.notifyDataSetChanged();
                                            } else {
                                                showIsSelectDirectory();
                                            }
                                        }
                                    }).show();
                        } else {
                            initData(item.listFiles());
                        }
                    }
                });
            }
        };
        //设置分割线
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        //设置适配器
        recyclerView.setAdapter(fileListAdapter);
        //声明一个DocumentFile对象
        DocumentFile documentFile;
        //判断根目录是否有授权记录
        if (rootPathAndUriMap.containsKey(rootPath)) {
            //有授权记录,就根据根目录路径获取根目录uri对象,使用uri对象获取DocumentFile对象
            //使用有授权的uri对象创建的DocumentFile拥有读写权限
            documentFile = DocumentFile.fromTreeUri(context, Uri.parse(rootPathAndUriMap.getString(rootPath)));
        } else {
            //没有授权就使用file对象获取DocumentFile对象
            //主储存使用file对象创建的DocumentFile对象是拥有读写权限的
            //非主储存使用file对象创建的DocumentFile对象只拥有读权限,没有写权限
            documentFile = DocumentFile.fromFile(new File(rootPath));
        }
        //初始化数据
        initData(documentFile.listFiles());
    }

    private void initData(DocumentFile[] documentFiles) {
        //先清理空文件列表
        fileList.clear();
        //直接将DocumentFile数值转化成DocumentFile列表,全部添加进文件列表
        fileList.addAll(Arrays.asList(documentFiles));
        //刷新文件列表视图
        fileListAdapter.notifyDataSetChanged();
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

    private void showIsSelectDirectory() {
        new AlertDialog.Builder(context)
                .setMessage("请选择要操作文件的根目录!")
                .setPositiveButton("去选择", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
                        //标识同时获取其子目录的读写权限
                        intent.addFlags(Intent.FLAG_GRANT_PREFIX_URI_PERMISSION);
                        startActivityForResult(intent, DOCUMENT_TREE_CODE);
                    }
                }).setNegativeButton("取消", null)
                .show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void initSaveRootPathAndUriMap(Uri treeUri) {
        DocumentFile documentFile = DocumentFile.fromTreeUri(context, treeUri);
        JSONArray treeUriArray = new JSONArray();
        for (DocumentFile df : documentFile.listFiles()) {
            treeUriArray.add(df.getName());
        }
        //type值是获取的文件夹的名字,null指应用文件的根目录,/storage/emulated/0/Android/data/com.sq26.experience/files
        File file = new File(rootPath);
        JSONArray fileArray = new JSONArray();
        for (File f : file.listFiles()) {
            fileArray.add(f.getName());
        }
        if (treeUriArray.containsAll(fileArray)) {
            rootPathAndUriMap.put(rootPath, treeUri.toString());
            AppUtil.savePrivateFile(context, "rootPathAndUriMap.json", rootPathAndUriMap.toJSONString());
        } else {
            showIsSelectDirectory();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case DOCUMENT_TREE_CODE:
                    //授予打开的文档树永久性的读写权限
                    getContentResolver().takePersistableUriPermission(data.getData(), Intent.FLAG_GRANT_READ_URI_PERMISSION |
                            Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                    initSaveRootPathAndUriMap(data.getData());
                    break;
            }
        }
    }
}
