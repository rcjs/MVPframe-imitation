package me.jessyan.mvparms.demo.mvp.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;

import com.jess.arms.base.DefaultAdapter;
import com.jess.arms.utils.UiUtils;

import java.util.ArrayList;

import butterknife.BindView;
import me.jessyan.mvparms.demo.R;
import me.jessyan.mvparms.demo.app.entity.meizi.Gank;
import me.jessyan.mvparms.demo.app.entity.meizi.Meizi;
import me.jessyan.mvparms.demo.di.component.AppComponent;
import me.jessyan.mvparms.demo.di.component.DaggerMeiziComponent;
import me.jessyan.mvparms.demo.di.module.MeiziModule;
import me.jessyan.mvparms.demo.mvp.contract.MeiziContract;
import me.jessyan.mvparms.demo.mvp.presenter.MeiziPresenter;
import me.jessyan.mvparms.demo.mvp.ui.adapter.MeiziAdapter;
import me.jessyan.mvparms.demo.mvp.ui.common.WEActivity;
import me.jessyan.mvparms.demo.mvp.ui.common.WEFragment;
import me.jessyan.mvparms.demo.utils.Once;
import me.jessyan.mvparms.demo.widget.WrapContentLinearLayoutManager;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import timber.log.Timber;

import static com.jess.arms.base.BaseApplication.getContext;

/**
 * Created by zjl on 2016/12/23.
 */

public class MeiziFragment extends WEFragment<MeiziPresenter> implements MeiziContract.View {

    @BindView(R.id.recycle_meizi)
    RecyclerView mRecyclerMeizi;
    @BindView(R.id.prograss)
    ProgressBar mProgress;

    WrapContentLinearLayoutManager linearLayoutManager;

    private MeiziAdapter meiziAdapter;
    RecyclerView.OnScrollListener loadmoreListener;
    private boolean loading;
    private int index = 1;
    private boolean isLoadingMore;

    @Override
    protected View initView() {
        return LayoutInflater.from(getContext()).inflate(R.layout.meizi_fragment_layout, null, false);
    }

    private void initialView() {
        linearLayoutManager = new WrapContentLinearLayoutManager(getContext());
        initalListener();
        mRecyclerMeizi.setLayoutManager(linearLayoutManager);
        mRecyclerMeizi.setAdapter(meiziAdapter);
        mRecyclerMeizi.addOnScrollListener(loadmoreListener);
        new Once(getContext()).show("tip_guide_6", new Once.OnceCallback() {
            @Override
            public void onOnce() {
                Snackbar.make(mRecyclerMeizi, getString(R.string.meizitips), Snackbar.LENGTH_INDEFINITE)
                        .setAction(R.string.meiziaction, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                            }
                        }).show();
            }
        });

        mRecyclerMeizi.setItemAnimator(new DefaultItemAnimator());

        loadDate();
    }

    private void initalListener() {
        loadmoreListener = new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) { //向下滚动
                    int visibleItemCount = linearLayoutManager.getChildCount();
                    int totalItemCount = linearLayoutManager.getItemCount();
                    int pastVisiblesItems = linearLayoutManager.findFirstVisibleItemPosition();

                    if (!loading && (visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                        loading = true;
                        index += 1;
                        loadMoreDate();
                    }

                }
            }
        };
    }

    private void loadMoreDate() {
        meiziAdapter.loadingStart();
        mPresenter.getMeiziData(index);
    }

    @Override
    protected void initData() {
        initialDate();
        initialView();
    }

    private void loadDate() {
        if (meiziAdapter.getItemCount() > 0) {
            meiziAdapter.clearData();
        }
        mPresenter.getMeiziData(index);//打开app时自动加载列表
    }

    private void initialDate() {
        meiziAdapter = new MeiziAdapter(getContext());
    }

    @Override
    public void updateMeiziData(ArrayList<Meizi> list) {
        System.out.println("zjlljz");

        meiziAdapter.loadingfinish();
        loading = false;
        meiziAdapter.addItems(list);
        mPresenter.getVedioData(index,true);
    }

    @Override
    public void updateVedioData(ArrayList<Gank> list) {
        meiziAdapter.addVedioDes(list);
    }


    /**
     * 开始加载更多
     */
    @Override
    public void startLoadMore() {
        isLoadingMore = true;
    }

    /**
     * 介绍加载更多
     */
    @Override
    public void endLoadMore() {
        isLoadingMore = false;
    }

    @Override
    public void showError(String error) {
        UiUtils.SnackbarText(error);
    }

    @Override
    public void showLoading() {
        Timber.tag(TAG).w("showLoading");
        Observable.just(1)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Integer>() {
                    @Override
                    public void call(Integer integer) {
                        mProgress.setVisibility(View.VISIBLE);
                    }
                });
    }

    @Override
    public void hideLoading() {
        Timber.tag(TAG).w("hideLoading");
        mProgress.setVisibility(View.GONE);
    }

    @Override
    public void showMessage(String message) {
        UiUtils.SnackbarText(message);
    }

    @Override
    public void launchActivity(Intent intent) {
        UiUtils.startActivity(intent);
    }

    @Override
    public void killMyself() {
        getActivity().finish();
    }

    @Override
    protected void setupFragmentComponent(AppComponent appComponent) {
        DaggerMeiziComponent.builder().appComponent(appComponent).meiziModule(new MeiziModule(this)).build().inject(this);
    }
}
