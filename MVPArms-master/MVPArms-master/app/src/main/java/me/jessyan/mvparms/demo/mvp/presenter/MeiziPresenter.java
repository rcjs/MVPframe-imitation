package me.jessyan.mvparms.demo.mvp.presenter;

import android.app.Application;
import android.util.Log;

import com.jess.arms.base.AppManager;
import com.jess.arms.base.DefaultAdapter;
import com.jess.arms.mvp.BasePresenter;
import com.jess.arms.utils.RxUtils;
import com.tbruyelle.rxpermissions.RxPermissions;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import me.jessyan.mvparms.demo.app.entity.User;
import me.jessyan.mvparms.demo.app.entity.meizi.MeiziData;
import me.jessyan.mvparms.demo.app.entity.meizi.VedioData;
import me.jessyan.mvparms.demo.mvp.contract.MeiziContract;
import me.jessyan.mvparms.demo.mvp.contract.UserContract;
import me.jessyan.mvparms.demo.mvp.ui.adapter.UserAdapter;
import me.jessyan.mvparms.demo.utils.ToastUtil;
import me.jessyan.rxerrorhandler.core.RxErrorHandler;
import me.jessyan.rxerrorhandler.handler.ErrorHandleSubscriber;
import me.jessyan.rxerrorhandler.handler.RetryWithDelay;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.schedulers.Schedulers;

/**
 * Created by zjl on 2016/12/23.
 */

public class MeiziPresenter extends BasePresenter<MeiziContract.Model, MeiziContract.View> {

    private RxErrorHandler mErrorHandler;
    private RxPermissions mRxPermissions;
    private AppManager mAppManager;
    private Application mApplication;

    @Inject
    public MeiziPresenter(MeiziContract.Model model, MeiziContract.View rootView, RxErrorHandler handler
            , RxPermissions rxPermissions, AppManager appManager, Application application) {
        super(model, rootView);
        this.mApplication = application;
        this.mErrorHandler = handler;
        this.mRxPermissions = rxPermissions;
        this.mAppManager = appManager;
    }

    public void getVedioData(int t, final boolean pullToRefresh) {
        mModel.getVedioData(t)
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
                .compose(RxUtils.<VedioData>bindToLifecycle(mRootView))//使用RXlifecycle,使subscription和activity一起销毁
                .subscribe(new ErrorHandleSubscriber<VedioData>(mErrorHandler) {
                    @Override
                    public void onNext(VedioData vedioData) {
                        mRootView.updateVedioData(vedioData.getResults());
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        mRootView.showError(e.getMessage());
                    }
                });
    }

    public void getMeiziData(int t) {

        mModel.getMeizhiData(t)
                .subscribeOn(Schedulers.io())
                .retryWhen(new RetryWithDelay(3, 2))//遇到错误时重试,第一个参数为重试几次,第二个参数为重试的间隔
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        if (true)
                            mRootView.showLoading();//显示上拉刷新的进度条
                        else
                            mRootView.startLoadMore();
                    }
                }).subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .doAfterTerminate(new Action0() {
                    @Override
                    public void call() {
                        if (true)
                            mRootView.hideLoading();//隐藏上拉刷新的进度条
                        else
                            mRootView.endLoadMore();
                    }
                })
                .compose(RxUtils.<MeiziData>bindToLifecycle(mRootView))//使用RXlifecycle,使subscription和activity一起销毁
                .subscribe(new ErrorHandleSubscriber<MeiziData>(mErrorHandler) {
                    @Override
                    public void onNext(MeiziData meiziData) {
                     mRootView.updateMeiziData(meiziData.getResults());
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        mRootView.showError(e.getMessage());
                    }
                });

    }
}
