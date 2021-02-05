package com.sq26.experience.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.activity.addCallback
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.paging.*
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.sq26.experience.R
import com.sq26.experience.data.RecyclerViewDao
import com.sq26.experience.data.RecyclerViewItem
import com.sq26.experience.databinding.ActivityPagingBinding
import com.sq26.experience.databinding.ItemRecyclerviewItemBinding
import com.sq26.experience.util.Log
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class PagingActivity : AppCompatActivity() {
    private val viewModel: PagingViewModel by viewModels()

    @ExperimentalPagingApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DataBindingUtil.setContentView<ActivityPagingBinding>(this, R.layout.activity_paging)
            .apply {
                toolbar.title = viewModel.title
                setSupportActionBar(toolbar)
                val adapter = PagingDemoAdapter()
                recyclerView.adapter = adapter

                lifecycleScope.launch {
                    //加载状态监听
                    adapter.addLoadStateListener {
                        //显示进度条
                        progressBar.isVisible = it.refresh is LoadState.Loading
                        //显示刷新
//                        retry.isVisible = loadState.refresh !is LoadState.Loading
                        //显示错误
//                        errorMsg.isVisible = loadState.refresh is LoadState.Error
                    }
                    viewModel.pager.collectLatest {
                        adapter.submitData(it)
                    }
                }
            }
        //添加返回监听
        onBackPressedDispatcher.addCallback {
            //退出前清空数据
            viewModel.deleteAll()
            isEnabled = false
            finish()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home)
            onBackPressedDispatcher.onBackPressed()
        return super.onOptionsItemSelected(item)
    }
}

class PagingViewModel @ViewModelInject constructor(
    private val recyclerViewDao: RecyclerViewDao
) : ViewModel() {
    val title = "Paging分页库"

    //    val flow = Pager<Int, RecyclerViewItem>(PagingConfig(20)) {
//        PagingDemoSource()
//    }.flow.cachedIn(viewModelScope)
    fun deleteAll() {
        viewModelScope.launch(Dispatchers.IO) {
            recyclerViewDao.deleteAll()
        }
    }

    //pageSize:单次加载的条数
    //initialLoadSize:初始缓存的条数
    //maxSize:最大缓存数量
    @ExperimentalPagingApi
    val pager = Pager(config = PagingConfig(20, maxSize = 60),
        remoteMediator = object : RemoteMediator<Int, RecyclerViewItem>() {
            override suspend fun load(
                loadType: LoadType,
                state: PagingState<Int, RecyclerViewItem>
            ): MediatorResult {
                Log.i("开始", "remoteMediator")
                Log.i(loadType.toString(), "remoteMediator")

                when (loadType) {
                    //刷新,现在是第一次加载列表
                    LoadType.REFRESH -> {

                    }
                    //前置,加载前面的数据,可以刷新之前的数据
                    LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
                    //加载新数据
                    LoadType.APPEND -> {

                    }
                }
                    //判断当前数据库已经大于200条了就直接返回,不往下新增数据了
                if (withContext(Dispatchers.IO) { recyclerViewDao.queryCount() } < 200) {
                    //模拟发起网络请求
                    val response = mutableListOf<RecyclerViewItem>()
                    withContext(Dispatchers.IO) {
                        for (i in 1..20)
                            response.add(RecyclerViewItem(sort = recyclerViewDao.getMaxSort() + 1))
                    }
                    //储存加载的数据
                    withContext(Dispatchers.IO) {
                        recyclerViewDao.insertAll(response)
                    }
                    //如果加载成功且收到的项列表不是空的，则将相应的列表项存储到数据库中并返回
                    return MediatorResult.Success(
                        endOfPaginationReached = false
                    )
                } else
                //如果加载成功，但收到的项列表是空的，则返回
                    return MediatorResult.Success(endOfPaginationReached = true)
            }
        }) {
        object : PagingSource<Int, RecyclerViewItem>() {
            override suspend fun load(params: LoadParams<Int>): LoadResult<Int, RecyclerViewItem> {
                Log.i("开始", "PagingSource")
                Log.i(params.key.toString(), "PagingSourcekey")
                val nextPageNumber = params.key ?: 0
                val prevKey = if (nextPageNumber == 0) null else nextPageNumber - 1
                val limit = nextPageNumber + params.loadSize
                val data = withContext(Dispatchers.IO) {
                    recyclerViewDao.queryPagingAll(nextPageNumber, limit)
                }
                Log.i(data.size.toString(), "PagingSourcesize")
                val count = withContext(Dispatchers.IO) {
                    recyclerViewDao.queryCount()
                }
                val nextKey = if (limit < count) limit else null
                Log.i("prevKey:$prevKey,nextKey:$nextKey", "Page")
                return LoadResult.Page(data, prevKey, nextKey)
            }

            override fun getRefreshKey(state: PagingState<Int, RecyclerViewItem>): Int? {
                return 0
            }
        }
    }.flow.cachedIn(viewModelScope)
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

    class RemoteMediatorDemo() {

    }
}
