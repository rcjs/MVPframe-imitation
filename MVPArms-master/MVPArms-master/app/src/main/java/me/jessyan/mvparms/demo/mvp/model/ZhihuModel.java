package me.jessyan.mvparms.demo.mvp.model;

import com.jess.arms.mvp.BaseModel;

import me.jessyan.mvparms.demo.app.api.Api;
import me.jessyan.mvparms.demo.app.api.cache.CacheManager;
import me.jessyan.mvparms.demo.app.api.service.ServiceManager;
import me.jessyan.mvparms.demo.app.entity.zhihu.ZhihuDaily;
import me.jessyan.mvparms.demo.mvp.contract.ZhihuContract;
import rx.Observable;

/**
 * Created by zjl on 2016/12/27.
 */

public class ZhihuModel extends BaseModel<ServiceManager, CacheManager> implements ZhihuContract.Model {
    public ZhihuModel(ServiceManager serviceManager, CacheManager cacheManager) {
        super(serviceManager, cacheManager);
    }

    @Override
    public Observable<ZhihuDaily> getLastDaily() {
        return  mServiceManager.getZhihuApi().getLastDaily();
    }

    @Override
    public Observable<ZhihuDaily> getTheDaily(String date) {
        return mServiceManager.getZhihuApi().getTheDaily(date);
    }

}
