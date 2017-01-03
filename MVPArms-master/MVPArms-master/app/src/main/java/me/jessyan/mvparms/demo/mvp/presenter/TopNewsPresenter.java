package me.jessyan.mvparms.demo.mvp.presenter;

import android.app.Application;
import android.graphics.Bitmap;
import android.util.Log;

import com.google.gson.Gson;
import com.jess.arms.base.AppManager;
import com.jess.arms.mvp.BasePresenter;
import com.jess.arms.utils.RxUtils;
import com.tbruyelle.rxpermissions.RxPermissions;

import javax.inject.Inject;

import me.jessyan.mvparms.demo.app.entity.news.NewsList;
import me.jessyan.mvparms.demo.app.entity.zhihu.ZhihuDaily;
import me.jessyan.mvparms.demo.app.entity.zhihu.ZhihuDailyItem;
import me.jessyan.mvparms.demo.config.Config;
import me.jessyan.mvparms.demo.mvp.contract.TopNewsContract;
import me.jessyan.mvparms.demo.mvp.contract.ZhihuContract;
import me.jessyan.mvparms.demo.utils.CacheUtil;
import me.jessyan.rxerrorhandler.core.RxErrorHandler;
import me.jessyan.rxerrorhandler.handler.ErrorHandleSubscriber;
import me.jessyan.rxerrorhandler.handler.RetryWithDelay;
import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by zjl on 2016/12/27.
 */

public class TopNewsPresenter extends BasePresenter<TopNewsContract.Model, TopNewsContract.View> {

    private RxErrorHandler mErrorHandler;
    private RxPermissions mRxPermissions;
    private AppManager mAppManager;
    private Application mApplication;

    @Inject
    public TopNewsPresenter(TopNewsContract.Model model, TopNewsContract.View rootView, RxErrorHandler handler
            , RxPermissions rxPermissions, AppManager appManager, Application application) {
        super(model, rootView);
        this.mApplication = application;
        this.mErrorHandler = handler;
        this.mRxPermissions = rxPermissions;
        this.mAppManager = appManager;
    }


    public void getNewsList(int t, final boolean pullToRefresh) {


        String[][] students ={{"1","2","3"},{"1","2","3"}};

        Observable.from(students)
                .map(new Func1<String[], Observable<String>>() {
                    @Override
                    public Observable<String> call(String[] student) {
                        return Observable.from(student);
                    }
                });

        Observable.from(students)
                .flatMap(new Func1<String[], Observable<String>>() {
                    @Override
                    public Observable<String> call(String[] student) {
                        return Observable.from(student);
                    }
                });

        Observable.from(students)
                .map(new Func1<String[], String>() {
                    @Override
                    public String call(String[] student) {
                        return student[0];
                    }
                });





        mModel.getNews(t)
                .subscribeOn(Schedulers.io())
                .retryWhen(new RetryWithDelay(3, 2))//遇到错误时重试,第一个参数为重试几次,第二个参数为重试的间隔
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        if (pullToRefresh)
                            mRootView.showLoading();//显示上拉刷新的进度条
                        else
                            mRootView.startLoadMore();
                    }
                })
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .doAfterTerminate(new Action0() {
                    @Override
                    public void call() {
                        if (pullToRefresh)
                            mRootView.hideLoading();//隐藏上拉刷新的进度条
                        else
                            mRootView.endLoadMore();
                    }
                })
                .compose(RxUtils.<NewsList>bindToLifecycle(mRootView))//使用RXlifecycle,使subscription和activity一起销毁
                .subscribe(new ErrorHandleSubscriber<NewsList>(mErrorHandler) {
                    @Override
                    public void onNext(NewsList newsList) {
                        mRootView.upListItem(newsList);
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        mRootView.showError(e.getMessage());
                    }
                });

    }
}
