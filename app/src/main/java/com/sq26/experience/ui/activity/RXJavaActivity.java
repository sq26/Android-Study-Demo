package com.sq26.experience.ui.activity;

import android.os.Bundle;
import android.util.Log;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

import com.sq26.experience.R;

import org.reactivestreams.Subscriber;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static java.lang.Thread.sleep;

public class RXJavaActivity extends AppCompatActivity {

    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rxjava);
        ButterKnife.bind(this);
        progressBar.setMax(100);


        //Observable:被观察者
        //通过create方法生成对象
        //ObservableOnSubscribe<T>:可以理解为一个计划表,T是要处理的对象类型
        //subscribe:重写该方法,处理计划的执行逻辑,罗列计划表
        //ObservableEmitter<T>:Emitter是发射器的意思,有三种发射的方法,onNext,onError,onComplete
        //onNext方法可以无限调用,所有的观察者都能接收到,
        //onError和onComplete是互斥的，Observer（观察者）只能接收到一个
        //OnComplete可以重复调用，但是Observer（观察者）只会接收一次
        //onError不可以重复调用，第二次调用就会报异常

        //observeOn:是事件回调的线程,AndroidSchedulers.mainThread()是在主线程运行，
        //subscribeOn:是事件执行的线程Schedulers.io()是子线程，这里也可以用Schedulers.newThread()，只不过io线程可以重用空闲的线程，因此多数情况下 io() 比 newThread() 更有效率
        //subscribe:被观察者绑定观察者的方法
        //onSubscribe:这里可以获取到观察者对象,可以调用Disposable.dispose()取消订阅,此时被观察者依然在执行,观察者已不再处理任何发射的信息
        //onNext、onError、onComplete都是跟被观察者发射的方法一一对应的
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                for (int i = 0; i < 100; i++) {
                    emitter.onNext(i);
                    sleep(100);
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
                        progressBar.setSecondaryProgress(s);
                        Log.e("onNext", s + "");
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {
                        progressBar.setSecondaryProgress(100);
                    }
                });


        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                for (int i = 0; i < 100; i++) {
                    emitter.onNext(i);
                    sleep(200);
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
                        progressBar.setProgress(s);
                        Log.e("onNext", s + "");
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {
                        progressBar.setProgress(100);
                    }
                });

    }
}
