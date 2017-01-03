package me.jessyan.mvparms.demo.mvp.presenter;

import android.app.Application;
import android.util.Log;

import com.google.gson.Gson;
import com.jess.arms.base.AppManager;
import com.jess.arms.mvp.BasePresenter;
import com.jess.arms.utils.RxUtils;
import com.tbruyelle.rxpermissions.RxPermissions;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import me.jessyan.mvparms.demo.app.entity.User;
import me.jessyan.mvparms.demo.app.entity.meizi.VedioData;
import me.jessyan.mvparms.demo.app.entity.zhihu.ZhihuDaily;
import me.jessyan.mvparms.demo.app.entity.zhihu.ZhihuDailyItem;
import me.jessyan.mvparms.demo.config.Config;
import me.jessyan.mvparms.demo.mvp.contract.ZhihuContract;
import me.jessyan.mvparms.demo.utils.CacheUtil;
import me.jessyan.rxerrorhandler.core.RxErrorHandler;
import me.jessyan.rxerrorhandler.handler.ErrorHandleSubscriber;
import me.jessyan.rxerrorhandler.handler.RetryWithDelay;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by zjl on 2016/12/27.
 */

public class ZhihuPresenter extends BasePresenter<ZhihuContract.Model,ZhihuContract.View> {

    private RxErrorHandler mErrorHandler;
    private RxPermissions mRxPermissions;
    private AppManager mAppManager;
    private Application mApplication;
    private CacheUtil mCacheUtil;
    private Gson gson = new Gson();

    @Inject
    public ZhihuPresenter(ZhihuContract.Model model,ZhihuContract.View rootView, RxErrorHandler handler
            , RxPermissions rxPermissions, AppManager appManager, Application application) {
        super(model, rootView);
        this.mApplication = application;
        this.mErrorHandler = handler;
        this.mRxPermissions = rxPermissions;
        this.mAppManager = appManager;
        mCacheUtil = CacheUtil.get(application.getApplicationContext());
    }


    public void getTheDaily(String date, final boolean pullToRefresh) {
        mModel.getTheDaily(date)
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
                .map(new Func1<ZhihuDaily, ZhihuDaily>() {
                    @Override
                    public ZhihuDaily call(ZhihuDaily zhihuDaily) {
                        String date = zhihuDaily.getDate();
                        for (ZhihuDailyItem zhihuDailyItem : zhihuDaily.getStories()) {
                            zhihuDailyItem.setDate(date);
                        }
                        return zhihuDaily;
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
                .compose(RxUtils.<ZhihuDaily>bindToLifecycle(mRootView))//使用RXlifecycle,使subscription和activity一起销毁
                .subscribe(new ErrorHandleSubscriber<ZhihuDaily>(mErrorHandler) {
                    @Override
                    public void onNext(ZhihuDaily zhihuDaily) {
                        mRootView.updateList(zhihuDaily);
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        mRootView.showError(e.getMessage());
                    }
                });
    }


    public void getLastFromCache() {
        if (mCacheUtil.getAsJSONObject(Config.ZHIHU) != null) {
            ZhihuDaily zhihuDaily = gson.fromJson(mCacheUtil.getAsJSONObject(Config.ZHIHU).toString(), ZhihuDaily.class);
            mRootView.updateList(zhihuDaily);
        }
    }


    public void getLastZhihuNews(final boolean pullToRefresh) {
        mModel.getLastDaily()
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
                }).map(new Func1<ZhihuDaily, ZhihuDaily>() {
            @Override
            public ZhihuDaily call(ZhihuDaily zhihuDaily) {
                Log.d("ljz",zhihuDaily.getDate());
                String date = zhihuDaily.getDate();
                for (ZhihuDailyItem zhihuDailyItem : zhihuDaily.getStories()) {
                    zhihuDailyItem.setDate(date);
                }
                return zhihuDaily;
            }
        }).subscribeOn(AndroidSchedulers.mainThread())
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
                .compose(RxUtils.<ZhihuDaily>bindToLifecycle(mRootView))//使用RXlifecycle,使subscription和activity一起销毁
                .subscribe(new ErrorHandleSubscriber<ZhihuDaily>(mErrorHandler) {
                    @Override
                    public void onNext(ZhihuDaily zhihuDaily) {
                        mCacheUtil.put(Config.ZHIHU, gson.toJson(zhihuDaily));
                        mRootView.updateList(zhihuDaily);
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        mRootView.showError(e.getMessage());
                    }
                });
    }
}
