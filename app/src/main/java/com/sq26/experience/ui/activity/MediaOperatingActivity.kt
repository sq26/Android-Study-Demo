package com.sq26.experience.ui.activity

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.alibaba.fastjson.JSONArray
import com.alibaba.fastjson.JSONObject
import com.facebook.drawee.view.SimpleDraweeView
import com.sq26.experience.R
import com.sq26.experience.adapter.CommonAdapter
import com.sq26.experience.adapter.ViewHolder
import com.sq26.experience.ui.dialog.ProgressDialog
import com.sq26.experience.util.DensityUtil
import com.sq26.experience.util.FileUtil
import com.sq26.experience.util.media.JImage
import com.sq26.experience.util.media.SimpleDraweeViewUtils

class MediaOperatingActivity : AppCompatActivity() {
    @JvmField
    @BindView(R.id.preview)
    var preview: SimpleDraweeView? = null

    @JvmField
    @BindView(R.id.imageRecyclerView)
    var imageRecyclerView: RecyclerView? = null
    private var imageAdapter: CommonAdapter? = null
    private val imageArray = JSONArray()
    private var context: Context? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_media_management)
        ButterKnife.bind(this)
        context = this
        init()
    }

    private fun init() {
        imageAdapter = object : CommonAdapter(R.layout.item_recyclerview, imageArray) {
            override fun bindViewHolder(
                viewHolder: ViewHolder,
                jsonObject: JSONObject,
                position: Int,
                payload: Any
            ) {
                viewHolder.setText(R.id.text, jsonObject.getString("name"))
                viewHolder.itemView.setOnClickListener { view: View? ->
                    SimpleDraweeViewUtils.setDraweeController(
                        jsonObject.getString("path"),
                        preview,
                        DensityUtil.dip2px(context, 150f)
                    )
                }
            }
        }
        imageRecyclerView!!.addItemDecoration(
            DividerItemDecoration(
                this,
                DividerItemDecoration.VERTICAL
            )
        )
        imageRecyclerView!!.adapter = imageAdapter
    }

    @OnClick(R.id.getImage, R.id.getVideo)
    fun onViewClicked(view: View) {
        when (view.id) {
            R.id.getImage -> JImage.initialize(this)
                .setImageSource(JImage.ALL)
                .isCompression(true)
                .success { path: Array<String?> ->
                    var item: JSONObject
                    for (p in path) {
                        Log.d("getImage", p!!)
                        item = JSONObject()
                        item["name"] = FileUtil.getFileName(p)
                        item["path"] = p
                        imageArray.add(item)
                    }
                    imageAdapter!!.notifyDataSetChanged()
                }.start()
            R.id.getVideo -> {
                val progressDialog = ProgressDialog(this)
                    .setMessage("123")
                    .show()
                progressDialog.setMessage("321")
            }
        }
    }
}