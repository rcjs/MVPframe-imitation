package me.jessyan.mvparms.demo.mvp.presenter;

import android.app.Application;

import com.jess.arms.base.AppManager;
import com.jess.arms.mvp.BasePresenter;
import com.tbruyelle.rxpermissions.RxPermissions;

import javax.inject.Inject;

import me.jessyan.mvparms.demo.app.entity.news.NewsDetailBean;
import me.jessyan.mvparms.demo.utils.Urls;
import me.jessyan.mvparms.demo.mvp.contract.TopNewsDescribeContract;
import me.jessyan.mvparms.demo.utils.NewsJsonUtils;
import me.jessyan.mvparms.demo.utils.OkHttpUtils;
import me.jessyan.rxerrorhandler.core.RxErrorHandler;

/**
 * Created by zjl on 2016/12/27.
 */

public class TopNewsDescribePresenter extends BasePresenter<TopNewsDescribeContract.Model, TopNewsDescribeContract.View> {
    private RxErrorHandler mErrorHandler;
    private RxPermissions mRxPermissions;
    private AppManager mAppManager;
    private Application mApplication;

    @Inject
    public TopNewsDescribePresenter(TopNewsDescribeContract.Model model, TopNewsDescribeContract.View rootView, RxErrorHandler handler
            , RxPermissions rxPermissions, AppManager appManager, Application application) {
        super(model, rootView);
        this.mApplication = application;
        this.mErrorHandler = handler;
        this.mRxPermissions = rxPermissions;
        this.mAppManager = appManager;
    }


    private String getDetailUrl(String docId) {
        StringBuffer sb = new StringBuffer(Urls.NEW_DETAIL);
        sb.append(docId).append(Urls.END_DETAIL_URL);
        return sb.toString();
    }

    public void getDescrible(final String docid) {
        mRootView.showLoading();
        String url = getDetailUrl(docid);
        OkHttpUtils.ResultCallback<String> loadNewsCallback = new OkHttpUtils.ResultCallback<String>() {
            @Override
            public void onSuccess(String response) {
                NewsDetailBean newsDetailBean = NewsJsonUtils.readJsonNewsDetailBeans(response, docid);
                mRootView.upListItem(newsDetailBean);
            }

            @Override
            public void onFailure(Exception e) {

                mRootView.hideLoading();
                mRootView.showError(e.toString());
            }
        };
        OkHttpUtils.get(url, loadNewsCallback);

    }

}
