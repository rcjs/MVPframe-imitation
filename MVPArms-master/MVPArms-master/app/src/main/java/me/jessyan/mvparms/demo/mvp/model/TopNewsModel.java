package me.jessyan.mvparms.demo.mvp.model;

import com.jess.arms.mvp.BaseModel;

import me.jessyan.mvparms.demo.app.api.Api;
import me.jessyan.mvparms.demo.app.api.cache.CacheManager;
import me.jessyan.mvparms.demo.app.api.service.ServiceManager;
import me.jessyan.mvparms.demo.app.entity.news.NewsList;
import me.jessyan.mvparms.demo.app.entity.zhihu.ZhihuDaily;
import me.jessyan.mvparms.demo.mvp.contract.TopNewsContract;
import me.jessyan.mvparms.demo.mvp.contract.ZhihuContract;
import rx.Observable;

/**
 * Created by zjl on 2016/12/27.
 */

public class TopNewsModel extends BaseModel<ServiceManager, CacheManager> implements TopNewsContract.Model {
    public TopNewsModel(ServiceManager serviceManager, CacheManager cacheManager) {
        super(serviceManager, cacheManager);
    }


    @Override
    public Observable<NewsList> getNews(int id) {
        return  mServiceManager.getTopNewsApi().getNews(id);
    }

    @Override
    public Observable<String> getNewsDetail(String id) {
        return  mServiceManager.getTopNewsApi().getNewsDetail(id);
    }
}
