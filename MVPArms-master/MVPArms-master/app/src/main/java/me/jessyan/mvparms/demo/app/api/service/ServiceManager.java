package me.jessyan.mvparms.demo.app.api.service;

import com.jess.arms.http.BaseServiceManager;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by jess on 8/5/16 13:01
 * contact with jess.yan.effort@gmail.com
 */
@Singleton
public class ServiceManager implements BaseServiceManager {
    private CommonService mCommonService;
    private UserService mUserService;
    private GankApi mGankApi;
    private ZhihuApi mZhihuApi;
    private TopNewsApi mTopNewsApi;

    /**
     * 如果需要添加service只需在构造方法中添加对应的service,在提供get方法返回出去,只要在ServiceModule提供了该service
     * Dagger2会自行注入
     *
     * @param commonService
     */
    @Inject
    public ServiceManager(CommonService commonService, UserService userService, GankApi mGankApi, ZhihuApi mZhihuApi, TopNewsApi mTopNewsApi) {
        this.mCommonService = commonService;
        this.mUserService = userService;
        this.mGankApi = mGankApi;
        this.mZhihuApi = mZhihuApi;
        this.mTopNewsApi = mTopNewsApi;
    }

    public CommonService getCommonService() {
        return mCommonService;
    }

    public UserService getUserService() {
        return mUserService;
    }

    public GankApi getGankApi() {
        return mGankApi;
    }

    public ZhihuApi getZhihuApi() {
        return mZhihuApi;
    }

    public TopNewsApi getTopNewsApi() {
        return mTopNewsApi;
    }

    /**
     * 这里可以释放一些资源(注意这里是单例，即不需要在activity的生命周期调用)
     */
    @Override
    public void onDestory() {

    }
}
