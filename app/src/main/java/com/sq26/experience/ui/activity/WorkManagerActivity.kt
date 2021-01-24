package com.sq26.experience.ui.activity

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.work.*
import com.sq26.experience.R
import com.sq26.experience.databinding.ActivityWorkManagerBinding
import com.sq26.experience.util.Log
import kotlinx.coroutines.delay
import java.util.*
import java.util.concurrent.TimeUnit

class WorkManagerActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DataBindingUtil.setContentView<ActivityWorkManagerBinding>(
            this,
            R.layout.activity_work_manager
        ).apply {
            lifecycleOwner = this@WorkManagerActivity
            setOnClick {
                //定义简单工作单元
                //OneTimeWorkRequest适用于调度非重复性工作
                //无需额外配置的简单工作，请使用静态方法 from
//                val demoWorkRequest: WorkRequest = OneTimeWorkRequest.from(DemoWorker::class.java)
                //对于更复杂的工作，可以使用构建器
                val demoWorkRequest = OneTimeWorkRequestBuilder<DemoWorker>()
                    //设置延迟工作(延迟10分钟),定期工作只有首次运行时会延迟。
//                    .setInitialDelay(10, TimeUnit.MINUTES)
                    //重试和退避政策,默认政策是 BackoffPolicy.EXPONENTIAL(指数方式增加退避时间20,40,80)
                    //BackoffPolicy.LINEAR是线性增加退避时间(20,30,40)
                    //最短退避延迟时间要大于OneTimeWorkRequest.MIN_BACKOFF_MILLIS(10秒)
                    .setBackoffCriteria(
                        BackoffPolicy.LINEAR,
                        OneTimeWorkRequest.MIN_BACKOFF_MILLIS,
                        TimeUnit.MILLISECONDS
                    )
                    //标记工作,以便取消工作或观察其进度.可以添加多个tag,通过 WorkRequest.getTags() 检索其标记集。
                    .addTag("demo")
                    //设置输入数据,输入值以键值对的形式存储在 Data 对象中
                    //Worker 类可通过调用 Worker.getInputData() 访问输入参数。
                    .setInputData(workDataOf("text1" to "hello world", "text2" to "二号任务"))
                    .build()


                //提交工作任务
                WorkManager.getInstance(this@WorkManagerActivity).enqueue(demoWorkRequest)
                //获取返回值
                //直接获取Future线程,在通过get方法同步获取输出数据
//                val text = WorkManager.getInstance(this@WorkManagerActivity).getWorkInfoById(demoWorkRequest.id)
//                    .get().outputData.getString("text")
                //通过liveDate实时获取返回参数
                WorkManager.getInstance(this@WorkManagerActivity)
                    .getWorkInfoByIdLiveData(demoWorkRequest.id).observe(this@WorkManagerActivity) {
                        Log.i(it.outputData.getString("text").toString())
                    }
            }
            setOnClick2 {
                //通过tag取消任务,任务不会直接停止,但不会继续重试
                WorkManager.getInstance(this@WorkManagerActivity).cancelAllWorkByTag("demo")
            }
        }
    }

    fun other() {
        //创建约束 Constraints 实例
        val constraints = Constraints.Builder()
            //约束运行工作所需的网络类型。
            // NetworkType.UNMETERED:表示这项工作需要未计量的网络连接。(wifi)
            .setRequiredNetworkType(NetworkType.UNMETERED)
            //如果设置为 true，那么当设备处于“电量不足模式”时，工作不会运行。
            .setRequiresBatteryNotLow(true)
            //如果设置为 true，那么工作只能在设备充电时运行。
            .setRequiresCharging(true)
            //如果设置为 true，则要求用户的设备必须处于空闲状态，才能运行工作。
            //如果您要运行批量操作，否则可能会降低用户设备上正在积极运行的其他应用的性能，建议您使用此约束。
            //只能在api23(Android6.0)上使用此方法
//                    .setRequiresDeviceIdle(false)
            //如果设置为 true，那么当用户设备上的存储空间不足时，工作不会运行。
            .setRequiresStorageNotLow(true)
            .build()

        //PeriodicWorkRequest适合调度以一定间隔重复执行的工作。
        //工作的运行时间间隔定为一小时。
//                val periodicWorkRequest =
//                    PeriodicWorkRequestBuilder<DemoWorker>(1, TimeUnit.HOURS).build()
        //可在每小时的最后 15 分钟内运行的定期工作的示例。
        val periodicWorkRequest =
            PeriodicWorkRequestBuilder<DemoWorker>(
                1,
                TimeUnit.HOURS,
                15,
                TimeUnit.MINUTES
            )
                //设置约束
                .setConstraints(constraints)
                .build()

        val workA = OneTimeWorkRequestBuilder<DemoWorker>().build()
        val workB = OneTimeWorkRequestBuilder<DemoWorker>().build()
        val workC = OneTimeWorkRequestBuilder<DemoWorker>().build()
        val workD = OneTimeWorkRequestBuilder<DemoWorker>().build()
        val workE = OneTimeWorkRequestBuilder<DemoWorker>().build()
        //链接任务
        //WorkManager 会根据每个任务的指定约束，按请求的顺序运行任务。如果有任务返回 Result.failure()，整个序列结束。
        WorkManager.getInstance(this)
            //设置第一个工作,或是一个工作列表Arrays.asList(workA, workB, workC)
            .beginWith(workA)
            //添加后续的工作
            .then(workB)
            //提交工作链
            .enqueue()
        //联接多个任务链来创建更为复杂的序列
        val chain1 = WorkManager.getInstance(this)
            .beginWith(workA)
            .then(workB)
        val chain2 = WorkManager.getInstance(this)
            /**创建唯一工作序列
             * ExistingWorkPolicy:
             * 取消现有序列，并以新序列替换:ExistingWorkPolicy.REPLACE
             * 保留现有序列，并忽略新请求:ExistingWorkPolicy.KEEP
             * 将新序列附加到现有序列后面，在现有序列的最后一个任务完成后，开始运行新系列的第一个任务:ExistingWorkPolicy.APPEND_OR_REPLACE
             */
            .beginUniqueWork("chain2", ExistingWorkPolicy.APPEND_OR_REPLACE, workC)
            .then(workD)
        val chain3 = WorkContinuation
            .combine(listOf(chain1, chain2))
            .then(workE)
        chain3.enqueue()


    }
}

//定义工作
class DemoWorker(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {
    override fun doWork(): Result {
        //设置工作内容
        //获取输入参数
        inputData.getString("text1")?.apply {
            Log.i("inputData")
            Log.i(this)
        }
        for (i in 1..10000) {
            Thread.sleep(1000)
            Log.i(i.toString(),"DemoWorker")
        }

        //返回值
        val output = workDataOf("text" to "damo1")

        //返回工作结果
        // Result.success()：工作成功完成。
        // Result.failure()：工作失败。
        //Result.retry()：工作失败，应根据其重试政策在其他时间尝试。
        //设置返回值
        return Result.success(output)
    }

}