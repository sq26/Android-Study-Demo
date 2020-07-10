package com.sq26.experience.ui.activity.file;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.documentfile.provider.DocumentFile;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.sq26.experience.R;
import com.sq26.experience.adapter.RecyclerViewListAdapter;
import com.sq26.experience.adapter.ViewHolder;
import com.sq26.experience.util.FileUtil;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FileOperateActivity extends AppCompatActivity {
    //父文件夹列表视图
    @BindView(R.id.rootRecyclerView)
    RecyclerView parentRecyclerView;
    //文件列表视图
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    //父文件夹列表适配器
    private RecyclerViewListAdapter<DocumentFile> parentFileListAdapter;
    //父文件夹列表
    private List<DocumentFile> parentFileList = new ArrayList<>();
    //文件列表适配器
    private RecyclerViewListAdapter<DocumentFile> fileListAdapter;
    //文件列表
    private List<DocumentFile> fileList = new ArrayList<>();
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
        parentFileListAdapter = new RecyclerViewListAdapter<DocumentFile>(R.layout.item_parent_file, parentFileList) {
            @Override
            public void bindViewHolder(ViewHolder viewHolder, DocumentFile item, int position) {
                viewHolder.setText(R.id.text, item.getName());
                viewHolder.setOnClickListener(R.id.text, view -> {
                    parentFileList.subList(position + 1, parentFileList.size()).clear();
                    notifyDataSetChanged();
                    initData(item.listFiles());
                });
            }
        };
        parentRecyclerView.setAdapter(parentFileListAdapter);

        //文件列表适配器
        fileListAdapter = new RecyclerViewListAdapter<DocumentFile>(R.layout.item_file, fileList) {
            @Override
            public void bindViewHolder(ViewHolder viewHolder, DocumentFile item, int position) {
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
                            FileUtil.openFile(context, item.getUri());
                        } else {
                            parentFileList.add(item);
                            parentFileListAdapter.notifyDataSetChanged();
                            initData(item.listFiles());
                        }
                    }
                });
                viewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        new AlertDialog.Builder(FileOperateActivity.this)
                                .setMessage("是否删除")
                                .setPositiveButton("是", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Log.d("删除", "有权限");
                                        item.delete();
                                        fileList.remove(item);
                                        fileListAdapter.notifyDataSetChanged();
                                    }
                                }).show();
                        return true;
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
        //判断是否是绝对路径
        if (FileUtil.isAbsolutePath(rootPath)) {
            //是绝对路径,要先转成File类
            documentFile = DocumentFile.fromFile(new File(rootPath));
        } else {
            //不是绝对路径
            documentFile = DocumentFile.fromTreeUri(context, Uri.parse(rootPath));
        }
        parentFileList.add(documentFile);
        parentFileListAdapter.notifyDataSetChanged();
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

    @Override
    public void onBackPressed() {
        if (parentFileList.size() > 1) {
            parentFileList.remove(parentFileList.size() - 1);
            parentFileListAdapter.notifyDataSetChanged();
            initData(parentFileList.get(parentFileList.size() - 1).listFiles());
        } else {
            super.onBackPressed();
        }
    }

}
