package me.jessyan.mvparms.demo.mvp.model;

import com.jess.arms.mvp.BaseModel;

import me.jessyan.mvparms.demo.app.api.cache.CacheManager;
import me.jessyan.mvparms.demo.app.api.service.ServiceManager;
import me.jessyan.mvparms.demo.app.entity.zhihu.ZhihuStory;
import me.jessyan.mvparms.demo.mvp.contract.TopNewsDescribeContract;
import me.jessyan.mvparms.demo.mvp.contract.ZhihuDescribeContract;
import rx.Observable;

/**
 * Created by zjl on 2016/12/27.
 */

public class TopNewsDescribeModel extends BaseModel<ServiceManager, CacheManager> implements TopNewsDescribeContract.Model {
    public TopNewsDescribeModel(ServiceManager serviceManager, CacheManager cacheManager) {
        super(serviceManager, cacheManager);
    }

}