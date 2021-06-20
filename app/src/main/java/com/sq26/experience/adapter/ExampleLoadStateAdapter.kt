package com.sq26.experience.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.sq26.experience.R
import com.sq26.experience.databinding.ItemLoadStateBinding
import com.sq26.experience.util.setOnClickAntiShake

/**
 * 创建时间：2021/5/11 17:18
 * 作者：syq
 * 描述：默认分页适配器的头部或尾部适配器
 */

class ExampleLoadStateAdapter(
    private val retry: () -> Unit
) : LoadStateAdapter<LoadStateViewHolder>() {
    //在状态变化后刷新状态
    override fun onBindViewHolder(holder: LoadStateViewHolder, loadState: LoadState) =
        holder.bind(loadState)
    //创建试图
    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState) =
        LoadStateViewHolder(parent, retry)
}
//默认分页适配器的头部或尾部视图
class LoadStateViewHolder(
    parent: ViewGroup,
    retry: () -> Unit
) : RecyclerView.ViewHolder(
    LayoutInflater.from(parent.context).inflate(R.layout.item_load_state, parent, false)
) {
    //获取视图绑定
    private val binding = ItemLoadStateBinding.bind(itemView)
    //设置重试按钮点击事件监听
    private val retry = binding.retry.also {
        it.setOnClickAntiShake {
            retry()
        }
    }
    //状态变化处理
    fun bind(loadState: LoadState) {
        //隐藏重试按钮
        retry.isVisible = false
        //判断是否出错状态
        if (loadState is LoadState.Error) {
            //根据错误类型做不同处理
            when (loadState.error) {
                //没有更多数据
                is NoMoreThrowable -> {
                    //显示没有更多数据
                    binding.errorMsg.text = binding.root.context.getText(R.string.no_more)
                }
                //网络异常
                is NetworkThrowable -> {
                    //显示网络异常
                    binding.errorMsg.text = binding.root.context.getText(R.string.network_anomaly)
                    //显示重试按钮
                    retry.isVisible = true
                }
                //其他错误
                else -> {
                    //显示异常错误信息
                    binding.errorMsg.text = loadState.error.message
                    //显示重试按钮
                    retry.isVisible = true
                }
            }
        }
        //判断是加载中状态就显示进度条
        binding.progressBar.isVisible = loadState is LoadState.Loading
        //判断是错误状态就显示错误信息
        binding.errorMsg.isVisible = loadState is LoadState.Error
    }
}
//没有更多数据错误
class NoMoreThrowable : Throwable()
//没有网络错误
class NetworkThrowable : Throwable()
