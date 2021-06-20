package com.sq26.experience.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.paging.*
import androidx.recyclerview.widget.DividerItemDecoration
import com.sq26.experience.R
import com.sq26.experience.adapter.CommonListViewHolder
import com.sq26.experience.adapter.ExampleLoadStateAdapter
import com.sq26.experience.adapter.NetworkThrowable
import com.sq26.experience.adapter.NoMoreThrowable
import com.sq26.experience.data.RecyclerViewItem
import com.sq26.experience.data.RecyclerViewItemCallback
import com.sq26.experience.databinding.ActivityPagingBinding
import com.sq26.experience.databinding.ItemRecyclerviewItemBinding
import com.sq26.experience.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch

class PagingActivity : AppCompatActivity() {

    private val viewModel: PagingViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DataBindingUtil.setContentView<ActivityPagingBinding>(this, R.layout.activity_paging)
            .apply {
                lifecycleOwner = this@PagingActivity

                toolbar.setNavigationOnClickListener {
                    onBackPressedDispatcher.onBackPressed()
                }
                //创建分页库
                val adapter = object :
                    PagingDataAdapter<RecyclerViewItem, CommonListViewHolder<ItemRecyclerviewItemBinding>>(
                        RecyclerViewItemCallback()
                    ) {
                    //绑定视图
                    override fun onBindViewHolder(
                        holder: CommonListViewHolder<ItemRecyclerviewItemBinding>,
                        position: Int
                    ) {
                        holder.bind(position)
                    }

                    //创建视图
                    override fun onCreateViewHolder(
                        parent: ViewGroup,
                        viewType: Int
                    ): CommonListViewHolder<ItemRecyclerviewItemBinding> {
                        return object : CommonListViewHolder<ItemRecyclerviewItemBinding>(
                            ItemRecyclerviewItemBinding.inflate(
                                LayoutInflater.from(parent.context),
                                parent, false
                            )
                        ) {
                            override fun bind(position: Int) {
                                v.item = getItem(position)
                            }
                        }
                    }
                }
                //设置分割线
                recyclerView.addItemDecoration(
                    DividerItemDecoration(
                        this@PagingActivity,
                        DividerItemDecoration.VERTICAL
                    )
                )
                swipeRefreshLayout.setOnRefreshListener {
                    adapter.refresh()
                }

                //设置适配器,并设置头部和尾部
                recyclerView.adapter = adapter.withLoadStateHeaderAndFooter(
                    ExampleLoadStateAdapter { adapter.retry() },
                    ExampleLoadStateAdapter { adapter.retry() }
                )
                //adapter.loadStateFlow是堵塞协程,要单独创建协程运行
                lifecycleScope.launch {
                    //collectLatest:在新值发送后会取向上个协程的调用
                    adapter.loadStateFlow.collectLatest {
                        swipeRefreshLayout.isRefreshing = it.refresh is LoadState.Loading
                    }
                }

                lifecycleScope.launch {
                    viewModel.flow.collectLatest {
                        adapter.submitData(it)
                    }
                }
            }
    }
}

class PagingViewModel : ViewModel() {
    private val pageSize = 20
    val flow = Pager(
        PagingConfig(
            //定义单页要加载的数据数量
            pageSize = pageSize,
            //是否显示空占位符,默认值true,最好禁用,不然就全是占位item,看不到正在加载UI了,并且需要设置jumpThreshold在翻页过远时取消请求
            enablePlaceholders = false,
            //初始加载的数据数量,默认为单页加载数量的三倍
            initialLoadSize = pageSize * 2,
            //列表保存的最大数据数量,超出后会删除之前或之后的数据
            maxSize = pageSize * 2,
            //预取距离,距离边界还有多少数据时加载下一页,默认值为一页的数量
            prefetchDistance = 1
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
                Log.i(params.loadSize, isThread = true)
                try {
                    val list = mutableListOf<RecyclerViewItem>()
                    //模拟网络加载堵塞3秒
                    delay(3000)
                    for (i in prevKey.until(prevKey + pageSize)) {
                        list.add(RecyclerViewItem(id = i))
                    }
                    //模拟没有更多数据
                    return if (prevKey >= 100) {
                        //没有更多数据
                        LoadResult.Error(NoMoreThrowable())
                    } else {
                        //成功的返回
                        LoadResult.Page(
                            list,
                            //上一页key,如果当前页是0则返回null表示没有上一页,否则减去每次加载的页数就是上一页的key
                            if (prevKey == 0) null else prevKey - pageSize,
                            //下一页key,当页页加上页数就是下载一次加载的key
                            prevKey + pageSize
                        )
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    //失败的返回
                    return LoadResult.Error(NetworkThrowable())
                }
            }
        }
    }.flow
        .cachedIn(viewModelScope)
        .flowOn(Dispatchers.IO)


}
