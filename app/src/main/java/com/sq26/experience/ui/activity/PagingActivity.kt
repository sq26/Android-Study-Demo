package com.sq26.experience.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.paging.*
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.sq26.experience.R
import com.sq26.experience.data.RecyclerViewDao
import com.sq26.experience.data.RecyclerViewItem
import com.sq26.experience.databinding.ActivityPagingBinding
import com.sq26.experience.databinding.ItemRecyclerviewItemBinding
import com.sq26.experience.util.Log
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@AndroidEntryPoint
class PagingActivity : AppCompatActivity() {
    private val viewModel: PagingViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DataBindingUtil.setContentView<ActivityPagingBinding>(this, R.layout.activity_paging)
            .apply {
                lifecycleOwner = this@PagingActivity
                val adapter = PagingDemoAdapter()
                recyclerView.addItemDecoration(
                    DividerItemDecoration(
                        this@PagingActivity,
                        DividerItemDecoration.VERTICAL
                    )
                )
                recyclerView.adapter = adapter

                lifecycleScope.launch {
                    viewModel.flow.collectLatest {
                        adapter.submitData(it)
                    }
                }
            }
    }
}

@HiltViewModel
class PagingViewModel @Inject constructor() : ViewModel() {
    val flow = Pager(
        PagingConfig(
            //定义单页要加载的数据数量
            pageSize = 20,
            //是否显示空占位符,默认值true,最好禁用,不然就全是占位item,看不到正在加载UI了,并且需要设置jumpThreshold在翻页过远时取消请求
            enablePlaceholders = false,
            //初始加载的数据数量,默认为单页加载数量的三倍
            initialLoadSize = 40,
            //列表保存的最大数据数量,超出后会删除之前或之后的数据
            maxSize = 50,
            //预取距离,距离边界还有多少数据时加载下一页,默认值为一页的数量
            prefetchDistance = 4
        )
    ) {
        object : PagingSource<Int, RecyclerViewItem>() {

            override fun getRefreshKey(state: PagingState<Int, RecyclerViewItem>): Int? {
                //在初始调用和刷新时调用,返回load方法的key
                return null
            }

            override suspend fun load(params: LoadParams<Int>): LoadResult<Int, RecyclerViewItem> {
                val prevKey = params.key ?: 0
                //params.loadSize:在初次请求时值为initialLoadSize的值,之后取pageSize的值
                Log.i(params.loadSize)
                try {
                    val list = mutableListOf<RecyclerViewItem>()
                    //模拟网络加载堵塞3秒
                    delay(3000)
                    for (i in prevKey..prevKey + params.loadSize) {
                        list.add(RecyclerViewItem(id = i))
                    }
                    //成功的返回
                    return LoadResult.Page(
                        list,
                        if (prevKey == 0) null else prevKey,
                        prevKey + params.loadSize
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                    //失败的返回
                    return LoadResult.Error(e)
                }

            }

        }
    }.flow
        .cachedIn(viewModelScope)


}

class PagingDemoAdapter() : PagingDataAdapter<RecyclerViewItem, PagingDemoAdapter.ViewHolder>(
    RecyclerViewItemComparator
) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemRecyclerviewItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        getItem(position)?.let { holder.bind(it) }
    }

    inner class ViewHolder(private val itemRecyclerviewItemBinding: ItemRecyclerviewItemBinding) :
        RecyclerView.ViewHolder(itemRecyclerviewItemBinding.root) {

        init {
            itemRecyclerviewItemBinding.root.setOnClickListener {
                itemRecyclerviewItemBinding.item?.apply {
                    Log.i(this.id.toString(), "Recyclerview")
                }
            }
        }

        fun bind(recyclerViewItem: RecyclerViewItem) {
            itemRecyclerviewItemBinding.apply {
                item = recyclerViewItem
            }
        }
    }

    object RecyclerViewItemComparator : DiffUtil.ItemCallback<RecyclerViewItem>() {
        override fun areItemsTheSame(
            oldItem: RecyclerViewItem,
            newItem: RecyclerViewItem
        ): Boolean {
            // Id is unique.
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: RecyclerViewItem,
            newItem: RecyclerViewItem
        ): Boolean {
            return oldItem == newItem
        }
    }

}

//class ExampleLoadStateAdapter() : LoadStateAdapter<LoadStateViewHolder>() {
//    override fun onBindViewHolder(holder: LoadStateViewHolder, loadState: LoadState) {
//        holder.bind(loadState)
//    }
//
//    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): LoadStateViewHolder =
//        LoadStateViewHolder(parent)
//
//}

//class LoadStateViewHolder(
//    parent: ViewGroup
//) : RecyclerView.ViewHolder(
//    LayoutInflater.from(parent.context)
//        .inflate(R.layout.load_state_item, parent, false)
//) {
//    private val binding = LoadStateItemBinding.bind(itemView)
//    private val progressBar: ProgressBar = binding.progressBar
//    private val errorMsg: TextView = binding.errorMsg
//    private val retry: Button = binding.retryButton
//        .also {
//            it.setOnClickListener { retry() }
//        }
//
//    fun bind(loadState: LoadState) {
//        if (loadState is LoadState.Error) {
//            errorMsg.text = loadState.error.localizedMessage
//        }
//
//        progressBar.isVisible = loadState is LoadState.Loading
//        retry.isVisible = loadState is LoadState.Error
//        errorMsg.isVisible = loadState is LoadState.Error
//    }
//}