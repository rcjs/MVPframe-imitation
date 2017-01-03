package me.jessyan.mvparms.demo.mvp.presenter;

import android.app.Application;

import com.jess.arms.base.AppManager;
import com.jess.arms.mvp.BasePresenter;
import com.jess.arms.utils.RxUtils;
import com.tbruyelle.rxpermissions.RxPermissions;

import javax.inject.Inject;

import me.jessyan.mvparms.demo.app.entity.zhihu.ZhihuStory;
import me.jessyan.mvparms.demo.mvp.contract.ZhihuDescribeContract;
import me.jessyan.rxerrorhandler.core.RxErrorHandler;
import me.jessyan.rxerrorhandler.handler.ErrorHandleSubscriber;
import me.jessyan.rxerrorhandler.handler.RetryWithDelay;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by zjl on 2016/12/27.
 */

public class ZhihuDescribePresenter extends BasePresenter<ZhihuDescribeContract.Model, ZhihuDescribeContract.View> {
    private RxErrorHandler mErrorHandler;
    private RxPermissions mRxPermissions;
    private AppManager mAppManager;
    private Application mApplication;

    @Inject
    public ZhihuDescribePresenter(ZhihuDescribeContract.Model model, ZhihuDescribeContract.View rootView, RxErrorHandler handler
            , RxPermissions rxPermissions, AppManager appManager, Application application) {
        super(model, rootView);
        this.mApplication = application;
        this.mErrorHandler = handler;
        this.mRxPermissions = rxPermissions;
        this.mAppManager = appManager;
    }


    public void getZhihuStory(String id, final boolean pullToRefresh) {
        mModel.getZhihuStory(id)
                .subscribeOn(Schedulers.io())
                .retryWhen(new RetryWithDelay(3, 2))//遇到错误时重试,第一个参数为重试几次,第二个参数为重试的间隔
               .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
              .compose(RxUtils.<ZhihuStory>bindToLifecycle(mRootView))//使用RXlifecycle,使subscription和activity一起销毁
                .subscribe(new ErrorHandleSubscriber<ZhihuStory>(mErrorHandler) {
                    @Override
                    public void onNext(ZhihuStory zhihuStory) {
                        mRootView.showZhihuStory(zhihuStory);
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        mRootView.showError(e.getMessage());
                    }
                });
    }
}
