package com.sq26.experience.ui.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;
import com.sq26.experience.R;
import com.sq26.experience.adapter.RecyclerViewListAdapter;
import com.sq26.experience.adapter.ViewHolder;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AppManagementActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.search)
    TextInputEditText search;
    @BindView(R.id.systemSwitch)
    Switch systemSwitch;
    private Context context;
    private String sourceDir;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_management);
        ButterKnife.bind(this);
        context = this;
        systemSwitch.setTag(true);
        List<PackageInfo> list = getPackageManager().getInstalledPackages(0);
        systemSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                systemSwitch.setTag(isChecked);
            }
        });


        RecyclerViewListAdapter<PackageInfo> recyclerViewListAdapter = new RecyclerViewListAdapter<PackageInfo>(R.layout.item_app_info, list) {
            @Override
            protected void bindViewHolder(ViewHolder viewHolder, PackageInfo item, int position) {
                String label = item.applicationInfo.loadLabel(getPackageManager()).toString();
                ImageView imageView = viewHolder.getView(R.id.icon);
                Drawable drawable = item.applicationInfo.loadIcon(getPackageManager());
                imageView.setImageDrawable(drawable);
                StringBuilder stringBuffer = new StringBuilder();
                stringBuffer.append(label);
                stringBuffer.append("\n");
                stringBuffer.append("包名:");
                stringBuffer.append(item.packageName);
                stringBuffer.append("\n");
                stringBuffer.append("版本名:");
                stringBuffer.append(item.versionName);
                stringBuffer.append("\n");
                stringBuffer.append("版本号:");
                stringBuffer.append(item.versionCode);
                stringBuffer.append("\n");
                stringBuffer.append("apk:");
                stringBuffer.append(item.applicationInfo.sourceDir);
                stringBuffer.append("\n");
                if ((item.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                    stringBuffer.append("用户应用");
                } else {
                    stringBuffer.append("系统应用");
                    if (!(boolean) systemSwitch.getTag()) {
//                        notifyItemRemoved(position);
                    }
                }
                viewHolder.setText(R.id.text, stringBuffer.toString());
                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new AlertDialog.Builder(context)
                                .setItems(new String[]{"把apk保存在指定位置"}, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        switch (which) {
                                            case 0:
                                                sourceDir = item.applicationInfo.sourceDir;
                                                Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
                                                intent.addCategory(Intent.CATEGORY_OPENABLE);
                                                intent.setType("application/apk");
                                                intent.putExtra(Intent.EXTRA_TITLE, item.applicationInfo.loadLabel(getPackageManager()) + ".apk");
//                                                intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, pickerInitialUri);
                                                startActivityForResult(intent, 1);
                                                break;
                                        }
                                    }
                                }).show();
                    }
                });
            }
        };
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        recyclerView.setAdapter(recyclerViewListAdapter);


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 1) {
                Uri apkUri = data.getData();
//                DocumentFile documentFile = DocumentFile.fromSingleUri(context, apkUri);
                try {
                    OutputStream outputStream = getContentResolver().openOutputStream(apkUri);
                    FileInputStream fileInputStream = new FileInputStream(new File(sourceDir));
                    byte[] bytes = new byte[1024];
                    int index;
                    while ((index = fileInputStream.read(bytes)) != -1) {
                        outputStream.write(bytes, 0, index);
                        outputStream.flush();
                    }
                    outputStream.close();
                    fileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Toast.makeText(context, "拷贝完成", Toast.LENGTH_LONG);
            }
        }
    }
}