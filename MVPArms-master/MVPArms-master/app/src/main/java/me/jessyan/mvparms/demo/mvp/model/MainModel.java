package me.jessyan.mvparms.demo.mvp.model;

import com.jess.arms.mvp.BaseModel;

import me.jessyan.mvparms.demo.app.api.cache.CacheManager;
import me.jessyan.mvparms.demo.app.api.service.ServiceManager;
import me.jessyan.mvparms.demo.mvp.contract.MainContract;

/**
 * Created by zjl on 2016/12/26.
 */

public class MainModel extends BaseModel<ServiceManager, CacheManager> implements MainContract.Model {
    public MainModel(ServiceManager serviceManager, CacheManager cacheManager) {
        super(serviceManager, cacheManager);
    }
}
