package me.jessyan.mvparms.demo.mvp.model;

import com.jess.arms.mvp.BaseModel;

import me.jessyan.mvparms.demo.app.api.Api;
import me.jessyan.mvparms.demo.app.api.cache.CacheManager;
import me.jessyan.mvparms.demo.app.api.service.ServiceManager;
import me.jessyan.mvparms.demo.app.entity.zhihu.ZhihuStory;
import me.jessyan.mvparms.demo.mvp.contract.ZhihuDescribeContract;
import rx.Observable;

/**
 * Created by zjl on 2016/12/27.
 */

public class ZhihuDescribeModel extends BaseModel<ServiceManager, CacheManager> implements ZhihuDescribeContract.Model {
    public ZhihuDescribeModel(ServiceManager serviceManager, CacheManager cacheManager) {
        super(serviceManager, cacheManager);
    }

    @Override
    public Observable<ZhihuStory> getZhihuStory(String id) {
        return mServiceManager.getZhihuApi().getZhihuStory(id);
    }
}