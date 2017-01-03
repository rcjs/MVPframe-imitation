package me.jessyan.mvparms.demo.mvp.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.os.Build;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewStub;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.jess.arms.utils.UiUtils;

import butterknife.BindView;
import me.jessyan.mvparms.demo.R;
import me.jessyan.mvparms.demo.app.entity.news.NewsList;
import me.jessyan.mvparms.demo.app.entity.zhihu.ZhihuDaily;
import me.jessyan.mvparms.demo.di.component.AppComponent;
import me.jessyan.mvparms.demo.di.component.DaggerTopNewsComponent;
import me.jessyan.mvparms.demo.di.module.TopNewsModule;
import me.jessyan.mvparms.demo.mvp.contract.TopNewsContract;
import me.jessyan.mvparms.demo.mvp.presenter.TopNewsPresenter;
import me.jessyan.mvparms.demo.mvp.ui.adapter.TopNewsAdapter;
import me.jessyan.mvparms.demo.mvp.ui.adapter.ZhihuAdapter;
import me.jessyan.mvparms.demo.mvp.ui.common.WEFragment;
import me.jessyan.mvparms.demo.widget.GridItemDividerDecoration;
import me.jessyan.mvparms.demo.widget.WrapContentLinearLayoutManager;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import timber.log.Timber;

/**
 * Created by zjl on 2016/12/27.
 */

public class TopNewsFragment extends WEFragment<TopNewsPresenter> implements TopNewsContract.View {

    boolean loading ;
    boolean connected  = true;
    TopNewsAdapter mTopNewsAdapter;

    LinearLayoutManager mLinearLayoutManager;
    RecyclerView.OnScrollListener loadingMoreListener;
    int currentIndex;

    @BindView(R.id.recycle_topnews)
    RecyclerView recycle;
    @BindView(R.id.prograss)
    ProgressBar mProgress;
    private int index = 1;
    private boolean isLoadingMore;
    @Override
    protected View initView() {
        return LayoutInflater.from(getContext()).inflate(R.layout.topnews_fragment_layout, null, false);
    }

    private void initialView() {
        initalListener();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mLinearLayoutManager = new WrapContentLinearLayoutManager(getContext());
        } else {
            mLinearLayoutManager = new LinearLayoutManager(getContext());
        }
        recycle.setLayoutManager(mLinearLayoutManager);
        recycle.setHasFixedSize(true);
        recycle.addItemDecoration(new GridItemDividerDecoration(getContext(), R.dimen.divider_height, R.color.divider));
        recycle.setItemAnimator(new DefaultItemAnimator());
        recycle.setAdapter(mTopNewsAdapter);
        recycle.addOnScrollListener(loadingMoreListener);
        if (connected) {
            loadDate();
        }
    }

    private void initalListener() {
        loadingMoreListener = new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (dy > 0) {
                    int visibleItemCount = mLinearLayoutManager.getChildCount();
                    int totalItemCount = mLinearLayoutManager.getItemCount();
                    int pastVisiblesItems = mLinearLayoutManager.findFirstVisibleItemPosition();
                    if (!loading && (visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                        loading = true;
                        loadMoreDate();
                    }
                }
            }
        };

    }

    private void loadMoreDate() {
        mTopNewsAdapter.loadingStart();
        currentIndex+=20;
        mPresenter.getNewsList(currentIndex,true);
    }

    @Override
    protected void initData() {
        initialDate();
        initialView();
    }

    private void loadDate() {
        if (mTopNewsAdapter.getItemCount() > 0) {
            mTopNewsAdapter.clearData();
        }
        currentIndex = 0;
        mPresenter.getNewsList(currentIndex,true);
    }

    private void initialDate() {
        mTopNewsAdapter =new TopNewsAdapter(getContext());
    }


    @Override
    public void upListItem(NewsList newsList) {
        loading=false;
        mProgress.setVisibility(View.INVISIBLE);
        mTopNewsAdapter.addItems(newsList.getNewsList());
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
        DaggerTopNewsComponent.builder().appComponent(appComponent).topNewsModule(new TopNewsModule(this)).build().inject(this);
    }


    @Override
    public void showError(String error) {
        if (recycle != null) {
            Snackbar.make(recycle, getString(R.string.snack_infor), Snackbar.LENGTH_SHORT).setAction("重试", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mPresenter.getNewsList(currentIndex,true);
                }
            }).show();

        }
    }
}