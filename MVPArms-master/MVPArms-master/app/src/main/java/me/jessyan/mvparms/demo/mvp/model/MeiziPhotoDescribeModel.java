package me.jessyan.mvparms.demo.mvp.model;

import com.jess.arms.mvp.BaseModel;

import me.jessyan.mvparms.demo.app.api.cache.CacheManager;
import me.jessyan.mvparms.demo.app.api.service.ServiceManager;
import me.jessyan.mvparms.demo.mvp.contract.MeiziContract;
import me.jessyan.mvparms.demo.mvp.contract.MeiziPhotoDescribeContract;

/**
 * Created by zjl on 2016/12/27.
 */

public class MeiziPhotoDescribeModel extends BaseModel<ServiceManager, CacheManager> implements MeiziPhotoDescribeContract.Model {
    public MeiziPhotoDescribeModel(ServiceManager serviceManager, CacheManager cacheManager) {
        super(serviceManager, cacheManager);
    }
}
