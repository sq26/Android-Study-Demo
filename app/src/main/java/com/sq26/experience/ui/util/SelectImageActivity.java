package com.sq26.experience.ui.util;

import android.app.Dialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.sq26.experience.R;
import com.sq26.experience.adapter.CommonAdapter;
import com.sq26.experience.adapter.RecyclerViewAdapter;
import com.sq26.experience.adapter.ViewHolder;
import com.sq26.experience.ui.view.zoomable.DoubleTapGestureListener;
import com.sq26.experience.ui.view.zoomable.ZoomableDraweeView;
import com.sq26.experience.util.AntiShake;
import com.sq26.experience.util.DensityUtil;
import com.sq26.experience.util.FileUtil;
import com.sq26.experience.util.media.SimpleDraweeViewUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

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
    @BindView(R.id.preview)
    TextView preview;
    @BindView(R.id.originalImage)
    CheckBox originalImage;
    //全局文件夹分类jsonArray
    private JSONArray selectFolderArray = new JSONArray();
    //弹出式窗口
    private PopupWindow selectFolderPopupWindow;
    //用于保存当前文件夹图片的jsonArray
    private JSONArray imageArray = new JSONArray();
    //用于展示当前文件夹图片的适配器
    private CommonAdapter imageAdapter;
    //用于记录全局选中的图片的路径和编号,key为路径,value为编号
    private JSONObject selectedItem = new JSONObject(new LinkedHashMap<>());
    //用于记录当前文件夹中选中的图片的路径和下标,key为路径,value为下标
    private JSONObject selectedItemIndex = new JSONObject();
    //用于记录全局选中的文件夹的下标,默认为0
    private int selectFolderArrayIndex = 0;
    //设置最大选择图片上限(默认0表示无上限)
    private int maxCount = 0;
    //用于方便设置activity上下文
    private Context context;

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
        //设置当前activity的上下文
        context = this;
        //设置最大选择数量
        maxCount = getIntent().getIntExtra("maxCount", 0);
        //设置toolbar为actionbar
        setSupportActionBar(toolbar);
        //设置不显示toolbar标题
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        //创建显示图片列表的内容适配器
        imageAdapter = new CommonAdapter(R.layout.item_select_image, imageArray) {
            @Override
            protected void bindViewHolder(ViewHolder viewHolder, JSONObject jsonObject, int position) {
                //给图片视图设置图片路径
                SimpleDraweeViewUtils.setDraweeController(jsonObject.getString("fileUri"), viewHolder.getView(R.id.image), DensityUtil.dip2px(context, 180));
                //判断有没有选中过
                if (selectedItem.containsKey(jsonObject.getString("fileUri"))) {
                    //选中过就设置当前选中的编号
                    viewHolder.setText(R.id.count, selectedItem.getString(jsonObject.getString("fileUri")));
                    //并把背景颜色改为选中色
                    viewHolder.setBackgroundResource(R.id.count, R.drawable.bg_corners_all);
                    //被选中过就保存一下该文件在当前文件夹中的下标,用于保存新文件夹中已选中的文件的下标(虽然在这里调用会重复保存造成些许多余的性能流失,但暂时找不到更高效的保存方法)
                    selectedItemIndex.put(jsonObject.getString("fileUri"), position);
                } else {
                    //没有选中就把编号设置为空字符
                    viewHolder.setText(R.id.count, "");
                    //并把背景色改为白色圆环
                    viewHolder.setBackgroundResource(R.id.count, R.drawable.bg_ring_white);
                }
                //给编号视图添加点击事件,用来记录选中状态
                viewHolder.setOnClickListener(R.id.count, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //判断有没有选中过
                        if (selectedItem.containsKey(jsonObject.getString("fileUri"))) {
                            //有选中过
                            //移除选中记录
                            selectedItem.remove(jsonObject.getString("fileUri"));
                            //移除下标记录
                            selectedItemIndex.remove(jsonObject.getString("fileUri"));
                            //刷新刚取消选中的视图的下标
                            notifyItemChanged(position);
                            //初始化编号标记
                            int index = 1;
                            //重新遍历,计算新的编号
                            for (String s : selectedItem.keySet()) {
                                //设置新的编号
                                selectedItem.put(s, index);
                                //刷新所有选中的视图(重新计算过编号信息后的),指定下标
                                notifyItemChanged(selectedItemIndex.getInteger(s));
                                //每设置好,编号增加一位
                                index++;
                            }
                        } else {
                            //判断是否到达已选择图片数量的上限
                            if (maxCount == 0 || selectedItem.size() < maxCount) {
                                //没有选中过就直接加入记录,并设置编号为总选中数量加1
                                selectedItem.put(jsonObject.getString("fileUri"), (selectedItem.size() + 1) + "");
                                //刷新指定下标的item视图
                                notifyItemChanged(position);
                            }
                        }
                        //刷新预览按钮文本
                        updatePreviewButton();
                    }
                });
                //给图片添加点击事件
                viewHolder.setOnClickListener(R.id.image, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //显示当前文件夹图片列表的dialog,并指定要显示的下标
                        showFullscreenDialog(false, position);
                    }
                });
            }
        };
        //给显示图片列表的视图设置内容适配器
        imageRecyclerView.setAdapter(imageAdapter);
    }

    private void init() {
        //使用rxJava在子线程中获取数据
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                //指定的是images,并且指明是外部内容
                Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                //指定要返回的内容列
                String[] projection = new String[]{
                        MediaStore.Images.Media._ID,//文件id
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
                        jsonObject.put("parentFilePath", FileUtil.getFileParentFolderPath(jsonObject.getString(MediaStore.Images.Media.DATA)));
                        //获取父文件夹名称
                        jsonObject.put("parentFileName", FileUtil.getParentFileName(jsonObject.getString(MediaStore.Images.Media.DATA)));
//                        //获取文件的uri
//                        jsonObject.put("fileUri", Uri.withAppendedPath(uri, jsonObject.getString(MediaStore.Images.Media._ID)).toString());
                        //获取文件的uri
                        jsonObject.put("fileUri", ContentUris.withAppendedId(uri, jsonObject.getLong(MediaStore.Images.Media._ID)).toString());
                        //加入到全图片列表中
                        jsonArray.add(jsonObject);
                        //判断以父文件夹路径作为key的jsonArray是否存在
                        if (parentJsonObject.containsKey(jsonObject.getString("parentFilePath"))) {
                            //存在就把jsonObject加入对应的value中
                            parentJsonObject.getJSONArray(jsonObject.getString("parentFilePath")).add(jsonObject);
                        } else {
                            //不存在就以父文件夹路径作为key创建jsonArray,并加入第一条数据
                            JSONArray newJsonArray = new JSONArray();
                            newJsonArray.add(jsonObject);
                            parentJsonObject.put(jsonObject.getString("parentFilePath"), newJsonArray);
                        }
                    }
                    //关闭链接
                    cursor.close();
                    //创建一个新的JSONObject
                    jsonObject = new JSONObject();
                    //设置标题
                    jsonObject.put("title", "全部图片");
                    //设置预览图(取图片里的第一张)
                    jsonObject.put("image", jsonArray.getJSONObject(0).getString("fileUri"));
                    //设置图片数量
                    jsonObject.put("count", jsonArray.size());
                    //设置图片内容jsonArray数组
                    jsonObject.put("array", jsonArray);
                    //加入到全局文件夹jsonArray
                    selectFolderArray.add(jsonObject);
                    //遍历保存的父文件夹
                    for (String key : parentJsonObject.keySet()) {
                        jsonObject = new JSONObject();
                        //设置标题(取列表第一条数据里父文件夹名称)
                        jsonObject.put("title", parentJsonObject.getJSONArray(key).getJSONObject(0).getString("parentFileName"));
                        //设置预览图(取列表第一条数据里的图片路径)
                        jsonObject.put("image", parentJsonObject.getJSONArray(key).getJSONObject(0).getString("fileUri"));
                        //设置图片数量
                        jsonObject.put("count", parentJsonObject.getJSONArray(key).size());
                        //设置图片内容jsonArray数组
                        jsonObject.put("array", parentJsonObject.getJSONArray(key));
                        //加入到全局文件夹jsonArray
                        selectFolderArray.add(jsonObject);
                    }
                    //将全部图片列表加入到当前显示图片视图列表中
                    imageArray.addAll(jsonArray);


                    Log.d("jsonArray", jsonArray.toJSONString());
                } else {
                    Log.d("图片", "没有图片");
                }


                emitter.onComplete();
            }
        }).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Integer s) {
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {
                        //刷新图片视图列表
                        imageAdapter.notifyDataSetChanged();

                    }
                });
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
                SimpleDraweeViewUtils.setDraweeController(jsonObject.getString("image"), viewHolder.getView(R.id.image), DensityUtil.dip2px(context, 48));
            }
        };
        //设置适配器
        recyclerView.setAdapter(commonAdapter);
        //给适配器加上点击事件
        commonAdapter.setOnClick(new RecyclerViewAdapter.OnClick() {
            @Override
            public void click(JSONObject jsonObject, int position) {
                //判断当前选择的文件夹的下标是不是当前显示的文件夹的标(简单点说就是重复选择)
                if (selectFolderArrayIndex != position) {
                    //不是重复选择,记录当前悬着的文件夹的下标
                    selectFolderArrayIndex = position;
                    //在toolbar设置当前文件夹名称
                    pathType.setText(getString(R.string.fileNameAndCount, selectFolderArray.getJSONObject(position).getString("title"),
                            selectFolderArray.getJSONObject(position).getString("count")));
                    //隐藏selectFolderPopupWindow
                    selectFolderPopupWindow.dismiss();
                    //清空当前显示的图片列表
                    imageArray.clear();
                    //添加选中的图片列表
                    imageArray.addAll(selectFolderArray.getJSONObject(position).getJSONArray("array"));
                    //清空已选中列表
                    selectedItemIndex.clear();
                    //刷新图片列表视图
                    imageAdapter.notifyDataSetChanged();
                }
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
                //这里加入重复点击判断,是为了保存一次点击记录,
                // 这样可以解决在PopupWindow已展开时点击selectFolder造成PopupWindow刚关闭就又瞬间显示的问题,一秒的重复判定可以让刚关闭后的点击无效
                AntiShake.check(selectFolder.getId());
                //检测到selectFolderPopupWindow隐藏就将箭头还原
                arrowDrop.animate().rotation(0).setDuration(500).start();
            }
        });
    }

    @OnClick({R.id.selectFolder, R.id.preview})
    public void onViewClicked(View view) {
        //判断是否重复点击
        if (AntiShake.check(view.getId()))
            return;
        switch (view.getId()) {
            case R.id.selectFolder:
                Log.d("selectFolder", selectFolderPopupWindow.isShowing() + "");
                if (!selectFolderPopupWindow.isShowing()) {
                    //将selectFolderPopupWindow依附于selectFolder显示
                    selectFolderPopupWindow.showAsDropDown(selectFolder);
                    //将箭头旋转180度
                    arrowDrop.animate().rotation(180).setDuration(500).start();
                }
                break;
            case R.id.preview:
                //显示全屏dialog
                if (selectedItem.size() > 0)
                    showFullscreenDialog(true, 0);
                break;
        }
    }

    //显示全屏dialog
    //isChosen 是否是显示已选择列表,true 就显示已选择列表,否是就显示当前文件夹列表
    private void showFullscreenDialog(boolean isChosen, int index) {
        //创建基础dialog,并设置全屏dialog基础样式
        Dialog dialog = new Dialog(this, R.style.DialogFullscreen);
        //设置内容布局
        dialog.setContentView(R.layout.dialog_preview_image_array);
        //获取子控件,顶部Toolbar
        Toolbar dialogToolbar = dialog.findViewById(R.id.dialogToolbar);
        //设置顶部Toolbar右边返回按钮的点击事件
        dialogToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //点击后关闭dialog
                dialog.dismiss();
            }
        });
        //设置顶部Toolbar右边的菜单点击事件
        dialogToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                //点击确定将数据返回上级界面
                if (item.getItemId() == R.id.action_determine) {
                    //点击后关闭dialog
                    dialog.dismiss();
                    determine();
                }
                return false;
            }
        });
        //是否原图
        CheckBox dialogOriginalImage = dialog.findViewById(R.id.originalImage);
        //将activity的选中状态设置到dialog
        dialogOriginalImage.setChecked(originalImage.isChecked());
        dialogOriginalImage.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                //把选择的结果设置到activity的是否原图
                originalImage.setChecked(b);
            }
        });
        ViewPager viewPager = dialog.findViewById(R.id.viewPager);
        //是否选择
        CheckBox select = dialog.findViewById(R.id.select);
        //要显示的图片的列表
        List<String> imageList = new ArrayList<>();
        //判断是否是已选中列表
        if (isChosen) {
            //已选中列表
            imageList.addAll(selectedItem.keySet());
            //设置Toolbar标题
            dialogToolbar.setTitle("1/" + imageList.size());
            //即将要显示的第一张图片肯定是选中(刚从选中列表遍历出来怎么可能没选中)
            select.setChecked(true);
        } else {
            //当前文件夹图片列表
            //遍历当前显示文件夹imageArray,
            for (JSONObject jsonObject : imageArray.toArray(new JSONObject[0]))
                //把当前显示文件夹的所有图片路径加入imageList
                imageList.add(jsonObject.getString("fileUri"));
            //设置当前dialog显示的图片是否选中
            select.setChecked(selectedItem.containsKey(imageList.get(index)));
            //设置Toolbar标题
            dialogToolbar.setTitle((index + 1) + "/" + imageList.size());
        }
        //是否选择的点击事件
        select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //获取当前显示的图片的路径
                String path = imageList.get(viewPager.getCurrentItem());
                //已选中列表
                //判断该路径是否选中
                if (selectedItem.containsKey(path)) {
                    //已选中,移除当前选中
                    selectedItem.remove(path);
                    //判断是否是文件夹列表
                    if (!isChosen) {//不是已选中列表就是文件夹列表
                        //移除当前文件夹的选中下标记录
                        selectedItemIndex.remove(path);
                        //刷新当前文件夹的被移除选中的记录的item
                        imageAdapter.notifyItemChanged(viewPager.getCurrentItem());
                    }
                    //初始化编号标记
                    int index = 1;
                    //重新遍历,计算新的编号
                    for (String s : selectedItem.keySet()) {
                        //设置新的编号
                        selectedItem.put(s, index);
                        //每设置好,编号增加一位
                        index++;
                    }
                } else {
                    //判断是否到达已选择图片数量的上限
                    if (maxCount == 0 || selectedItem.size() < maxCount) {
                        //未选中(这种操作,只有反复横跳,才会出现)
                        selectedItem.put(path, (selectedItem.size() + 1) + "");
                        //判断是否是文件夹列表
                        if (!isChosen) {//不是已选中列表就是文件夹列表
                            //保存一下该图片在当前文件夹中的下标
                            selectedItemIndex.put(path, viewPager.getCurrentItem());
                        }
                    } else {
                        //这里要添加已到上限的提醒
                        select.setChecked(false);
                    }
                }
                //刷新预览按钮文本
                updatePreviewButton();
                Log.d("selectedItem", selectedItem.toJSONString());
            }
        });
        //设置图片列表适配器
        viewPager.setAdapter(new ImagePagerAdapter(imageList));
        //指定显示的图片的下标
        viewPager.setCurrentItem(index);
        //设置滑动监听
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            /**
             * 滑动监听
             * @param position 滑动的界面位置（viewpager界面排序为0.1.2.3....）
             * @param positionOffset 滑动的页面占整个屏幕的百分比
             * @param positionOffsetPixels 屏幕像素位置
             */
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                //这里用不上这个方法
            }

            /**
             *  滑动完成后的调用
             * @param position 跳转完成后的页面位置
             */
            @Override
            public void onPageSelected(int position) {
                //设置Toolbar标题
                dialogToolbar.setTitle((position + 1) + "/" + imageList.size());
                //设置当前显示的图片的选中状态
                select.setChecked(selectedItem.containsKey(imageList.get(position)));
            }

            /**
             * 滑动状态监听
             * @param state 当页面停止的时候该参数为0，页面开始滑动的时候变成了1，
             *              当手指从屏幕上抬起变为了2（无论页面是否从1跳到了2），当页面静止后又变成了0
             */
            @Override
            public void onPageScrollStateChanged(int state) {
                //这里用不上这个方法
            }
        });
        //监听dialog的关闭事件
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                //遍历当前文件夹中选中的图片的路径和下标
                for (String s : selectedItemIndex.keySet())
                    //刷新当前文件夹中记录过的下标
                    imageAdapter.notifyItemChanged(selectedItemIndex.getInteger(s));
            }
        });
        //创建并显示dialog
        dialog.show();
    }

    //将选择的图片返回到上级页面
    private void determine() {
        //获取已选中的图片路径
        String[] paths = selectedItem.keySet().toArray(new String[0]);
        //判断有没有选择
        if (paths.length > 0)
            //有选择
            //做遍历打印所有已选中的图片路径
            for (String s : paths)
                Log.d("paths", s);
        //创建intent对象
        Intent data = new Intent();
        //设置选中的数据
        data.putExtra("paths", paths);
        //设置设置是否原图
        data.putExtra("isOriginal", originalImage.isChecked());
        //设置返回的intent对象
        setResult(RESULT_OK, data);
        //关闭当前页面
        finish();
    }

    //更新预览按钮的文字
    private void updatePreviewButton() {
        //判断有没有选择
        if (selectedItem.size() == 0) {
            //没选择统一只显示预览
            preview.setText(R.string.Preview);
        } else {
            //有选中
            //判断有没有设置数量上限
            if (maxCount == 0) {
                //没有上限,只显示预览和已选择的数量
                preview.setText(getString(R.string.Preview_d, selectedItem.size()));
            } else {
                //有上限,显示预览加已选择的数量和上限数量
                preview.setText(getString(R.string.Preview_2d, selectedItem.size(), maxCount));
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //设置toolbar上的菜单按钮,设置了一个确定按钮
        getMenuInflater().inflate(R.menu.menu_determine, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                //调用返回按钮
                onBackPressed();
                break;
            case R.id.action_determine:
                determine();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    //自定义全屏dialog中的viewPager的适配器
    class ImagePagerAdapter extends PagerAdapter {
        private List<String> imageList;

        ImagePagerAdapter(List<String> imageList) {
            this.imageList = imageList;
        }

        //返回viewpager页面的个数
        @Override
        public int getCount() {
            return imageList.size();
        }

        //判断是否为同一个视图
        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }

        //是用于往viewpage中添加控件，添加内容
        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            //创建可以缩放的图片控件
            ZoomableDraweeView zoomableDraweeView = new ZoomableDraweeView(context);
            //允许缩放时切换
            zoomableDraweeView.setAllowTouchInterceptionWhileZoomed(true);
            //设置是否长按启用
            zoomableDraweeView.setIsLongpressEnabled(false);
            //双击击放大或缩小(这句要放在最后,以上都是设置),设置最大缩放倍数也要在DoubleTapGestureListener中
            zoomableDraweeView.setTapListener(new DoubleTapGestureListener(zoomableDraweeView));
            //加载图片
            DraweeController draweeController = Fresco.newDraweeControllerBuilder()
                    //指定配置,使用旧的配置,不新建
                    .setOldController(zoomableDraweeView.getController())
                    .setUri(imageList.get(position))
                    .build();
            //设置配置
            zoomableDraweeView.setController(draweeController);
            //加入ViewGroup
            container.addView(zoomableDraweeView);
            //返回图像控件
            return zoomableDraweeView;
        }

        //是加入页面的时候，默认缓存三个，如不做处理，滑多了程序就会蹦
        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView((View) object);
        }
    }

    //点击back键
    @Override
    public void onBackPressed() {
        //在每次点击返回键时做一次保存返回值的操作确保页面关闭前可以返回已选数据
        setResult(RESULT_CANCELED);
        super.onBackPressed();
    }
}
