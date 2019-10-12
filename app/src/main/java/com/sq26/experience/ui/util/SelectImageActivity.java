package com.sq26.experience.ui.util;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.facebook.drawee.view.SimpleDraweeView;
import com.sq26.experience.R;
import com.sq26.experience.adapter.CommonAdapter;
import com.sq26.experience.adapter.RecyclerViewJsonArrayAdapter;
import com.sq26.experience.adapter.ViewHolder;
import com.sq26.experience.util.FileUtils;

import java.util.LinkedHashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SelectImageActivity extends AppCompatActivity {

    @BindView(R.id.pathType)
    TextView pathType;
    @BindView(R.id.arrowDrop)
    SimpleDraweeView arrowDrop;
    @BindView(R.id.selectFolder)
    ConstraintLayout selectFolder;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.imageRecyclerView)
    RecyclerView imageRecyclerView;
    //全局文件夹分类jsonArray
    private JSONArray selectFolderArray = new JSONArray();
    //弹出式窗口
    private PopupWindow selectFolderPopupWindow;
    //用于保存当前文件夹图片的jsonArray
    private JSONArray imageArray = new JSONArray();
    //用于展示当前文件夹图片的适配器
    private CommonAdapter imageAdapter;

    private JSONObject SelectedItem = new JSONObject(new LinkedHashMap<>());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_image);
        ButterKnife.bind(this);
        //初始化界面
        initView();
        //初始化数据
        init();
        //初始化弹出框
        initSelectFolderPopupWindow();
    }

    private void initView() {
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        imageAdapter = new CommonAdapter(R.layout.item_select_image, imageArray) {
            @Override
            protected void bindViewHolder(ViewHolder viewHolder, JSONObject jsonObject, int position) {
                viewHolder.setImageURI(R.id.image, "file://" + jsonObject.getString(MediaStore.Images.Media.DATA));
                if (SelectedItem.containsKey(jsonObject.getString(MediaStore.Images.Media.DATA))) {
                    viewHolder.setText(R.id.count, SelectedItem.getString(jsonObject.getString(MediaStore.Images.Media.DATA)));
                    viewHolder.setBackgroundResource(R.id.count, R.drawable.bg_corners_all);
                } else {
                    viewHolder.setText(R.id.count, "");
                    viewHolder.setBackgroundResource(R.id.count, R.drawable.bg_ring_white);
                }
                viewHolder.setOnClickListener(R.id.count, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (SelectedItem.containsKey(jsonObject.getString(MediaStore.Images.Media.DATA))) {
                            SelectedItem.remove(jsonObject.getString(MediaStore.Images.Media.DATA));
                            int index = 1;
                            for (String s : SelectedItem.keySet()) {
                                SelectedItem.put(s, index);
                                index++;
                            }
                        } else {
                            SelectedItem.put(jsonObject.getString(MediaStore.Images.Media.DATA), (SelectedItem.size() + 1) + "");
                        }
                        notifyDataSetChanged();
                    }
                });
            }
        };
        imageRecyclerView.setAdapter(imageAdapter);
    }

    private void init() {
        //指定的是images,并且指明是外部内容
        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        //指定要返回的内容列
        String[] projection = new String[]{
                MediaStore.Images.Media.DISPLAY_NAME,//文件名
                MediaStore.Images.Media.DATA,//文件路径
                MediaStore.Images.Media.HEIGHT,//媒体项目的高度，以像素为单位。
                MediaStore.Images.Media.SIZE,//媒体项目的大小。
                MediaStore.Images.Media.TITLE,//媒体项目的标题。
                MediaStore.Images.Media.WIDTH,//媒体项目的宽度，以像素为单位。
                MediaStore.Images.Media.DATE_MODIFIED//媒体项目上次修改的时间。
        };

        /*
         * url:指明要查询的内容类型
         * projection:要返回的内容列
         *selection:设置条件，相当于SQL语句中的where。null表示不进行筛选。
         * selectionArgs:这个参数是要配合第三个参数使用的，如果你在第三个参数里面有？，那么你在selectionArgs写的数据就会替换掉？
         * sortOrder:按照什么进行排序，相当于SQL语句中的Order by。如果想要结果按照ID的降序排列,DESC是从大到小排序,ASC是从小到大排序
         */
        Cursor cursor = getContentResolver().query(uri, projection, null, null,
                MediaStore.Images.Media.DATE_MODIFIED + " DESC");
        if (cursor != null) {
            //用于关联文件夹和文件,将文件分类在文件夹中
            JSONObject parentJsonObject = new JSONObject();
            //使用jsonArray保存所有查询出的图片
            JSONArray jsonArray = new JSONArray();
            //使用jsonObject保存每个图片的信息
            JSONObject jsonObject;
            //开始遍历查询出的内容
            while (cursor.moveToNext()) {
                jsonObject = new JSONObject();
                for (int i = 0; i < projection.length; i++) {
                    //字段名做key,值做value
                    jsonObject.put(projection[i], cursor.getString(i));
                }
                //获取父文件夹路径
                jsonObject.put("parentFilePath", FileUtils.getFileParentFolderPath(jsonObject.getString(MediaStore.Images.Media.DATA)));
                //获取父文件夹名称
                jsonObject.put("parentFileName", FileUtils.getParentFileName(jsonObject.getString(MediaStore.Images.Media.DATA)));
                //加入到全图片列表中
                jsonArray.add(jsonObject);
                //判断以父文件夹路径作为key的jsonArray是否存在
                if (parentJsonObject.containsKey(jsonObject.getString("parentFilePath"))) {
                    //存在就把jsonObject加入对应的value中
                    parentJsonObject.getJSONArray(jsonObject.getString("parentFilePath")).add(jsonObject);
                } else {
                    //不存在就创建以父文件夹路径作为key的jsonArray,并加入第一条数据
                    JSONArray newJsonArray = new JSONArray();
                    newJsonArray.add(jsonObject);
                    parentJsonObject.put(jsonObject.getString("parentFilePath"), newJsonArray);
                }
            }

            jsonObject = new JSONObject();
            //设置标题
            jsonObject.put("title", "全部图片");
            //设置预览图(取图片里的第一张)
            jsonObject.put("image", jsonArray.getJSONObject(0).getString(MediaStore.Images.Media.DATA));
            //设置图片数量
            jsonObject.put("count", jsonArray.size());
            //设置图片内容jsonArray数组
            jsonObject.put("array", jsonArray);
            //加入到全局文件夹jsonArray
            selectFolderArray.add(jsonObject);

            for (String key : parentJsonObject.keySet()) {
                jsonObject = new JSONObject();
                //设置标题(取列表第一条数据里父文件夹名称)
                jsonObject.put("title", parentJsonObject.getJSONArray(key).getJSONObject(0).getString("parentFileName"));
                //设置预览图(取列表第一条数据里的图片路径)
                jsonObject.put("image", parentJsonObject.getJSONArray(key).getJSONObject(0).getString(MediaStore.Images.Media.DATA));
                //设置图片数量
                jsonObject.put("count", parentJsonObject.getJSONArray(key).size());
                //设置图片内容jsonArray数组
                jsonObject.put("array", parentJsonObject.getJSONArray(key));
                //加入到全局文件夹jsonArray
                selectFolderArray.add(jsonObject);
            }
            imageArray.addAll(jsonArray);
            imageAdapter.notifyDataSetChanged();
            Log.d("jsonArray", jsonArray.toJSONString());
        } else {
            Log.d("图片", "没有图片");
        }
    }

    private void initSelectFolderPopupWindow() {
        //创建文件夹列表
        RecyclerView recyclerView = new RecyclerView(this);
        //设置线性布局管理器
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        //创建文件夹列表内容适配器
        CommonAdapter commonAdapter = new CommonAdapter(R.layout.item_select_folder, selectFolderArray) {
            @Override
            protected void bindViewHolder(ViewHolder viewHolder, JSONObject jsonObject, int position) {
                //设置列表中的标题并加上数量
                viewHolder.setText(R.id.text, jsonObject.getString("title")
                        + "(" + jsonObject.getString("count") + ")");
                //设置预览的第一张图片
                viewHolder.setImageURI(R.id.image, "file://" + jsonObject.getString("image"));
            }
        };
        //设置适配器
        recyclerView.setAdapter(commonAdapter);
        //给适配器加上点击事件
        commonAdapter.setOnClick(new RecyclerViewJsonArrayAdapter.Click() {
            @Override
            public void onClick(int position) {
                //在toolbar设置当前文件夹名称
                pathType.setText(selectFolderArray.getJSONObject(position).getString("title")
                        + "(" + selectFolderArray.getJSONObject(position).getString("count") + ")");
                //隐藏selectFolderPopupWindow
                selectFolderPopupWindow.dismiss();
                imageArray.clear();
                imageArray.addAll(selectFolderArray.getJSONObject(position).getJSONArray("array"));
                imageAdapter.notifyDataSetChanged();
            }
        });
        //创建selectFolderPopupWindow
        selectFolderPopupWindow = new PopupWindow(this);
        //给selectFolderPopupWindow设置根布局
        selectFolderPopupWindow.setContentView(recyclerView);
        // 设置点击popuwindow外让其消失
        selectFolderPopupWindow.setOutsideTouchable(true);
        //设置selectFolderPopupWindow隐藏的监听
        selectFolderPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                //检测到selectFolderPopupWindow隐藏就将箭头还原
                arrowDrop.animate().rotation(0).setDuration(500).start();
            }
        });
    }


    @OnClick(R.id.selectFolder)
    public void onViewClicked() {
        //将selectFolderPopupWindow依附于selectFolder显示
        selectFolderPopupWindow.showAsDropDown(selectFolder);
        //将箭头旋转180度
        arrowDrop.animate().rotation(180).setDuration(500).start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_determine, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.action_determine:
                String[] paths = SelectedItem.keySet().toArray(new String[0]);
                if (paths.length > 0) {
                    for (String s : paths)
                        Log.d("paths", s);
                    Intent data = new Intent();
                    data.putExtra("paths", paths);
                    setResult(RESULT_OK, data);
                    finish();
                } else {
                    finish();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
