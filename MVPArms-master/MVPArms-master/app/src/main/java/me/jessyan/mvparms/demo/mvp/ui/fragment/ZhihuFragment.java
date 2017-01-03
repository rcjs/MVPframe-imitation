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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewStub;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.jess.arms.utils.UiUtils;

import butterknife.BindView;
import me.jessyan.mvparms.demo.R;
import me.jessyan.mvparms.demo.app.entity.zhihu.ZhihuDaily;
import me.jessyan.mvparms.demo.di.component.AppComponent;
import me.jessyan.mvparms.demo.di.component.DaggerMeiziComponent;
import me.jessyan.mvparms.demo.di.component.DaggerZhihuComponent;
import me.jessyan.mvparms.demo.di.module.MeiziModule;
import me.jessyan.mvparms.demo.di.module.ZhihuModule;
import me.jessyan.mvparms.demo.mvp.contract.ZhihuContract;
import me.jessyan.mvparms.demo.mvp.presenter.ZhihuPresenter;
import me.jessyan.mvparms.demo.mvp.ui.adapter.ZhihuAdapter;
import me.jessyan.mvparms.demo.mvp.ui.common.WEFragment;
import me.jessyan.mvparms.demo.widget.GridItemDividerDecoration;
import me.jessyan.mvparms.demo.widget.WrapContentLinearLayoutManager;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import timber.log.Timber;

/**
 * Created by zjl on 2016/12/23.
 */

public class ZhihuFragment extends WEFragment<ZhihuPresenter> implements ZhihuContract.View {

    TextView noConnectionText;
    View view = null;
    boolean connected = true;
    boolean monitoringConnectivity;
    @BindView(R.id.prograss)
    ProgressBar mProgress;
    LinearLayoutManager mLinearLayoutManager;
    RecyclerView.OnScrollListener loadingMoreListener;

    @BindView(R.id.recycle_zhihu)
    RecyclerView recycle;
    private String currentLoadDate;
    private ConnectivityManager.NetworkCallback connectivityCallback;
    boolean loading;
    ZhihuAdapter zhihuAdapter;
    private int index = 1;
    private boolean isLoadingMore;
    @Override
    protected View initView() {
        return LayoutInflater.from(getContext()).inflate(R.layout.zhihu_fragment_layout, null, false);
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
        recycle.setAdapter(zhihuAdapter);
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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            connectivityCallback = new ConnectivityManager.NetworkCallback() {
                @Override
                public void onAvailable(Network network) {
                    connected = true;
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            noConnectionText.setVisibility(View.GONE);
                            loadDate();
                        }
                    });
                }

                @Override
                public void onLost(Network network) {
                    connected = false;
                }
            };
        }

    }

    private void loadMoreDate() {
        zhihuAdapter.loadingStart();
        mPresenter.getTheDaily(currentLoadDate,true);
    }

    @Override
    protected void initData() {
        checkConnectivity(view);
        initialDate();
        initialView();
    }

    //Android 判断wifi和4G网络是否开启
    private void checkConnectivity(View view) {
        final ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        connected = activeNetworkInfo != null && activeNetworkInfo.isConnected();
        if (!connected && mProgress != null) {
            // 方式2，获取ViewStub,
            ViewStub stub_text = (ViewStub) view.findViewById(R.id.stub_no_connection_text);
            // 加载布局, 并且获取控件,inflate函数直接返回TextView对象
            noConnectionText = (TextView) stub_text.inflate();

            //在wifi开启时，强制通过手机网络发送请求
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                connectivityManager.registerNetworkCallback(
                        new NetworkRequest.Builder()
                                .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET).build(), connectivityCallback);

                monitoringConnectivity = true;
            }
        }

    }

    private void loadDate() {
        if (zhihuAdapter.getItemCount() > 0) {
            zhihuAdapter.clearData();
        }
        currentLoadDate = "0";
        mPresenter.getLastZhihuNews(true);//打开app时自动加载列表
    }

    private void initialDate() {
        zhihuAdapter = new ZhihuAdapter(getContext());
    }

    @Override
    public void updateList(ZhihuDaily zhihuDaily) {
        if (loading) {
            loading = false;
            zhihuAdapter.loadingfinish();
        }
        currentLoadDate = zhihuDaily.getDate();
        zhihuAdapter.addItems(zhihuDaily.getStories());
        if (!recycle.canScrollVertically(View.SCROLL_INDICATOR_BOTTOM)) {
            loadMoreDate();
        }
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
        Log.d("zdf222",message);
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
        DaggerZhihuComponent.builder().appComponent(appComponent).zhihuModule(new ZhihuModule(this)).build().inject(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (monitoringConnectivity) {
            final ConnectivityManager connectivityManager
                    = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                connectivityManager.unregisterNetworkCallback(connectivityCallback);
            }
            monitoringConnectivity = false;
        }
    }

    @Override
    public void showError(String error) {
        Log.d("zdf",error);
        if (recycle != null) {
            Log.d("zdf3333",error);
            Snackbar.make(recycle, getString(R.string.snack_infor), Snackbar.LENGTH_SHORT).setAction("重试", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (currentLoadDate.equals("0")) {
                        mPresenter.getLastZhihuNews(true);
                    } else {
                        mPresenter.getTheDaily(currentLoadDate,true);
                    }
                }
            }).show();

        }
    }
}
