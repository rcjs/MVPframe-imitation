package me.jessyan.mvparms.demo.di.module;

import com.jess.arms.di.scope.ActivityScope;

import dagger.Module;
import dagger.Provides;
import me.jessyan.mvparms.demo.app.api.cache.CacheManager;
import me.jessyan.mvparms.demo.app.api.service.ServiceManager;
import me.jessyan.mvparms.demo.mvp.contract.MainContract;
import me.jessyan.mvparms.demo.mvp.contract.ZhihuContract;
import me.jessyan.mvparms.demo.mvp.model.MainModel;
import me.jessyan.mvparms.demo.mvp.model.ZhihuModel;

/**
 * Created by zjl on 2016/12/27.
 */
@Module
public class ZhihuModule {
    private ZhihuContract.View view;

    /**
     * 构建UserModule时,将View的实现类传进来,这样就可以提供View的实现类给presenter
     * @param view
     */
    public ZhihuModule(ZhihuContract.View view) {
        this.view = view;
    }

    @ActivityScope
    @Provides
    ZhihuContract.View provideZhihuView(){
        return this.view;
    }

    @ActivityScope
    @Provides
    ZhihuContract.Model provideZhihuModel(ServiceManager serviceManager, CacheManager cacheManager){
        return new ZhihuModel(serviceManager,cacheManager);
    }
}
